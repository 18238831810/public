/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs;

import com.cf.AdminApplication;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.sys.entity.SysUserEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class RedisTest {
    @Autowired
    private RedisUtils redisUtils;

    @Test
    public void contextLoads() {
        SysUserEntity user = new SysUserEntity();
        user.setEmail("123456@qq.com");
        redisUtils.set("user", user);

        System.out.println(ToStringBuilder.reflectionToString(redisUtils.get("user")));
    }


    @Test
    public void testRedis() {
        String key = "test_key";
        redisUtils.set(key, 123, 50);
        long startTime = System.currentTimeMillis();
        redisUtils.get(key);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);

    }



}