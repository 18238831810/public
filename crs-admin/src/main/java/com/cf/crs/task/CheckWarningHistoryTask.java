package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.CheckWarningHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checkWarningHistoryTask")
public class CheckWarningHistoryTask implements ITask{

    @Autowired
    CheckWarningHistoryService checkWarningHistoryService;

    @Override
    public void run(String params) {
        try {
            log.info("同步告警计划开始执行");
            checkWarningHistoryService.synWarningHistory();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
