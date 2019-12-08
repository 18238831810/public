package com.cf.crs.service;

import com.cf.crs.entity.CheckMode;
import com.cf.crs.mapper.CheckModeMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

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
     * @param checkMode
     * @return
     */
    public ResultJson<String> updateCheckMode(CheckMode checkMode){
        try {
            if(StringUtils.isEmpty(checkMode.getRule())) return HttpWebResult.getMonoError("考评模型不能为空");
            checkModeMapper.updateById(checkMode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }

}
