package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.entity.CheckInfo;
import com.cf.crs.entity.CheckResult;
import com.cf.crs.mapper.CheckInfoMapper;
import com.cf.crs.mapper.CheckResultMapper;
import com.cf.util.exception.CFException;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.CacheKey;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考评菜单
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckResultService {

    @Autowired
    CheckResultMapper checkResultMapper;

    @Autowired
    CheckInfoService checkInfoService;
    /**
     * 获取考评结果
     * @return
     */
    public ResultJson<List<CheckResult>> getCheckResult(){
        List<CheckResult> list = checkResultMapper.selectList(new QueryWrapper<CheckResult>());
        if (CollectionUtils.isEmpty(list)) return HttpWebResult.getMonoSucResult(Lists.newArrayList());
        Map<String, String> map = checkInfoService.getCheckInfoName();
        for (CheckResult checkResult : list) {
            String name = map.get(String.valueOf(checkResult.getCheckId()));
            if (StringUtils.isNotEmpty(name)) checkResult.setName(name);
        }
        return HttpWebResult.getMonoSucResult(list);
    }






}
