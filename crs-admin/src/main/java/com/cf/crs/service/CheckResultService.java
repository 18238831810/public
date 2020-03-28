package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CheckInfo;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.entity.CheckResult;
import com.cf.crs.mapper.CheckInfoMapper;
import com.cf.crs.mapper.CheckModeMapper;
import com.cf.crs.mapper.CheckResultMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    @Autowired
    CheckInfoMapper checkInfoMapper;

    @Autowired
    CheckModeService checkModeService;

    @Autowired
    CheckModeMapper checkModeMapper;

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


    /**
     * 考评入口
     */
    public void startCheck(Long id,Integer type){
        try {
            List<CheckInfo> list = checkInfoService.getCheckInfoList();
            for (CheckInfo checkInfo : list) {
                if (DataUtil.checkIsUsable(id) && !checkInfo.getId().equals(id)) continue;
                startCheck(checkInfo,type);
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
    }

    /**
     * 考评入口
     */
    public void startCheck(CheckInfo checkInfo,Integer type){
        //获取考评项目
        String checkItems = checkInfo.getCheckItems();
        if (StringUtils.isEmpty(checkItems)) {
            log.info("{}未设置考评项目");
            return;
        }
        //开始考评
        CheckResult checkResult = new CheckResult();
        //考评总分
        int scoreTotal = 0;
        //获取考评模型
        JSONObject checkMode = getCheckMode();

        List<String> checkItemList = Arrays.asList(checkItems.split(","));

        //业务考评
        JSONObject business = checkMode.getJSONObject("business");

        //业务健康考评
        JSONArray health = business.getJSONArray("health");
        for (Object o : health) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0){
                Integer score = jsonObject.getInteger("fraction");
                //页面可用性
                if (checkItemList.contains("4")){
                    //考评业务监测,暂不考评
                    checkResult.setHealth(1);
                    scoreTotal =+ score;
                }else{
                    checkResult.setHealth(1);
                    scoreTotal =+ score;
                }
            }else if(id == 1){
                //页面响应时间
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
            }else if(id == 2){
                //数据质量
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
            }else if(id == 3){
                //数据共享
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
            }
        }
        if (checkResult.getHealth() == null) checkResult.setHealth(business.getInteger("healthTotal"));
        //业务健康考评结束

        //信息安全
        JSONArray security = business.getJSONArray("security");
        for (Object o : security) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0) {
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 1) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 2) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 3) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 4) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 5) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 6) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 7) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 8) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            }
        }
        if (checkResult.getSafe() == null) checkResult.setSafe(business.getInteger("internetTotal"));
        //信息安全考评结束

        //物联网设备
        JSONArray internet = business.getJSONArray("internet");
        for (Object o : internet) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0) {
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 1) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 2) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 3) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 4) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 5) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 6) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 7) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            } else if (id == 8) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            }else if (id == 9) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            }else if (id == 10) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal = +score;
            }
        }
        if (checkResult.getIot() == null) checkResult.setIot(business.getInteger("internetTotal"));
        //信息安全考评结束

        //技术考评
        JSONArray technology = business.getJSONArray("technology");
        for (Object o : technology) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0){
                //服务器
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
                checkResult.setServerDevice(score);
            }else if(id == 1){
                //数据库
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
                checkResult.setSqlDevice(score);
            }else if(id == 2){
                //中间件
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal =+ score;
                checkResult.setMiddleware(score);
            }
        }
        //技术考评评结束
        Integer total = checkMode.getInteger("objectTotal");
        //考评总分
        if (scoreTotal >= total) checkResult.setResult(1);
        else checkResult.setResult(0);

        //考评类型
        checkResult.setType(type);
        //考评时间
        checkResult.setTime(System.currentTimeMillis());
        checkResultMapper.insert(checkResult);
    }

    private JSONObject getCheckMode() {
        CheckMode checkMode = checkModeMapper.selectById(1);
        String rule = checkMode.getRule();
        return JSON.parseObject(rule);
    }


}
