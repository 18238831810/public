package com.cf.crs.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.job.task.ITask;
import com.cf.crs.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checkPlanTask")
public class CheckPlanTask implements ITask{

    @Autowired
    EmailSenderService emailSenderService;

    @Override
    public void run(String params) {
        try {
            log.info("考评计划开始执行");
            JSONObject json = JSON.parseObject(params);
            Integer type = json.getInteger("type");
            String email = json.getString("email");
            switch (type){
                case 1:dayCheckPlan(email);
                case 2:weekCheckPlan(email);
                case 3:monthCheckPlan(email);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void dayCheckPlan(String email){
        log.info("日考评计划开始执行:{}",email);
        emailSenderService.sendEmail("hello", "日考评计划开始执行",email);
    }
    public void weekCheckPlan(String email){
        log.info("周考评计划开始执行:{}",email);
        emailSenderService.sendEmail("hello", "周考评计划开始执行",email);
    }
    public void monthCheckPlan(String email){
        log.info("月考评计划开始执行:{}",email);
        emailSenderService.sendEmail("hello", "月考评计划开始执行",email);
    }
}
