package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.CheckReportService;
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
@Component("checkReportTask")
public class CheckReportTask implements ITask{

    @Autowired
    CheckReportService checkReportService;

    @Override
    public void run(String params) {
        try {
            log.info("生成报表计划开始执行");
            //checkReportService.synData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
