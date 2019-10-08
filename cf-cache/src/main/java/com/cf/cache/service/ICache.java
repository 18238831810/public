package com.cf.cache.service;

public interface ICache {
	/**
	 * 删除缓存
	 * 
	 * @param key
	 * @return
	 */
	boolean deleteCache(String key);

	/**
	 * 设置缓存
	 * 
	 * @param key
	 * @param value
	 * @param expire
	 * @return
	 */
	boolean setCache(String key, Object value, long expire);

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */

	Object getCache(String key);

}
