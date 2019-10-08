package com.cf.cache.task;

import com.cf.cache.aop.EnableCFCache;
import com.cf.cache.vo.CacheTasks;
import com.cf.cache.util.BetweenTimeKey;
import com.cf.cache.util.ClassesWithAnnotationFromPackage;
import com.cf.cache.util.ThreadTaskHelper;
import com.google.common.collect.ArrayListMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 开户刷新缓存线程
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/11
 */
@Service()
@Slf4j
public class TaskThreadService {

    @Autowired
    WebApplicationContext context;
    @Autowired
    private RedisTemplate redisTemplate;

    private  Map taskExcuterTime= null;

    private ThreadPoolExecutor executorService= null;

    /**
     * 缓存刷新线程的入口
     */
    public void  init(String[] packageNames)
    {
        ArrayListMultimap<String, CacheTasks> multimap = ClassesWithAnnotationFromPackage.getCacheTasksWithAnnotationFromPackage(packageNames);
        if(multimap==null || multimap.size()<=0)
        {
            log.warn("TaskThreadService.init can not found any CacheTasks");
            return;
        }
        int size =multimap.keySet().size();
        log.warn("ThreadPoolExecutor.newFixedThreadPool.size ==={}",size);
        executorService = (ThreadPoolExecutor)Executors.newFixedThreadPool(size);
        runner(multimap);
    }
    public void  init(String packageName)
    {
        init(new String[]{packageName});
    }

    public void runner(ArrayListMultimap<String, CacheTasks> multimap)
    {
        multimap.asMap().forEach((key,collection)->{
            //线程归属于哪一个，用于控制线程跑完之后等待时长用
            if(key.startsWith("one"))
                executorService.execute(new Thread(()->taskTrigered(collection,BetweenTimeKey.getByKey("one").getInterval(),key)));
            else
                executorService.execute(new Thread(()->taskTrigered(collection,BetweenTimeKey.getByKey(key).getInterval(),key)));
        });

    }

    /**
     * 线程触发
     * @param cacheTasks
     * @param waitingTime
     * @param timeKey
     */
    private void taskTrigered(Collection<CacheTasks> cacheTasks, long waitingTime,String timeKey) {
        cacheTasks.forEach(cacheTask -> log.info("taskTrigered.start >>> {}.{} prexKey->{}",cacheTask.getSignClassName(),cacheTask.getMethod().getName(),cacheTask.getEnableCFCache().prefix()));
        while (true)
        {
            try {
                cacheTasks.forEach(cacheTask->{
                    if(acquire(timeKey))
                        taskTrigered(cacheTask);
                });

            }finally {
                release(timeKey);
                ClassesWithAnnotationFromPackage.waiting(waitingTime);
            }
        }
    }

    /**
     * 自动刷新缓存中的数据
     * @param cacheTasks
     */
    private void taskTrigered(CacheTasks cacheTasks){
        try
        {
            if (canDone(cacheTasks)) {
                if(cacheTasks.isNonArgs())
                {
                    taskTrigered( cacheTasks,null);
                }
                else
                {
                    //根据 uuid获取运行时的需要保存缓存的参数
                    Collection coll = ThreadTaskHelper.getThreadParamCached(cacheTasks.getMethod());
                    if (coll == null || coll.isEmpty()) return;
                    coll.forEach(item ->{taskTrigered(cacheTasks, (Object[]) item);});
                }
            }
        }catch (Exception e)
        {
            log.error("taskTrigered.error->{}",e.getMessage());
        }
    }

    /**
     * 判断是否到可以刷新缓存的时间了
     * @param cacheTasks
     * @return
     */
    private boolean canDone(CacheTasks cacheTasks)
    {
        if(taskExcuterTime==null) taskExcuterTime= new HashMap();
        Object exctime=taskExcuterTime.get(cacheTasks.getMethod());
        if(exctime==null) return true;
        //如果上次执行时间+定时时长都比当前 时间小，说明现在需要执行了
        if(cacheTasks.getEnableCFCache().fixed()*1000+Long.parseLong(exctime.toString())<System.currentTimeMillis())
            return true;
        return false;
    }

    /**
     * 刷新缓存
     * @param cacheTasks
     * @param args
     */
    private   void taskTrigered(CacheTasks cacheTasks,Object[] args) {

            try {
                Class _class = cacheTasks.getMethod().getDeclaringClass();
                Object ob = getCacheBenService(StringUtils.uncapitalize(_class.getSimpleName()), _class);
                setEnableCfCacheFlush2True( cacheTasks.getMethod()).invoke(ob, args);
                if(taskExcuterTime==null) taskExcuterTime= new HashMap();
                taskExcuterTime.put(cacheTasks.getMethod(),System.currentTimeMillis());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
    }

    /**
     * 通过反射，设置缓存注解类的flush值为true,这样让切面知道此次操作为刷新缓存
     * @param method
     * @return
     */
    private Method setEnableCfCacheFlush2True(Method method)
    {
        try {
            EnableCFCache annotation = method.getAnnotation(EnableCFCache.class);
            if (annotation != null){
                InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
                Field values = invocationHandler.getClass().getDeclaredField("memberValues");
                values.setAccessible(true);
                Map<String, Object> memberValues =(Map<String, Object>) values.get(invocationHandler);
                memberValues.put("flush", true);
            }
        } catch (Exception e) {
           log.error(e.getMessage());
        }
        return method;
    }

    /**
     * 获取Ben实体类
     * @param name
     * @param requiredType
     * @return
     */
    public Object getCacheBenService(String name,Class requiredType)
    {
        return  context.getBean(name, requiredType);
    }

    /**
     * 获取分布式锁
     * @param timeKey
     * @return
     */
    public boolean acquire(String timeKey)
    {
        return  redisTemplate.opsForValue().setIfAbsent(timeKey,System.currentTimeMillis(),10, TimeUnit.SECONDS);
    }

    /**
     * 释放分布锁
     * @param key
     */
    public void release(String key)
    {
        if(redisTemplate.hasKey(key))
            redisTemplate.delete(key);
    }

}
