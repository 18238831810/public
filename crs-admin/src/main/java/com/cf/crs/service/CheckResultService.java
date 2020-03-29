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

import java.util.*;
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

    @Autowired
    CheckInfoMapper checkInfoMapper;

    @Autowired
    CheckModeService checkModeService;

    @Autowired
    CheckModeMapper checkModeMapper;

    @Autowired
    WarningService warningService;

    /**
     * 获取考评结果
     * @return
     */
    public ResultJson<List<CheckResult>> getCheckResult(){
        List<CheckResult> list = checkResultMapper.selectList(new QueryWrapper<CheckResult>().orderByDesc("time"));
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
                scoreTotal += score;
                if (checkItemList.contains("4")){
                    //考评业务监测,暂不考评
                    checkResult.setHealth(1);
                }else{
                    checkResult.setHealth(1);
                }
            }else if(id == 1){
                //页面响应时间
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }else if(id == 2){
                //数据质量
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }else if(id == 3){
                //数据共享
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }
        }
        if (checkResult.getHealth() == null) {
            business.getInteger("healthTotal");
            checkResult.setHealth(1);
        }
        //业务健康考评结束

        //信息安全
        JSONArray security = business.getJSONArray("security");
        for (Object o : security) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0) {
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 1) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 2) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 3) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 4) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 5) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 6) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 7) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 8) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }
        }
        if (checkResult.getSafe() == null) {
            business.getInteger("internetTotal");
            checkResult.setSafe(1);
        }
        //信息安全考评结束

        //物联网设备
        JSONArray internet = business.getJSONArray("internet");
        for (Object o : internet) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            if (id == 0) {
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 1) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 2) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 3) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 4) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 5) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 6) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 7) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            } else if (id == 8) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }else if (id == 9) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }else if (id == 10) {
                //
                Integer score = jsonObject.getInteger("fraction");
                scoreTotal += score;
            }
        }
        if (checkResult.getIot() == null) {
            business.getInteger("internetTotal");
            checkResult.setIot(1);
        }
        //信息安全考评结束

        //技术考评
        scoreTotal = checkTechnology(checkInfo, checkResult, scoreTotal, checkMode, checkItemList);
        //技术考评评结束
        Integer total = checkMode.getInteger("objectTotal");
        //考评总分
        log.info("scoreTotal:{}",scoreTotal);
        if (scoreTotal >= total) checkResult.setResult(1);
        else checkResult.setResult(0);

        //考评类型
        checkResult.setType(type);
        //考评时间
        checkResult.setTime(System.currentTimeMillis());
        checkResult.setCheckId(checkInfo.getId());
        checkResultMapper.insert(checkResult);
    }

    /**
     * 技术考评
     * @param checkInfo
     * @param checkResult
     * @param scoreTotal
     * @param checkMode
     * @param checkItemList
     * @return
     */
    private int checkTechnology(CheckInfo checkInfo, CheckResult checkResult, int scoreTotal, JSONObject checkMode, List<String> checkItemList) {
        JSONObject technology = checkMode.getJSONObject("technology");
        JSONArray deviceTxt = technology.getJSONArray("deviceTxt");
        Map<String, List<CheckInfo>> deviceList = checkInfo.getDeviceList();
        for (Object o : deviceTxt) {
            //技术考评
            scoreTotal = checkTechnology(checkResult, scoreTotal, checkItemList, deviceList, o);
        }
        return scoreTotal;
    }

    /**
     * 技术考评
     * @param checkResult
     * @param scoreTotal
     * @param checkItemList
     * @param deviceList
     * @param o
     * @return
     */
    private int checkTechnology(CheckResult checkResult, int scoreTotal, List<String> checkItemList, Map<String, List<CheckInfo>> deviceList, Object o) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
        Integer id = jsonObject.getInteger("id");
        if (id == 0){
            //考评服务器
            scoreTotal = checkServer(checkResult, scoreTotal, checkItemList, deviceList, jsonObject);
        }else if(id == 1){
            //考评数据库
            scoreTotal = checkSql(checkResult, scoreTotal, checkItemList, deviceList, jsonObject);
        }else if(id == 2){
            //考评中间件
            scoreTotal = checkMiddleware(checkResult, scoreTotal, checkItemList, deviceList, jsonObject);
        }
        return scoreTotal;
    }

    /**
     * 考评中间件
     * @param checkResult
     * @param scoreTotal
     * @param checkItemList
     * @param deviceList
     * @param jsonObject
     * @return
     */
    private int checkMiddleware(CheckResult checkResult, int scoreTotal, List<String> checkItemList, Map<String, List<CheckInfo>> deviceList, JSONObject jsonObject) {
        //中间件
        List<String> deviceNameList = getDeviceNameList(deviceList, "2");
        //考评对象是否存在数据库和考评对象是否需要考评数据库
        if (CollectionUtils.isNotEmpty(deviceNameList) && checkItemList.contains("12")){
            //需要考评数据库
            JSONObject serverWaring = warningService.getSqlWaring(2,deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            if (critical > jsonObject.getInteger("maxHeight") || warning > jsonObject.getInteger("minHeight")){
                checkResult.setMiddleware(0);
                return scoreTotal;
            }
        }
        Integer score = jsonObject.getInteger("fraction");
        scoreTotal += score;
        checkResult.setMiddleware(1);
        return scoreTotal;
    }

    /**
     * 考评数据库
     * @param checkResult
     * @param scoreTotal
     * @param checkItemList
     * @param deviceList
     * @param jsonObject
     * @return
     */
    private int checkSql(CheckResult checkResult, int scoreTotal, List<String> checkItemList, Map<String, List<CheckInfo>> deviceList, JSONObject jsonObject) {
        //数据库
        List<String> deviceNameList = getDeviceNameList(deviceList, "1");
        //考评对象是否存在数据库和考评对象是否需要考评数据库
        if (CollectionUtils.isNotEmpty(deviceNameList) && checkItemList.contains("11")){
            //需要考评数据库
            JSONObject serverWaring = warningService.getSqlWaring(1,deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            if (critical > jsonObject.getInteger("maxHeight") || warning > jsonObject.getInteger("minHeight")){
                checkResult.setSqlDevice(0);
                return scoreTotal;
            }
        }
        Integer score = jsonObject.getInteger("fraction");
        scoreTotal += score;
        checkResult.setSqlDevice(1);
        return scoreTotal;
    }

    /**
     * 考评服务器
     * @param checkResult
     * @param scoreTotal
     * @param checkItemList
     * @param deviceList
     * @param jsonObject
     * @return
     */
    private int checkServer(CheckResult checkResult, int scoreTotal, List<String> checkItemList, Map<String, List<CheckInfo>> deviceList, JSONObject jsonObject) {
        //服务器
        List<String> deviceNameList = getDeviceNameList(deviceList, "3");
        //考评对象是否存在服务器和考评对象是否需要考评服务器
        if (CollectionUtils.isNotEmpty(deviceNameList) && checkItemList.contains("10")){
            //需要考评服务器
            JSONObject serverWaring = warningService.getServerWaring(deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            if (critical > jsonObject.getInteger("maxHeight") || warning > jsonObject.getInteger("minHeight")){
                checkResult.setServerDevice(0);
                return scoreTotal;
            }
        }
        Integer score = jsonObject.getInteger("fraction");
        scoreTotal += score;
        checkResult.setServerDevice(1);
        return scoreTotal;
    }

    /**
     * 获取考评设备列表
     * @param deviceList
     * @param type  1：服务器，2：数据库 3：中间件
     * @return
     */
    private List<String>  getDeviceNameList(Map<String, List<CheckInfo>> deviceList,String type) {
        if (!DataUtil.mapNotEmpty(deviceList)) return null;
        List<CheckInfo> CheckInfoList = deviceList.get(type);
        if (CollectionUtils.isEmpty(CheckInfoList)) return null;
        return CheckInfoList.stream().map(CheckInfo::getName).collect(Collectors.toList());
    }

    private JSONObject getCheckMode() {
        CheckMode checkMode = checkModeMapper.selectById(1);
        String rule = checkMode.getRule();
        return JSON.parseObject(rule);
    }


}
