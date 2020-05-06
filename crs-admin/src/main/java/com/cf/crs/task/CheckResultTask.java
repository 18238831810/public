package com.cf.crs.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.job.task.ITask;
import com.cf.crs.service.CheckResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checkResultTask")
public class CheckResultTask implements ITask{

    @Autowired
    CheckResultService checkResultService;

    @Override
    public void run(String params) {
        try {
            JSONObject jsonObject = JSON.parseObject(params);
            Long id = jsonObject.getLong("id");
            Integer type = jsonObject.getInteger("type");
            checkResultService.autoCheck(id,type);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


}
