package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("checkPlanTask")
public class CheckPlanTask implements ITask{

    @Override
    public void run(String params) {
        try {
            log.info("考评计划开始执行");
            switch (params){
                case "day":dayCheckPlan();
                case "week":weekCheckPlan();
                case "month":monthCheckPlan();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void dayCheckPlan(){
        log.info("日考评计划开始执行");
    }
    public void weekCheckPlan(){
        log.info("周考评计划开始执行");
    }
    public void monthCheckPlan(){
        log.info("月考评计划开始执行");
    }
}
