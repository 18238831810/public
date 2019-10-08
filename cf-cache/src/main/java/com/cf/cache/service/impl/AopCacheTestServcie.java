package com.cf.cache.service.impl;

import com.cf.cache.aop.EnableCFCache;
import com.cf.cache.aop.EnableMapCFCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/4
 */
@Service
@Slf4j
public class AopCacheTestServcie {

    @EnableCFCache(prefix = "test:",fieldKey = "#id", expire = 360,fixed = 10,condition = "#age<20")
    public String getUser(String id,int age)
    {
        log.info("{}",id);
        return "hello,id="+id;
    }
    @EnableCFCache(prefix = "test:",expire = 360)
    public String getUser()
    {
        return "hello,id=hi";
    }

    @EnableCFCache(prefix = "test:",fieldKey = "#id",expire = 3600,fixed = 20)
    public String getFixUser(String id)
    {
        log.info("{}",id);
        return "hello,id="+id;
    }

    @EnableCFCache(prefix = "test:map2",expire = 360,fixed = 20,type =EnableCFCache.CacheType.MAP )
    @EnableMapCFCache(exitKeys = {"one"},exitValues = {"world"},exitKeyValues = "three=I",filterKeys = {"four","coming"})
    public Map getMapUser()
    {
       Map map = new HashMap();
        map.put("one","hello");
        map.put("tow","world");
        map.put("three","I");
        map.put("four","coming");
        return map;
    }
}
