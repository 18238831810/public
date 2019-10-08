package com.cf.cache.service.impl;

import com.cf.cache.service.ICache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * redis缓存基础类
 * 
 * @author denghui
 * 
 * @param
 */
@Service
public  class RedisCacheService implements ICache {
	public Logger log = LogManager.getLogger(getClass());
	@Autowired
	protected RedisTemplate<String, Object> redisTemplate;

	public boolean setCache(final String key, final Object value) {
		try {
			redisTemplate.boundValueOps(key).set(value);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}
		return true;
	}


	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/*
	 * 向redis里面添加key-value格式的数据
	 * 
	 * @param key key
	 * 
	 * @param value value
	 * 
	 * @param offset 存活时间
	 */
	@Override
	public boolean setCache(final String key, final Object value,
			final long expireTime) {
		try {
			redisTemplate.boundValueOps(key).set(value, expireTime,TimeUnit.SECONDS);
		} catch (Exception e) {
			log.error(e.toString(), e);
			return false;
		}
		return true;
	}

	@Override
	public Object getCache(String key) {
		Object o = null;
		try {
			o = redisTemplate.boundValueOps(key).get();
		} catch (Exception e) {
			log.info("get【{}】 errormssage【{}】", key, e.getMessage());
		}
		return o;
	}

	
	public boolean deleteCache(String key) {
		if (redisTemplate.hasKey(key)) {
			return redisTemplate.expire(key, -1, TimeUnit.MICROSECONDS);
		}
		return false;
	}


}
