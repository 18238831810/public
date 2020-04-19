package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.*;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
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

    @Autowired
    CheckInfoMapper checkInfoMapper;

    @Autowired
    CheckModeService checkModeService;

    @Autowired
    CheckModeMapper checkModeMapper;

    @Autowired
    WarningService warningService;

    @Autowired
    CheckResultLastMapper checkResultLastMapper;

    @Autowired
    CheckReportMapper checkReportMapper;

    /**
     * 考评id字段对照表
     */
    public Map<String,String> itemMap = Maps.newHashMap();


    @PostConstruct
    public void PostConstruct(){
        //业务监测
        itemMap.put("4","responseStatus");
        //数据质量
        itemMap.put("5","dataQualityStatus");
        //数据共享
        itemMap.put("6","dataSharingStatus");
        //页面可用性
        itemMap.put("7","businessStatus");
        //信息安全
        itemMap.put("8","safe");
        //物联网设备状态
        itemMap.put("9","iot");
        //服务器
        itemMap.put("10","serverDevice");
        //数据库
        itemMap.put("11","sqlDevice");
        //中间件
        itemMap.put("12","middleware");
        //网络设备
        itemMap.put("13","Internet");
    }

    /**
     * 获取考评结果
     * @return
     */
    public ResultJson<List<CheckResultLast>> getCheckResult(){
        List<CheckResultLast> list = checkResultLastMapper.selectList(new QueryWrapper<CheckResultLast>().orderByDesc("time"));
        if (CollectionUtils.isEmpty(list)) return HttpWebResult.getMonoSucResult(Lists.newArrayList());
        Map<String, String> map = checkInfoService.getCheckInfoName();
        for (CheckResultLast checkResult : list) {
            String name = map.get(String.valueOf(checkResult.getCheckId()));
            if (StringUtils.isNotEmpty(name)) checkResult.setName(name);
        }
        return HttpWebResult.getMonoSucResult(list);
    }


    /**
     *
     * 获取考评报表
     * @param page  分页参数
     * @param id   报表id
     * @param endTime   结束时间
     * @param page      页数
     * @return
     */
    public ResultJson<IPage<CheckResult>> getcheckReport(Long id,Long startTime, Long endTime, Page<CheckResult> page){
        if (!DataUtil.checkIsUsable(id)) return HttpWebResult.getMonoError("报表id不存在");
        CheckReport checkReport = checkReportMapper.selectById(id);
        if (checkReport == null) return HttpWebResult.getMonoError("报表设置不存在");
        String checkItems = checkReport.getCheckItems();
        List<String> selectFiledList = getSelectFiled(checkItems);
        String[] selectFiled = selectFiledList.toArray(new String[selectFiledList.size()]);
        String checkObjectIds = checkReport.getCheckObjectIdList();
        //考评对象列表
        List<String> objectList = Arrays.asList(checkObjectIds.split(","));
        IPage<CheckResult> checkResultIPage = checkResultMapper.selectPage(page, new QueryWrapper<CheckResult>().select(selectFiled).in("checkId", objectList).between("time", startTime, endTime));
        List<CheckResult> records = checkResultIPage.getRecords();
        Map<String, String> map = checkInfoService.getCheckInfoName();
        if(CollectionUtils.isNotEmpty(records)){
            for (CheckResult checkResult : records) {
                String name = map.get(String.valueOf(checkResult.getCheckId()));
                if (StringUtils.isNotEmpty(name)) checkResult.setName(name);
            }
        }
        String message = selectFiledList.stream().collect(Collectors.joining(","));
        return HttpWebResult.getMonoSucResult(message,checkResultIPage);
    }

    private List<String> getSelectFiled(String checkItems) {
        //考评项目id列表
        List<String> itemList = Arrays.asList(checkItems.split(","));
        //获取考评查询字段
        List<String> selectFiled = itemList.stream().map(key -> itemMap.get(key)).filter(key->StringUtils.isNotEmpty(key)).collect(Collectors.toList());
        selectFiled.add("id");
        selectFiled.add("checkId");
        selectFiled.add("type");
        selectFiled.add("result");
        selectFiled.add("time");
        return selectFiled;
    }


    /**
     * 考评入口
     */
    public void startCheck(Long id,Integer type){
        try {
            List<CheckInfo> list = checkInfoService.getCheckInfoList();
            for (CheckInfo checkInfo : list) {
                if (DataUtil.checkIsUsable(id) && !checkInfo.getId().equals(id)) continue;
                try {
                    startCheck(checkInfo,type);
                } catch (Exception e) {
                    log.info(e.getMessage(),e);
                }
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
            Integer score = jsonObject.getInteger("fraction");
            Map<String, List<CheckInfo>> deviceList = checkInfo.getDeviceList();
            if (id == 0){
                //页面可用性
                if (checkItemList.contains("7")){
                    //需要考评,考评页面可用性
                    checkResult.setBusinessCondition(jsonObject.getString("qualification"));
                    List<String> deviceNameList = getDeviceNameList(deviceList, "1");
                    if (CollectionUtils.isNotEmpty(deviceNameList)){
                        JSONObject serverWaring = warningService.getSqlWaring(1,deviceNameList);
                        Integer clear = serverWaring.getInteger("clear");
                        if (clear <= 0){
                            //页面可用不正常
                            checkResult.setBusinessVaule("不正常");
                            checkResult.setBusinessStatus(0);
                            checkResult.setHealth(0);
                        }else{
                            checkResult.setBusinessVaule("正常");
                        }
                    }
                    if (checkResult.getBusinessStatus() == null || checkResult.getBusinessStatus() != 0) scoreTotal += score;
                }else{
                    //不需要考评
                    //checkResult.setBusinessVaule(DEFAULTVALUE);
                    scoreTotal += score;
                }
            }else if(id == 1){
                //业务监测
                if (checkItemList.contains("4")){
                    checkResult.setResponseCondition(jsonObject.getString("qualification"));
                    List<String> deviceNameList = getDeviceNameList(deviceList, "6");
                    if (CollectionUtils.isNotEmpty(deviceNameList)){
                        JSONObject serverWaring = warningService.getSqlWaring(1,deviceNameList);
                        Integer clear = serverWaring.getInteger("clear");
                        if (clear <= 0){
                            //页面可用不正常
                            checkResult.setResponseVaule("不正常");
                            checkResult.setResponseStatus(0);
                            checkResult.setHealth(0);
                        }else{
                            checkResult.setResponseVaule("正常");
                        }
                    }
                    //需要考评,业务监测
                    if (checkResult.getResponseStatus() == null || checkResult.getResponseStatus() != 0)scoreTotal += score;
                }else{
                    //不需要考评
                    //checkResult.setBusinessVaule(DEFAULTVALUE);
                    scoreTotal += score;
                }
            }else if(id == 2){
                //数据质量
                if (checkItemList.contains("5")){
                    //需要考评,业务监测
                    checkResult.setDataQualityCondition(jsonObject.getString("qualification"));
                    checkResult.setDataQualityVaule("95");
                    scoreTotal += score;
                }else{
                    //不需要考评
                    //checkResult.setDataQualityVaule(DEFAULTVALUE);
                    scoreTotal += score;
                }
            }else if(id == 3){
                //数据共享
                if (checkItemList.contains("6")){
                    //需要考评,业务监测
                    checkResult.setDataSharingCondition(jsonObject.getString("qualification"));
                    checkResult.setDataSharingVaule("正常");
                    scoreTotal += score;
                }else{
                    //不需要考评
                    //checkResult.setDataSharingVaule(DEFAULTVALUE);
                    scoreTotal += score;
                }
            }
        }
        if (checkResult.getHealth() == null) {
            business.getInteger("healthTotal");
            checkResult.setHealth(1);
        }
        //业务健康考评结束

        //信息安全
        String informationSecurity = checkInfo.getInformationSecurity();
        JSONArray securityArray = JSONArray.parseArray(informationSecurity);
        if (checkItemList.contains("8") && StringUtils.isNotEmpty(informationSecurity) && securityArray != null && !securityArray.isEmpty()){
            //需要考评
            JSONArray security = business.getJSONArray("security");

            //获取用户设置的信息安全设置
            for (Object o : security) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                Integer id = jsonObject.getInteger("id");
                Integer score = jsonObject.getInteger("fraction");
                if (id == 0) {
                    //安全漏洞
                    //获取安全漏洞比率设置
                    String condition = "";
                    Integer percent = jsonObject.getInteger("percent");
                    condition += "加固比率>=" + percent;
                    //获取漏洞比率
                    JSONArray deviceTxtCondition = jsonObject.getJSONArray("deviceTxt");
                    //服务器
                    JSONObject serveCondition = (JSONObject)deviceTxtCondition.get(0);
                    condition += " 服务器:";
                    //危机漏洞
                    Integer serverMaxHeightCondition = serveCondition.getInteger("maxHeight");
                    condition += " 危机漏洞<="+ serverMaxHeightCondition;
                    //高分险漏洞
                    Integer serverMidHeightCondition = serveCondition.getInteger("midHeight");
                    condition += " 高分险漏洞<="+ serverMidHeightCondition;
                    //中风险漏洞
                    Integer serverMinHeightCondition = serveCondition.getInteger("minHeight");
                    condition += " 中风险漏洞<="+ serverMinHeightCondition;

                    JSONObject sqlCondition = (JSONObject)deviceTxtCondition.get(1);
                    condition += " 数据库<=";
                    //危机漏洞
                    Integer sqlMaxHeightCondition = sqlCondition.getInteger("maxHeight");
                    condition += " 危机漏洞<="+ sqlMaxHeightCondition;
                    //高分险漏洞
                    Integer sqlMidHeightCondition = sqlCondition.getInteger("midHeight");
                    condition += " 高分险漏洞<="+ sqlMidHeightCondition;
                    //中风险漏洞
                    Integer sqlMinHeightCondition = sqlCondition.getInteger("minHeight");
                    condition += " 中风险漏洞<="+ sqlMinHeightCondition;

                    JSONObject middlewareCondition = (JSONObject)deviceTxtCondition.get(2);
                    condition += " 中间件<=";
                    //危机漏洞
                    Integer middlewareMaxHeightCondition = middlewareCondition.getInteger("maxHeight");
                    condition += " 危机漏洞<="+ middlewareMaxHeightCondition;
                    //高分险漏洞
                    Integer middlewareMidHeightCondition = middlewareCondition.getInteger("midHeight");
                    condition += " 高分险漏洞<="+ middlewareMidHeightCondition;
                    //中风险漏洞
                    Integer middlewareMinHeightCondition = middlewareCondition.getInteger("minHeight");
                    condition += " 中风险漏洞<="+ middlewareMinHeightCondition;
                    checkResult.setSecurityBreachCondition(condition);


                    String securityValue= "";
                    JSONObject leak = getCheckSafeByType(securityArray, "leak");
                    //获取加固比率
                    Integer leakPercent = leak.getInteger("leakPercent");
                    if(leakPercent != null) {
                        securityValue+= "加固比率:" + leakPercent;
                        if (leakPercent < percent){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //获取漏洞比率
                    JSONArray deviceTxt = leak.getJSONArray("deviceTxt");
                    //服务器
                    JSONObject serve = (JSONObject)deviceTxt.get(0);
                    securityValue+= " 服务器:";
                    //危机漏洞
                    Integer serverMaxHeight = serve.getInteger("maxHeight");
                    if (serverMaxHeight != null) {
                        securityValue+= " 危机漏洞:"+ serverMaxHeight;
                        if (serverMaxHeight > serverMaxHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //高分险漏洞
                    Integer serverMidHeight = serve.getInteger("midHeight");
                    if (serverMidHeight != null) {
                        securityValue+= " 高分险漏洞:"+ serverMidHeight;
                        if (serverMidHeight > serverMidHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //中风险漏洞
                    Integer serverMinHeight = serve.getInteger("minHeight");
                    if (serverMinHeight != null) {
                        securityValue+= " 中风险漏洞:"+ serverMinHeight;
                        if (serverMinHeight > serverMinHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }

                    JSONObject sql = (JSONObject)deviceTxt.get(1);
                    securityValue+= " 数据库:";
                    //危机漏洞
                    Integer sqlMaxHeight = sql.getInteger("maxHeight");
                    if (sqlMaxHeight != null) {
                        securityValue+= " 危机漏洞:"+ sqlMaxHeight;
                        if (sqlMaxHeight > sqlMaxHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //高分险漏洞
                    Integer sqlMidHeight = sql.getInteger("midHeight");
                    if (sqlMidHeight != null) {
                        securityValue+= " 高分险漏洞:"+ sqlMidHeight;
                        if (sqlMidHeight > sqlMidHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //中风险漏洞
                    Integer sqlMinHeight = sql.getInteger("minHeight");
                    if (sqlMinHeight != null) {
                        securityValue+= " 中风险漏洞:"+ sqlMinHeight;
                        if (sqlMinHeight > sqlMinHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }

                    JSONObject middleware = (JSONObject)deviceTxt.get(2);
                    securityValue+= " 中间件:";
                    //危机漏洞
                    Integer middlewareMaxHeight = middleware.getInteger("maxHeight");
                    if (middlewareMaxHeight != null) {
                        securityValue+= " 危机漏洞:"+ middlewareMaxHeight;
                        if (middlewareMaxHeight > middlewareMaxHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //高分险漏洞
                    Integer middlewareMidHeight = middleware.getInteger("midHeight");
                    if (middlewareMidHeight != null) {
                        securityValue+= " 高分险漏洞:"+ middlewareMidHeight;
                        if (middlewareMidHeight > middlewareMidHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //中风险漏洞
                    Integer middlewareMinHeight = middleware.getInteger("minHeight");
                    if (middlewareMinHeight != null) {
                        securityValue+= " 中风险漏洞:"+ middlewareMinHeight;
                        if (middlewareMinHeight > middlewareMinHeightCondition){
                            checkResult.setSecurityBreachStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (securityValue.endsWith("中间件:")) securityValue = securityValue.replace("中间件:","").trim();
                    if (securityValue.endsWith("数据库:")) securityValue = securityValue.replace("数据库:","").trim();
                    if (securityValue.endsWith("服务器:")) securityValue = securityValue.replace("服务器:","").trim();
                    if (StringUtils.isNotEmpty(securityValue)) checkResult.setSecurityBreachVaule(securityValue.trim());
                    if (checkResult.getSecurityBreachStatus() == null || checkResult.getSecurityBreachStatus() != 0) scoreTotal += score;
                } else if (id == 1) {
                    //病毒攻击
                    //获取病毒攻击配置
                    JSONObject virus = getCheckSafeByType(securityArray, "virus");
                    //查杀比率设置值
                    Integer virusPercent = virus.getInteger("virusPercent");
                    //病毒数量设置值
                    Integer virusNum = virus.getInteger("virusNum");

                    //查杀比率达标值
                    Integer percent = jsonObject.getInteger("percent");
                    //病毒数量达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setVirusAttackCondition("查杀比率>="+percent+" 病毒数量<="+num);
                    String virusAttackVaule = "";
                    if(virusPercent != null) {
                        virusAttackVaule += "查杀比率:"+virusPercent;
                        if (virusPercent < percent){
                            checkResult.setVirusAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (virusNum != null){
                        virusAttackVaule += (" 病毒数量" + virusNum);
                        if(virusNum > num){
                            checkResult.setVirusAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (StringUtils.isNotEmpty(virusAttackVaule)) checkResult.setVirusAttackVaule(virusAttackVaule);
                    if (checkResult.getVirusAttackStatus() == null || checkResult.getVirusAttackStatus() != 0) scoreTotal += score;
                } else if (id == 2) {
                    //端口扫描
                    JSONObject port = getCheckSafeByType(securityArray, "port");
                    //获取端口扫描设置值
                    Integer portNum = port.getInteger("portNum");
                    //获取端口扫描达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setPortScanCondition("端口扫描<="+num);
                    if(portNum != null){
                        checkResult.setPortScanVaule("端口扫描:"+portNum);
                        if (portNum > num){
                            checkResult.setPortScanStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    //病毒攻击
                    if (checkResult.getPortScanStatus() == null || checkResult.getPortScanStatus() != 0) scoreTotal += score;
                } else if (id == 3) {
                    //强力攻击
                    JSONObject strong = getCheckSafeByType(securityArray, "strong");
                    //获取强力攻击设置值
                    Integer strongNum = strong.getInteger("strongNum");
                    //获取强力攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setForceAttackCondition("强力攻击<="+num);
                    if(strongNum != null){
                        checkResult.setForceAttackVaule("强力攻击:"+strongNum);
                        if (strongNum > num){
                            checkResult.setForceAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getForceAttackStatus() == null || checkResult.getForceAttackStatus() != 0) scoreTotal += score;
                } else if (id == 4) {
                    //木马后门攻击
                    JSONObject trojan = getCheckSafeByType(securityArray, "trojan");
                    //获取木马后门攻击设置值
                    Integer trojanNum = trojan.getInteger("trojanNum");
                    //获取木马后门攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setTrojanAttackCondition("木马后门攻击<="+num);
                    if(trojanNum != null){
                        checkResult.setTrojanAttackVaule("木马后门攻击:"+trojanNum);
                        if (trojanNum > num){
                            checkResult.setTrojanAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getTrojanAttackStatus() == null || checkResult.getTrojanAttackStatus() != 0) scoreTotal += score;
                } else if (id == 5) {
                    //拒绝访问攻击
                    JSONObject refuse = getCheckSafeByType(securityArray, "refuse");
                    //获取拒绝访问攻击设置值
                    Integer refuseNum = refuse.getInteger("refuseNum");
                    //获取拒绝访问攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setDeniedAttacCondition("拒绝访问攻击<="+num);
                    if(refuseNum != null){
                        checkResult.setDeniedAttackVaule("拒绝访问攻击:"+refuseNum);
                        if (refuseNum > num){
                            checkResult.setDeniedAttacStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getDeniedAttacStatus() == null || checkResult.getDeniedAttacStatus() != 0) scoreTotal += score;
                } else if (id == 6) {
                    //缓冲区溢出攻击
                    JSONObject buffer = getCheckSafeByType(securityArray, "buffer");
                    //获取缓冲区溢出攻击设置值
                    Integer bufferNum = buffer.getInteger("bufferNum");
                    //获取缓冲区溢出攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setZoneAttacCondition("缓冲区溢出攻击<="+num);
                    if(bufferNum != null){
                        checkResult.setZoneAttackVaule("缓冲区溢出攻击:"+bufferNum);
                        if (bufferNum > num){
                            checkResult.setZoneAttacStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getZoneAttacStatus() == null || checkResult.getZoneAttacStatus() != 0) scoreTotal += score;
                } else if (id == 7) {
                    //网络蠕虫攻击
                    JSONObject worm = getCheckSafeByType(securityArray, "worm");
                    //获取网络蠕虫攻击设置值
                    Integer wormNum = worm.getInteger("wormNum");
                    //获取网络蠕虫攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setWormAttacCondition("网络蠕虫攻击<="+num);
                    if(wormNum != null){
                        checkResult.setWormAttackVaule("网络蠕虫攻击:"+wormNum);
                        if (wormNum > num){
                            checkResult.setWormAttacStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getWormAttacStatus() == null || checkResult.getWormAttacStatus() != 0) scoreTotal += score;
                } else if (id == 8) {
                    //ip碎片攻击
                    JSONObject ip = getCheckSafeByType(securityArray, "ip");
                    //获取ip碎片攻击设置值
                    Integer ipNum = ip.getInteger("ipNum");
                    //获取ip碎片攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setIpAttacCondition("ip碎片攻击<="+num);
                    if(ipNum != null){
                        checkResult.setIpAttackVaule("ip碎片攻击:"+ipNum);
                        if (ipNum > num){
                            checkResult.setIpAttacStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getIpAttacStatus() == null || checkResult.getIpAttacStatus() != 0) scoreTotal += score;
                }
            }
            if (checkResult.getSafe() == null) {
                checkResult.setSafe(1);
            }
        }else{
            //不需要考评
            Integer score = business.getInteger("securityTotal");
            scoreTotal += score;
            checkResult.setSafe(1);
        }
        //信息安全考评结束

        //物联网设备
        JSONArray internet = business.getJSONArray("internet");
        for (Object o : internet) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            Integer id = jsonObject.getInteger("id");
            Integer score = jsonObject.getInteger("fraction");
            if (id == 0) {
                //执法车
                scoreTotal += score;
            } else if (id == 1) {
                //绿化车辆
                scoreTotal += score;
            } else if (id == 2) {
                //环卫车
                scoreTotal += score;
            } else if (id == 3) {
                //摄像头
                scoreTotal += score;
            } else if (id == 4) {
                //执法仪
                scoreTotal += score;
            } else if (id == 5) {
                //对讲机
                scoreTotal += score;
            } else if (id == 6) {
                //环卫工牌
                scoreTotal += score;
            } else if (id == 7) {
                //公厕一体机
                scoreTotal += score;
            } else if (id == 8) {
                //果壳箱监测仪
                scoreTotal += score;
            }else if (id == 9) {
                //气体监测仪
                scoreTotal += score;
            }else if (id == 10) {
                //避险设备
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
        long now = System.currentTimeMillis();
        checkResult.setTime(now);
        checkResult.setCheckId(checkInfo.getId());
        checkResultMapper.insert(checkResult);
        checkInfoMapper.update(null,new UpdateWrapper<CheckInfo>().eq("id",checkResult.getCheckId()).set("lastCheckTime",now).set("lastCheckResult",checkResult.getResult()));
        checkResultLastMapper.delete(new QueryWrapper<CheckResultLast>().eq("checkId",checkResult.getCheckId()));
        checkResultLastMapper.insert(checkResult);
    }

    private JSONObject getCheckSafeByType(JSONArray securityArray,String type) {
        for (Object obj : securityArray) {
            JSONObject json = JSON.parseObject(JSON.toJSONString(obj));
            String id = json.getString("id");
            if (type.equalsIgnoreCase(id)) return json;
        }
        return null;
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
            Integer maxHeight = jsonObject.getInteger("maxHeight");
            Integer minHeight = jsonObject.getInteger("minHeight");
            checkResult.setMiddlewareCondition("严重告警数<="+maxHeight+",故障告警数<="+minHeight);
            JSONObject serverWaring = warningService.getSqlWaring(2,deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            checkResult.setMiddlewareVaule("严重告警数:"+critical+",故障告警数:"+warning);
            if (critical > maxHeight || warning > minHeight){
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
            Integer maxHeight = jsonObject.getInteger("maxHeight");
            Integer minHeight = jsonObject.getInteger("minHeight");
            checkResult.setSqlCondition("严重告警数<="+maxHeight+",故障告警数<="+minHeight);
            JSONObject serverWaring = warningService.getSqlWaring(1,deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            checkResult.setSqlVaule("严重告警数:"+critical+",故障告警数:"+warning);
            if (critical > maxHeight || warning > minHeight){
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
            Integer maxHeight = jsonObject.getInteger("maxHeight");
            Integer minHeight = jsonObject.getInteger("minHeight");
            checkResult.setServerCondition("严重告警数<="+maxHeight+",故障告警数<="+minHeight);

            JSONObject serverWaring = warningService.getServerWaring(deviceNameList);
            Integer critical = serverWaring.getInteger("critical");
            Integer warning = serverWaring.getInteger("warning");
            checkResult.setServerVaule("严重告警数:"+critical+",故障告警数:"+warning);
            if (critical >  maxHeight || warning > minHeight){
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
