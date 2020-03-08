/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.AdminApplication;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.service.*;
import com.cf.crs.sys.entity.SysUserEntity;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class RedisTest {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CheckSqlService checkSqlService;

    @Autowired
    CheckObjectAnalyService checkObjectAnalyService;

    @Autowired
    SynUserService synUserService;

    @Autowired
    CheckWarningHistoryService checkWarningHistoryService;

    @Autowired
    CheckAvailaHistoryService checkAvailaHistoryService;

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

    @Test
    public void sendEmail() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.163.com");
        sender.setDefaultEncoding("utf-8");
        sender.setUsername("18238831810");
        sender.setPassword("han18238831810");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("18238831810@163.com");
        message.setTo("1527583922@qq.com");
        message.setSubject("邪客发送文本邮件测试");// 标题
        message.setText("世界，你好！--->文本邮件");// 内容
        try {
            sender.send(message);
            log.info("文本邮件发送成功！");
        } catch (Exception e) {
            log.error("文本邮件发送异常！", e);
        }
    }

    @Test
    public void setCheckSqlService() {
        ResultJson<List<JSONObject>> checkSqlList = checkSqlService.getCheckList(2,null);
        System.out.println(JSONArray.toJSON(checkSqlList.getData()));
    }

    @Autowired
    WarningService warningService;

    @Autowired
    ClientLoginService clientLoginService;

    @Autowired
    CheckReportService checkReportService;

    @Test
    public void getServerList() {
        ResultJson json = warningService.analyWaring();
        System.out.println(json.getData());
    }
    @Test
    public void getCheckObjectAnalyResult() {
        List<Object> checkObjectAnalyResult = checkObjectAnalyService.getCheckObjectAnalyResult();
        System.out.println(JSON.toJSONString(checkObjectAnalyResult));
    }

    @Test
    public void login() {
        synUserService.synUserData();
    }

    @Test
    public void loginOUT() {
        synUserService.logout("95b51c71-e8ea-45d4-afe2-61ee24cbf0f9");
    }

    @Test
    public void getToken(){
        clientLoginService.getUser("3ea9286cda3956372cff5df1f983131b");
    }
    @Test
    public void getOrderJson(){
        JSONObject orderJson = warningService.getOrderJson();
        System.out.println(JSON.toJSONString(orderJson));
    }

    @Test
    public void synWaringHistory(){
        checkWarningHistoryService.synWarningHistory();
    }

    @Test
    public void checkByDay(){
        //checkReportService.createByDay(null);
    }

    @Test
    public void synSqlAndMiddlewareScore(){
        checkAvailaHistoryService.synSqlAndMiddlewareScore();
    }

    @Test
    public void createByWeek(){
        //checkReportService.createByWeek("202001",3);
    }

}