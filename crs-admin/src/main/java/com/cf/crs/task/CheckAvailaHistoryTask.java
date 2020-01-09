package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.CheckAvailaHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checAvailaHistoryTask")
public class CheckAvailaHistoryTask implements ITask{

    @Autowired
    CheckAvailaHistoryService checkAvailaHistoryService;

    @Override
    public void run(String params) {
        try {
            log.info("同步性能考评数据计划开始执行");
            checkAvailaHistoryService.synSqlAndMiddlewareScore();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
