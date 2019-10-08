package com.cf.crs.security.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.LifecycleUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RedisCacheManager implements CacheManager {

    private ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();

    private static int expireTime = 7200;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        Cache<K,V> cache = caches.get(name);
        if(cache == null){
            cache = new RedisCache<>(name, redisTemplate, expireTime);
            caches.put(name, cache);
        }
        return cache;
    }

}
