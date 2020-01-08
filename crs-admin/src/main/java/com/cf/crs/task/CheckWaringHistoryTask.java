package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.CheckWaringHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checkWaringHistory")
public class CheckWaringHistoryTask implements ITask{

    @Autowired
    CheckWaringHistoryService checkWaringHistoryService;

    @Override
    public void run(String params) {
        try {
            log.info("同步告警计划开始执行");
            checkWaringHistoryService.synWaringHistory();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}