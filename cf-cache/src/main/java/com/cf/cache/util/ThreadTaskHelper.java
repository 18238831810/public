package com.cf.cache.util;

import com.google.common.collect.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 缓存线程帮助类，用于存线程执行过种的数据，同时存有需要定时刷新缓存的uuid对应的参数
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/8
 */
@Slf4j
public class ThreadTaskHelper  {


    //需要定时刷新缓存的参数
    private static Map<Method,ArrayListMultimap<String,Object[]>>  threadParamCached=null;

    /**
     * 设置需要定时刷新缓存的参数
     * 三维数组
     * @param method
     * @param params 参数
     */
    public static void putThreadParamCached(Method method, String cacheKey, Object[] params)
    {
        if(threadParamCached==null)
        {
            threadParamCached= new HashMap();//HashMultimap.create();
        }
        if(threadParamCached.containsKey(method))
        {
            ArrayListMultimap<String,Object[]> setMultimap= threadParamCached.get(method);
            if(setMultimap==null) setMultimap=ArrayListMultimap.create();
            if(!setMultimap.containsKey(cacheKey))
            {
                setMultimap.put(cacheKey,params);
                threadParamCached.put(method,setMultimap);
            }
        }
        else
        {
            ArrayListMultimap<String,Object[]> setMultimap= threadParamCached.get(method);
            if(setMultimap==null) setMultimap= ArrayListMultimap.create();
            setMultimap.put(cacheKey,params);
            threadParamCached.put(method,setMultimap);
        }
    }

    /**
     * 获取所有需要定时刷新缓存的参数
     * @param key
     * @return
     */
    public static Collection getThreadParamCached(Method key)
    {
        if(threadParamCached==null) return null;
        ArrayListMultimap<String,Object[]> array = threadParamCached.get(key);
        if(array==null) return null;
        return array.values();
    }

}
