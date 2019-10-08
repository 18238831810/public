package com.cf.crs.security.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisCache<K, V> implements Cache<K, V>, Serializable {

    private static final String REDIS_SHIRO_CACHE = "crs:shiro-cache:";
    private String cacheKey;
    private RedisTemplate<K, V> redisTemplate;
    private long expireTime = 7200;
    private String principalIdFieldName = "id";

    public RedisCache(String name, RedisTemplate redisTemplate, long expireTime) {
        this.cacheKey = REDIS_SHIRO_CACHE + name + ":";
        this.redisTemplate = redisTemplate;
        this.expireTime = expireTime;
    }

    @Override
    public V get(K key) throws CacheException {
//        System.out.println("key:" + key);
//        long startTime = System.currentTimeMillis();
        BoundValueOperations<K, V> operations = redisTemplate.boundValueOps(getCacheKey(key));
        //operations.expire(expireTime, TimeUnit.MINUTES);
        V v =operations.get();
//        long endTime = System.currentTimeMillis();
//        System.out.println("耗时：" + (endTime - startTime));
        return v;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        V old = get(key);
        redisTemplate.boundValueOps(getCacheKey(key)).set(value, expireTime, TimeUnit.MINUTES);
        return old;
    }

    @Override
    public V remove(K key) throws CacheException {
        V old = get(key);
        redisTemplate.delete(getCacheKey(key));
        return old;
    }

    @Override
    public void clear() throws CacheException {
        redisTemplate.delete(keys());
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        return redisTemplate.keys(getCacheKey("*"));
    }

    @Override
    public Collection<V> values() {
        Set<K> set = keys();
        List<V> list = new ArrayList<>();
        for (K s : set) {
            list.add(get(s));
        }
        return list;
    }

    private K getCacheKey(Object k) {
//        if (k instanceof PrincipalCollection) {
//            PrincipalCollection key = (PrincipalCollection) k;
//            Object principalObject = key.getPrimaryPrincipal();
//            if (principalObject instanceof String) {
//                k = principalObject.toString();
//            } else {
//                k = getRedisKeyFromPrincipalIdField(key);
//            }
//        }

        K fk = (K) (this.cacheKey + k);
        return fk;
    }

    private String getRedisKeyFromPrincipalIdField(PrincipalCollection key) {
        Object principalObject = key.getPrimaryPrincipal();
        if (principalObject instanceof String) {
            return principalObject.toString();
        }
        Method pincipalIdGetter = getPrincipalIdGetter(principalObject);
        return getIdObj(principalObject, pincipalIdGetter);
    }

    private String getIdObj(Object principalObject, Method pincipalIdGetter) {
        String redisKey = "";
        try {
            Object idObj = pincipalIdGetter.invoke(principalObject);
            if (idObj != null) {
                redisKey = idObj.toString();
            }
        } catch (Exception e) {
            log.error("shiro getIdObj exception", e);
        }
        return redisKey;
    }

    private Method getPrincipalIdGetter(Object principalObject) {
        Method pincipalIdGetter = null;
        String principalIdMethodName = this.getPrincipalIdMethodName();
        try {
            pincipalIdGetter = principalObject.getClass().getMethod(principalIdMethodName);
        } catch (NoSuchMethodException e) {
            log.error("shiro getPrincipalIdGetter exception", e);
        }
        return pincipalIdGetter;
    }

    private String getPrincipalIdMethodName() {
        if (this.principalIdFieldName == null || "".equals(this.principalIdFieldName)) {
            log.error("shiro getPrincipalIdMethodName exception");
        }
        return "get" + this.principalIdFieldName.substring(0, 1).toUpperCase() + this.principalIdFieldName.substring(1);
    }


}
