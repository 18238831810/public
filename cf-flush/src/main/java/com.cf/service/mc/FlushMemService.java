package com.cf.service.mc;

import com.cf.cache.service.impl.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 
 * Copyright (c) 2017
 * @ClassName:     FlushMemService.java
 * @Description:   操作缓存数据
 * 
 * @author:        hui
 * @version:       V1.0  
 * @Date:           2017年6月28日 上午10:17:50
 */
@Service
@Slf4j
public class FlushMemService {

	@Autowired
	RedisCacheService redisCacheService;

	public Object getRedisValue(String key) {
		Object result = null;
		DataType dt = redisCacheService.getRedisTemplate().type(key);
		log.info("key【{}】 the type is【{}】", key, dt.name().toLowerCase());
		long time = redisCacheService.getRedisTemplate().getExpire(key);
		if ("string".equals(dt.name().toLowerCase())) {
			Object ob = redisCacheService.getCache(key);
			String temp = "";
			if (ob instanceof String || ob instanceof Long
					|| ob instanceof Integer || ob instanceof Double
					|| ob instanceof Float) {
				temp = ob.toString();
			} else if (ob instanceof ArrayList) {
				temp = JSONArray.fromObject(ob).toString();
			} else
				temp = JSONObject.fromObject(ob).toString();
			return temp
					+ "\r\n"
					+ "<font color='red'>还剩："
					+ DurationFormatUtils.formatDuration(time * 1000,
							"HH小时mm分ss秒") + "过期</font>";
		} else if ("set".equals(dt.name().toLowerCase())) {
			result = redisCacheService.getRedisTemplate().boundSetOps(key)
					.members();
			return JSONArray.fromObject(result).toString()
					+ "\r\n"
					+ "<font color='red'>还剩："
					+ DurationFormatUtils.formatDuration(time * 1000,
							"HH小时mm分ss秒") + "分钟过期</font>";
		} else if ("list".equals(dt.name().toLowerCase())) {
			result = redisCacheService.getRedisTemplate().boundListOps(key)
					.range(0, -1);
			return JSONArray.fromObject(result).toString()
					+ "\r\n"
					+ "<font color='red'>还剩："
					+ DurationFormatUtils.formatDuration(time * 1000,
							"HH小时mm分ss秒") + "分钟过期</font>";
		} else if ("zset".equals(dt.name().toLowerCase())) {
			result = redisCacheService.getRedisTemplate().boundZSetOps(key)
					.range(0, -1);
			BoundZSetOperations<String, Object> zset = redisCacheService
					.getRedisTemplate().boundZSetOps(key);
			Set<ZSetOperations.TypedTuple<Object>> set = zset
					.reverseRangeWithScores(0, -1);
			return JSONArray.fromObject(set).toString()
					+ "\r\n"
					+ "<font color='red'>还剩："
					+ DurationFormatUtils.formatDuration(time * 1000,
							"HH小时mm分ss秒") + "分钟过期</font>";
		} else if ("hash".equals(dt.name().toLowerCase())) {
			List<?> list = redisCacheService.getRedisTemplate()
					.boundHashOps(key).values();
			Set<?> list2 = redisCacheService.getRedisTemplate()
					.boundHashOps(key).keys();
			Object[] ob = new Object[2];
			ob[0] = list;
			ob[1] = list2;
			result = ob;
			// return JSONArray.fromObject(result).toString();
			return JSONArray.fromObject(result).toString()
					+ "\r\n"
					+ "<font color='red'>还剩："
					+ DurationFormatUtils.formatDuration(time * 1000,
							"HH小时mm分ss秒") + "分钟过期</font>";
		} else
			log.info("key【{}】 the type is not fond");

		return null;

	}

	public Object getRedisKey(String pattern, Integer page) {

		int curPage = (page == null || page == 0) ? 1 : page;
		Set<String> set = new HashSet<String>();
		if (StringUtils.isBlank(pattern))
			set = redisCacheService.getRedisTemplate().keys("*");
		else
			set = redisCacheService.getRedisTemplate().keys(pattern + "*");
		// 总条数
		int total = set.size();
		// 总页数
		int pageTotal = total / 20 + (total % 20 > 0 ? 1 : 0);

		if (pageTotal <= curPage && pageTotal > 0)
			curPage = pageTotal;

		int start = (curPage - 1) * 20, length = 20;
		if (curPage == pageTotal)
			length = total - ((curPage - 1) * 20);
		if (pageTotal == 0)
			length = 0;
		String b[] = null;
		Map<String, Object> map = new HashMap<String, Object>();
		if (length > 0) {
			b = new String[length];
			Set set1 = new TreeSet();
			set1.addAll(set);
			System.arraycopy(set1.toArray(), start, b, 0, length);
		}
		map.put("total", total);
		map.put("pageTotal", pageTotal);
		map.put("curPage", curPage);
		map.put("list", b);
		return map;
	}

	/**
	 * 根据key删除缓存
	 * 
	 * @param token
	 * @return
	 */
	public Object deleteBatchCache(String token) {
		RedisTemplate<String, Object> tedisTemplate= redisCacheService.getRedisTemplate();
		String attr[]= token.split(",");
		for (String string : attr) {
			if(StringUtils.isNoneBlank(string)) {
				//如果是手动删除的话，支持通配符的删除
				if(string.startsWith("del_keys_byhand:"))
				{
					string=string.replace("del_keys_byhand:","");
					log.info("del_keys_byhand->{}",string);
					tedisTemplate.delete(tedisTemplate.keys(string));
				}
				else tedisTemplate.delete(string);
			}
		}
		return "suc";
	}

}
