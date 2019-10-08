package com.cf.cache.aop;

import com.cf.cache.util.ClassesWithAnnotationFromPackage;
import com.cf.cache.util.ThreadTaskHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * <p>Description:缓存拦截切面实现类 </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/3
 */

@Aspect
@Component
@Slf4j
public class CacheServiceAspect  implements Ordered {

    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.cf.cache.aop.EnableCFCache)")
    public void dealCacheServiceCut() {
    }


    @Around(value = "dealCacheServiceCut()")
    public Object dealCacheService(ProceedingJoinPoint point) throws Throwable {
        Object result=null;
        try {
            Method method = getMethod(point);
            // 获取注解对象
            EnableCFCache enableCFCache = method.getAnnotation(EnableCFCache.class);
            switch (enableCFCache.type().name())
            {
                case "STRING":
                    result=  processStringCache( enableCFCache, point,  method);
                    break;
                case "MAP":
                    result= processMapCache(enableCFCache, point,  method);
                    break;
                default:
                    result=  processStringCache( enableCFCache, point,  method);
                    break;
            }

        } catch (Exception e) {
            log.error("dealCacheService error,JoinPoint:{}", point.getSignature(), e);
        }
        if(result!=null) return result;
        return point.proceed();
    }

    /**
     * 处理Map结构类型的key->key->value
     * @param enableCFCache
     * @param point
     * @param method
     * @return
     * @throws Throwable
     */
    private Object processMapCache(EnableCFCache enableCFCache, ProceedingJoinPoint point, Method method) throws Throwable {
        Object[] args = point.getArgs();
        EnableCFCache.CacheOperation cacheOperation = enableCFCache.operation();
        String cacheKey = enableCFCache.prefix() ,
                fixKey =ClassesWithAnnotationFromPackage.parseKey(enableCFCache.fieldKey(), method, args);
        if (StringUtils.isEmpty(cacheKey)) {
            return point.proceed();
        }
        //对于参数进行相当的保存,如果是刷新操作，不需要存参数值
        if (cacheOperation == EnableCFCache.CacheOperation.QUERY && isSuitThePurpose(enableCFCache, point)) {
            if(enableCFCache.fixed()>0 &&  !enableCFCache.flush() )
            {
                ThreadTaskHelper.putThreadParamCached(method,cacheKey,args);
            }
            return processMapQuery(method,point, enableCFCache, cacheKey,fixKey);
        }
        if (cacheOperation == EnableCFCache.CacheOperation.UPDATE || cacheOperation == EnableCFCache.CacheOperation.DELETE) {
            return processUpdateAndDelete(point, cacheKey);
        }
        return point.proceed();
    }

    /**
     * 设置或取Map类型的key->key->value
     * @param point
     * @param enableCFCache
     * @param cacheKey
     * @param fixKey
     * @return
     * @throws Throwable
     */
    private Object processMapQuery( Method method,ProceedingJoinPoint point, EnableCFCache enableCFCache, String cacheKey, String fixKey) throws Throwable {
        if(!enableCFCache.flush())
        {
            Object object=  getMapCacheValue( cacheKey,  fixKey);
            //如果缓存有值则直接取。否则再继续走逻辑
            if(object !=null) return object;
        }
        if (point == null) return null;
        return processMapPoceed( method,point, cacheKey,  fixKey);
    }

    /**
     * 从缓存中获取Map类型的值
     * @param cacheKey
     * @param fixKey
     * @return
     */
    private Object getMapCacheValue(String cacheKey, String fixKey)
    {
        Object result;
        if(StringUtils.isBlank(fixKey))
            result=redisTemplate.boundHashOps(cacheKey).entries();
        else  result=redisTemplate.boundHashOps(cacheKey).get(fixKey);
        if(result!=null && result instanceof Map)
        {
            if(!((Map) result).isEmpty()) return result;
        }
        return null;
    }
  private  Object processMapPoceed(Method method,ProceedingJoinPoint point,String cacheKey, String fixKey) throws Throwable {
        Object  result = point.proceed();
        if(result instanceof Map)
        {
            Map map =(Map)result;
            //如果第二个key为空，则取全部。如果不为空，则根据key值取map中的值
            //如果在用户返回时已经指定不需要缓存的情况
            if(!map.isEmpty()) cacheMapValue(method, map,cacheKey);
            if(StringUtils.isBlank(fixKey)) return map;
            return map.get(fixKey);
        }
        return result;
    }

    /**
     * 对注解EnableMapCFCache做相关处理
     * @param method
     * @param map
     * @param cacheKey
     */
    private void  cacheMapValue(Method method,Map map,String cacheKey)
    {
        EnableMapCFCache enableMapCFCache = method.getAnnotation(EnableMapCFCache.class);
        if(enableMapCFCache!=null) {
            //如若包含指定的key，则不缓存
            String[] keys = enableMapCFCache.exitKeys();
            if (keys != null && keys.length > 0) {
                for (String key : keys) {
                    if(map.containsKey(key)) return;
                }
            }
            //如若包含指定的value，则不缓存
            keys = enableMapCFCache.exitValues();
            if (keys != null && keys.length > 0) {
                for (String key : keys) {
                    if(map.containsValue(key)) return;
                }
            }

            //去掉不要缓存的key
            keys = enableMapCFCache.exitKeyValues();
            if (keys != null && keys.length > 0) {
                for (String key : keys) {
                    String[] keyValue=StringUtils.split(key,"=");
                    if(keyValue.length!=2) continue;
                    if(keyValue[1].equals(map.get(key)))
                        map.remove(key);
                }
            }

            //去掉不要缓存的key
            keys = enableMapCFCache.filterKeys();
            if (keys != null && keys.length > 0) {
                for (String key : keys) {
                    map.remove(key);
                }
            }
        }

        //过滤新的map中没有，但是cache中有的key
       clearNotUseedCacheKeys( cacheKey,  map);

        //设置过期时间
        if(redisTemplate.hasKey(cacheKey))
        {
            EnableCFCache enableCFCache= method.getAnnotation(EnableCFCache.class);
            redisTemplate.expire(cacheKey,enableCFCache.expire(),TimeUnit.SECONDS);
        }
    }

    /**
     * 去掉缓存中有，但是新拉取的数据却没有的记录，同时将记录存入redis中
     * @param cacheKey
     * @param map
     */
    private  void clearNotUseedCacheKeys(String cacheKey, Map map)
    {
        BoundHashOperations<String, String, Object> boundHashOperations= redisTemplate.boundHashOps(cacheKey);
        Set<String> cacheMapKeys =  boundHashOperations.keys();
        if(cacheMapKeys!=null && !cacheMapKeys.isEmpty())
        {
            //缓存中的所有的key
            Iterator iterator=  cacheMapKeys.iterator();
            Set originKeys = map.keySet();
            if(originKeys!=null && !originKeys.isEmpty())
            {
                while (iterator.hasNext())
                {
                    //如若源数据源不包含缓存中的key，那说明此key是已经不用了的。则直接从redis缓存中将此key干掉
                    if(!originKeys.contains(iterator.next()))
                    {
                        boundHashOperations.delete();
                    }
                }
            }
        }
        boundHashOperations.putAll(map);
    }

    /**
     * String 类型的key->value
     * @param enableCFCache
     * @param point
     * @param method
     * @return
     * @throws Throwable
     */
    private  Object  processStringCache(EnableCFCache enableCFCache,ProceedingJoinPoint point, Method method) throws Throwable
    {
        Object[] args = point.getArgs();
        String cacheKey = enableCFCache.prefix() + ClassesWithAnnotationFromPackage.parseKey(enableCFCache.fieldKey(), method, args);
        log.info("{} enable cache service,cacheKey:{}", point.getSignature(), cacheKey);
        if (StringUtils.isEmpty(cacheKey)) {
            return point.proceed();
        }
        EnableCFCache.CacheOperation cacheOperation = enableCFCache.operation();
        //对于参数进行相当的保存,如果是刷新操作，不需要存参数值
        if (cacheOperation == EnableCFCache.CacheOperation.QUERY && isSuitThePurpose(enableCFCache, point)) {
            if(enableCFCache.fixed()>0 &&  !enableCFCache.flush() )
            {
                ThreadTaskHelper.putThreadParamCached(method,cacheKey,args);
            }
            return processQuery(point, enableCFCache, cacheKey);
        }
        if (cacheOperation == EnableCFCache.CacheOperation.UPDATE || cacheOperation == EnableCFCache.CacheOperation.DELETE) {
            return processUpdateAndDelete(point, cacheKey);
        }
        return point.proceed();
    }
    /**
     * 对condition条件的判定，主要过滤不符合条件的不走缓存。
     * @param enableCFCache
     * @param joinPoint
     * @return
     */
    private boolean isSuitThePurpose(EnableCFCache enableCFCache,ProceedingJoinPoint joinPoint) {
        if(StringUtils.isBlank(enableCFCache.condition())) return true;
        StandardEvaluationContext standardEvaluationContext =new StandardEvaluationContext(joinPoint.getArgs());
        standardEvaluationContext = setContextVariables(standardEvaluationContext,joinPoint);

        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression(enableCFCache.condition());
        Boolean condition =exp.getValue(standardEvaluationContext,Boolean.class);
        return condition.booleanValue();
    }

    /**
     * 对上下文参数赋值
     * @param standardEvaluationContext
     * @param joinPoint
     * @return
     */
    private     StandardEvaluationContext setContextVariables(StandardEvaluationContext standardEvaluationContext,ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        String[] parametersName =parameterNameDiscoverer.getParameterNames(targetMethod);
        if(args ==null|| args.length<=0) {
            return standardEvaluationContext;
        }
        for(int i =0;i < args.length;i++) {
            standardEvaluationContext.setVariable(parametersName[i],args[i]);
        }
        return standardEvaluationContext;
    }
    /**
     * 查询处理
     */
    private Object processQuery(ProceedingJoinPoint point, EnableCFCache enableCFCache, String cacheKey ) throws Throwable {
        Object result;
        boolean flush =false;
        RequestAttributes requestAttributes= RequestContextHolder.getRequestAttributes();
        if(requestAttributes==null) flush=true;
        if (!flush) {
            result = getCache(cacheKey);
            if (result != null) return result;
        }
        if (point == null) return null;
        result = point.proceed();
        return setCache(cacheKey, result, enableCFCache);
    }




    /**
     * 删除和修改处理
     */
    private Object processUpdateAndDelete(ProceedingJoinPoint point, String cacheKey)
            throws Throwable {
        //通常来讲,数据库update操作后,只需删除掉原来在缓存中的数据,下次查询时就会刷新
        try {
            return point.proceed();
        } finally {
            redisTemplate.delete(cacheKey);
        }
    }


    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method;
    }

    public  Object getCache(String cacheKey)
    {
        //如果要求不重新拉取数据，则直接从缓存中拉取
        if (redisTemplate.hasKey(cacheKey)) {
            return  redisTemplate.opsForValue().get(cacheKey);
        }
        return null;
    }

    public  Object setCache(String cacheKey,Object result,EnableCFCache enableCFCache )
    {
        //对非null
        if (result != null) {
            if (result instanceof String && StringUtils.isBlank(result.toString()) || (result instanceof Iterator && !((Iterator) result).hasNext())) {
                log.info("result is null,redisKey:{} can not be cached ", cacheKey);
            } else if (StringUtils.isNotBlank(cacheKey)) {
                redisTemplate.opsForValue().setIfAbsent(cacheKey,result,enableCFCache.expire(), TimeUnit.SECONDS);
            }
        }
        return result;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
