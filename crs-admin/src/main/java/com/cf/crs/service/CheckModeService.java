package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.entity.CheckPlan;
import com.cf.crs.job.dto.ScheduleJobDTO;
import com.cf.crs.job.entity.ScheduleJobEntity;
import com.cf.crs.job.service.ScheduleJobService;
import com.cf.crs.mapper.CheckModeMapper;
import com.cf.crs.mapper.ScheduleJobMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckModeService {

    @Autowired
    CheckModeMapper checkModeMapper;

    /**
     * 获取考评任务配置
     * @return
     */
    public ResultJson<List<CheckMode>> getCheckMode(){
        List<CheckMode> checkModes = checkModeMapper.selectBatchIds(Arrays.asList(1, 2, 3, 4));
        return HttpWebResult.getMonoSucResult(checkModes);

    }

    /**
     * 更改考评任务配置
     * @param list
     * @return
     */
    public ResultJson<String> updateCheckMode(String list){
        try {
            List<CheckMode> checkPlans = JSON.parseObject(list, List.class);
            if(CollectionUtils.isEmpty(checkPlans)) return HttpWebResult.getMonoError("考评模型不能为空");
            checkPlans.forEach(checkMode ->  checkModeMapper.updateById(checkMode));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }

}
