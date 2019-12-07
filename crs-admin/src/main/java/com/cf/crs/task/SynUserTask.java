package com.cf.crs.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.job.task.ITask;
import com.cf.crs.service.EmailSenderService;
import com.cf.crs.service.SynUserService;
import com.cf.crs.sys.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("synUserTask")
public class SynUserTask implements ITask{

    @Autowired
    SynUserService synUserService;

    @Override
    public void run(String params) {
        try {
            log.info("同步用户信息计划开始执行");
            synUserService.synUserData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
