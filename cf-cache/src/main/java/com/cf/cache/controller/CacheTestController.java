package com.cf.cache.controller;

import com.cf.cache.aop.EnableCFCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/5/5
 */
@RestController
@RequestMapping("/cache")
public class CacheTestController {

    @EnableCFCache(prefix="inex:test")
    @GetMapping("/index")
    public Object index()
    {
        String hello="hello";
        return hello;
    }
}
