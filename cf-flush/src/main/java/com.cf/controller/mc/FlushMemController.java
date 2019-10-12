package com.cf.controller.mc;

import com.cf.anno.Tourist;
import com.cf.cache.service.impl.RedisCacheService;
import com.cf.service.mc.FlushMemService;
import com.cf.util.http.HttpWebResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/***
 *
 * Copyright (c) 2017
 * @ClassName: FlushMemController.java
 * @Description: 刷新缓存操作类
 *
 * @author: hui
 * @version: V1.0
 * @Date: 2017年6月28日 上午10:46:05
 */
@Tourist(is = true)
@RestController
@RequestMapping("/public/mc")
public class FlushMemController{
    @Autowired
    FlushMemService flushMemService;

    @Autowired
    RedisCacheService redisCacheService;


    private final static String SIGN = "cf20190701";

    /**
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param sign
     * @param: @return
     * @return: boolean
     */
    private boolean hasRight(String sign) {
        if (!SIGN.equals(sign))
            return false;
        return true;
    }


    /**
     * 根据key删除数据
     */
    @RequestMapping("/deleterediskey")
    public Object deleteRediskey(String token, String sign) {
        if (!hasRight(sign)) return HttpWebResult.getMonoError("没权限");
        if (StringUtils.isBlank(token)) return HttpWebResult.getMonoError("token is null");
        return HttpWebResult.getMonoSucResult(flushMemService.deleteBatchCache(token));
    }

    /**
     * 获取缓存的数据
     */
    @RequestMapping("/redis")
    public Object redis(String key, String sign) {
        if (!hasRight(sign)) return HttpWebResult.getMonoError("没权限");
        if (StringUtils.isBlank(key)) return HttpWebResult.getMonoError("key is null");
        return flushMemService.getRedisValue(key);
    }

    /**
     * 获取缓存的数据
     */
    @RequestMapping("/rediskey")
    public Object redisKey(String pattern, Integer page, String sign) {
        if (!hasRight(sign)) return HttpWebResult.getMonoError("没权限");
        return flushMemService.getRedisKey(pattern, page);
    }


}
