package com.cf;

import com.cf.cache.service.impl.AopCacheTestServcie;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/3/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes={CacheApplication.class})// 指定启动类
@Slf4j
public class ApplicationTests {
    @Autowired
    AopCacheTestServcie aopCacheTestServcie;

    @Test
    public void testOne(){
        //String result =aopCacheTestServcie.getUser("ad",22);
        Map resultMap =aopCacheTestServcie.getMapUser();
        System.out.println(resultMap.size());
       // log.info(result);
     //  Object ob = cacheableService.getCacheBenService("AopCacheTestServcie",aopCacheTestServcie.getClass());
       // ArrayListMultimap<String, CacheTasks> list = ClassesWithAnnotationFromPackage.getCacheTasksWithAnnotationFromPackage("com.cf.cache.impl");

    }

    @Before
    public void testBefore(){
        System.out.println("before");
    }

    @After
    public void testAfter(){
        System.out.println("after");
    }
}

