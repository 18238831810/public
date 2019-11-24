package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.CheckPlan;
import com.cf.crs.job.dto.ScheduleJobDTO;
import com.cf.crs.job.entity.ScheduleJobEntity;
import com.cf.crs.job.service.ScheduleJobService;
import com.cf.crs.mapper.ScheduleJobMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.activation.MailcapCommandMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckPlanService {

    @Autowired
    ScheduleJobService scheduleJobService;

    @Autowired
    ScheduleJobMapper scheduleJobMapper;

    /**
     * 获取考评任务配置
     * @return
     */
    public ResultJson<List<CheckPlan>> getCheckPlan(){
        List<ScheduleJobEntity> list = scheduleJobMapper.selectBatchIds(Arrays.asList(1, 2, 3, 4));
        if (CollectionUtils.isEmpty(list)) return HttpWebResult.getMonoSucResult(Lists.newArrayList());
        List<CheckPlan> collect = list.stream().map(scheduleJobEntity -> {
            CheckPlan checkPlan = new CheckPlan();
            checkPlan.setCronExpression(scheduleJobEntity.getCronExpression());
            String params = scheduleJobEntity.getParams();
            JSONObject jsonObject = JSON.parseObject(params);
            checkPlan.setEmail(jsonObject.getString("email"));
            checkPlan.setType(jsonObject.getLong("type"));
            return checkPlan;
        }).collect(Collectors.toList());
        return HttpWebResult.getMonoSucResult(collect);

    }

    /**
     * 更改考评任务配置
     * @param list
     * @return
     */
    public ResultJson<String> updateCheckPlan(String list){
        try {
            List<CheckPlan> checkPlans = (List<CheckPlan>)JSONArray.parse(list);
            if(CollectionUtils.isEmpty(checkPlans)) return HttpWebResult.getMonoError("考评管理设置不能为空");
            checkPlans.forEach(checkPlan -> updateCheckPlan(checkPlan));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 更改任务配置
     * @param checkPlan
     */
    private void updateCheckPlan(CheckPlan checkPlan) {
        ScheduleJobDTO scheduleJobDTO = new ScheduleJobDTO();
        scheduleJobDTO.setStatus(1);
        scheduleJobDTO.setId(checkPlan.getType());
        scheduleJobDTO.setBeanName("checkPlanTask");
        scheduleJobDTO.setCronExpression(checkPlan.getCronExpression());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", checkPlan.getType());
        jsonObject.put("email",checkPlan.getEmail());
        scheduleJobDTO.setParams(jsonObject.toJSONString());
        scheduleJobService.update(scheduleJobDTO);
    }

    public static void main(String[] args) {
        CheckPlan checkPlan = new CheckPlan();
        checkPlan.setEmail("dfdf");
        CheckPlan checkPlan1 = new CheckPlan();
        checkPlan1.setEmail("dfdfd");
        ArrayList<Object> objects = Lists.newArrayList();
        objects.add(checkPlan);
        objects.add(checkPlan1);
        String s = JSON.toJSONString(objects);
        List<CheckPlan> checkPlans = (List<CheckPlan>)JSONArray.parse(s);
        System.out.println(JSON.toJSONString(checkPlans));

    }

}
