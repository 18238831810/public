package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.entity.*;
import com.cf.crs.mapper.*;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.CacheKey;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.DataUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
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
    HttpServletRequest request;

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

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CheckIotService checkIotService;

    @Autowired
    CityTokenService cityTokenService;

    @Value("${checkPdf}")
    String checkResultPath;

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
     * 发送考评结果
     * @return
     */
    public ResultJson<String> sendEmailForReslut(Long id){
        try {
            CheckInfo checkInfo = createPdf(checkResultPath, id);
            if (checkInfo == null) return HttpWebResult.getMonoError("发送失败");
            //发送指定报表
            String title = checkInfo.getName() + "-报表";
            String content = "报表详情请参考附件";
            String email = checkInfo.getEmail();
            if (StringUtils.isEmpty(email)) return HttpWebResult.getMonoError("此考评对象没有设置发送邮箱");
            return emailSenderService.sendEmail(title,content,email,checkResultPath + checkInfo.getName() + ".pdf");
        } catch (Exception e) {
            return HttpWebResult.getMonoError("发送失败");
        }
    }

    /**
     * 发送考评结果
     * @return
     */
    public ResultJson<String> sendEmailForResluts(String ids){
        List<String> idList = Lists.newArrayList();
        String[] split = ids.split(",");
        for (String s : split) {
            ResultJson<String> resultJson = sendEmailForReslut(DataChange.obToLong(s));
            if(resultJson.getCode() != 200) idList.add(s);
        }
        if (CollectionUtils.isNotEmpty(idList)) {
            String collect = idList.stream().collect(Collectors.joining(","));
            return HttpWebResult.getMonoError(collect+"发送失败");
        }
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 审批考评结果
     * @param id
     * @param filed
     * @param result
     * @return
     */
    public ResultJson<String> updateCheckResult(Long id,String filed,Integer result){
        String userAuth = cityTokenService.getUserAuth();
        if (!"1".equalsIgnoreCase(userAuth)) return  HttpWebResult.getMonoError("您没有审批权限，请申请管理员权限");
        if (!DataUtil.checkIsUsable(id) || StringUtils.isEmpty(filed) || result == null) return HttpWebResult.getMonoError("审批失败(审批信息错误)");
        //更改结果字段
        checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set(filed,result));
        checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set(filed,result));
        //更改关联字段
        CheckResultLast checkResultLast = checkResultLastMapper.selectById(id);
        //更新业务健康度
        updateHealth(id, checkResultLast);
        //更新安全信息
        updateSafe(id, checkResultLast);
        //更新物联网设备
        updateIot(id,checkResultLast);
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 更新安全信息
     * @param id
     * @param checkResultLast
     */
    private void updateSafe(Long id, CheckResultLast checkResultLast) {
        Integer securityBreachStatus = checkResultLast.getSecurityBreachStatus();
        Integer virusAttackStatus = checkResultLast.getVirusAttackStatus();
        Integer portScanStatus = checkResultLast.getPortScanStatus();
        Integer forceAttackStatus = checkResultLast.getForceAttackStatus();
        Integer trojanAttackStatus = checkResultLast.getTrojanAttackStatus();
        Integer deniedAttacStatus = checkResultLast.getDeniedAttackStatus();
        Integer zoneAttacStatus = checkResultLast.getZoneAttackStatus();
        Integer wormAttacStatus = checkResultLast.getWormAttackStatus();
        Integer ipAttacStatus = checkResultLast.getIpAttackStatus();
        Integer safe = checkResultLast.getSafe();
        if (DataUtil.checkIsUsable(securityBreachStatus) &&
                DataUtil.checkIsUsable(virusAttackStatus) &&
                DataUtil.checkIsUsable(portScanStatus) &&
                DataUtil.checkIsUsable(forceAttackStatus) &&
                DataUtil.checkIsUsable(trojanAttackStatus) &&
                DataUtil.checkIsUsable(deniedAttacStatus) &&
                DataUtil.checkIsUsable(zoneAttacStatus) &&
                DataUtil.checkIsUsable(wormAttacStatus) &&
                DataUtil.checkIsUsable(ipAttacStatus)){
            //业务健康度达标
            if (!DataUtil.checkIsUsable(safe)) {
                //业务健康度需改为达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("safe",1));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("safe",1));
            }
        }else{
            //业务健康度不达标
            if (DataUtil.checkIsUsable(safe)) {
                //业务健康度需改为不达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("safe",0));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("safe",0));
            }
        }
    }

    /**
     * 更新业务健康度
     * @param id
     * @param checkResultLast
     */
    private void updateHealth(Long id, CheckResultLast checkResultLast) {
        //业务可用性达标状态
        Integer businessStatus = checkResultLast.getBusinessStatus();
        //业务监测达标状态
        Integer responseStatus = checkResultLast.getResponseStatus();
        //数据质量达标状态
        Integer dataQualityStatus = checkResultLast.getDataQualityStatus();
        //数据共享达标状态
        Integer dataSharingStatus = checkResultLast.getDataSharingStatus();
        //业务健康度
        Integer health = checkResultLast.getHealth();
        if (DataUtil.checkIsUsable(businessStatus) &&
                DataUtil.checkIsUsable(responseStatus) &&
                DataUtil.checkIsUsable(dataQualityStatus) &&
                DataUtil.checkIsUsable(dataSharingStatus)){
            //业务健康度达标
            if (!DataUtil.checkIsUsable(health)) {
                //业务健康度需改为达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("health",1));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("health",1));
            }
        }else{
            //业务健康度不达标
            if (DataUtil.checkIsUsable(health)) {
                //业务健康度需改为不达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("health",0));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("health",0));
            }
        }
    }


    /**
     * 更新物联网设备
     * @param id
     * @param checkResultLast
     */
    private void updateIot(Long id, CheckResultLast checkResultLast) {
        Integer zhifacheStatus = checkResultLast.getZhifacheStatus();
        Integer lvhuacheStatus = checkResultLast.getLvhuacheStatus();
        Integer huanweicheStatus = checkResultLast.getHuanweicheStatus();
        Integer shexiangtouStatus = checkResultLast.getShexiangtouStatus();
        Integer zhifayiStatus = checkResultLast.getZhifayiStatus();
        Integer duijiangjiStatus = checkResultLast.getDuijiangjiStatus();
        Integer hwgongpaiStatus = checkResultLast.getHwgongpaiStatus();
        Integer gcyitijiStatus = checkResultLast.getGcyitijiStatus();
        Integer gkxjianceyiStatus = checkResultLast.getGkxjianceyiStatus();
        Integer qtjianceyiStatus = checkResultLast.getQtjianceyiStatus();
        Integer bixianshebeiStatus = checkResultLast.getBixianshebeiStatus();
        //物联网设备
        Integer iot = checkResultLast.getIot();
        if (DataUtil.checkIsUsable(zhifacheStatus) &&
                DataUtil.checkIsUsable(lvhuacheStatus) &&
                DataUtil.checkIsUsable(huanweicheStatus) &&
                DataUtil.checkIsUsable(shexiangtouStatus) &&
                DataUtil.checkIsUsable(shexiangtouStatus) &&
                DataUtil.checkIsUsable(zhifayiStatus) &&
                DataUtil.checkIsUsable(duijiangjiStatus) &&
                DataUtil.checkIsUsable(hwgongpaiStatus) &&
                DataUtil.checkIsUsable(gcyitijiStatus) &&
                DataUtil.checkIsUsable(gkxjianceyiStatus) &&
                DataUtil.checkIsUsable(qtjianceyiStatus) &&
                DataUtil.checkIsUsable(bixianshebeiStatus)){
            //物联网设备达标
            if (!DataUtil.checkIsUsable(iot)) {
                //业务健康度需改为达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("iot",1));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("iot",1));
            }
        }else{
            //物联网设备不达标
            if (DataUtil.checkIsUsable(iot)) {
                //业务健康度需改为不达标
                checkResultLastMapper.update(null,new UpdateWrapper<CheckResultLast>().eq("id",id).set("iot",0));
                checkResultMapper.update(null,new UpdateWrapper<CheckResult>().eq("id",id).set("iot",0));
            }
        }
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
     * 自动考评入口
     */
    public void autoCheck(Long id,Integer type){
        CheckInfo allCheckInfo = (CheckInfo) redisUtils.get(CacheKey.CHECK_PLAN);
        if (id != 0) {
            if (allCheckInfo != null) {
                log.info("存在全局考评任务，此任务不生效");
                return;
            }
            startCheck(id,type,true);
        }else{
            if(isCheckTime(allCheckInfo)) return;
            startCheck(id,type,false);
        }

    }

    private boolean isCheckTime(CheckInfo checkInfo) {
        Long checkStartTime = checkInfo.getCheckStartTime();
        Long checkEndTime = checkInfo.getCheckEndTime();
        long now = System.currentTimeMillis();
        if(DataUtil.checkIsUsable(checkEndTime) && checkEndTime < now){
            log.info("{},{}已经过期",checkInfo.getId(),now);
            return true;
        }
        if(DataUtil.checkIsUsable(checkStartTime) && checkStartTime > now){
            log.info("{},{}未到开始时间",checkInfo.getId(),now);
            return true;
        }
        return false;
    }


    /**
     * 考评入口
     */
    public void startCheck(Long id,Integer type,boolean autoCheck){
        try {
            List<CheckInfo> list = checkInfoService.getCheckInfoList();
            for (CheckInfo checkInfo : list) {
                if (DataUtil.checkIsUsable(id) && !checkInfo.getId().equals(id)) continue;
                try {
                    //自动考评需要考评考评时间限制
                    if(autoCheck && isCheckTime(checkInfo)) continue;
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
                    List<String> deviceNameList = getDeviceNameList(deviceList, "6");
                    if (CollectionUtils.isNotEmpty(deviceNameList)){
                        JSONObject serverWaring = warningService.getSqlWaring(6,deviceNameList);
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
                    List<String> deviceNameList = getDeviceNameList(deviceList, "7");
                    if (CollectionUtils.isNotEmpty(deviceNameList)){
                        JSONObject serverWaring = warningService.getSqlWaring(7,deviceNameList);
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
                    checkResult.setDeniedAttackCondition("拒绝访问攻击<="+num);
                    if(refuseNum != null){
                        checkResult.setDeniedAttackVaule("拒绝访问攻击:"+refuseNum);
                        if (refuseNum > num){
                            checkResult.setDeniedAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getDeniedAttackStatus() == null || checkResult.getDeniedAttackStatus() != 0) scoreTotal += score;
                } else if (id == 6) {
                    //缓冲区溢出攻击
                    JSONObject buffer = getCheckSafeByType(securityArray, "buffer");
                    //获取缓冲区溢出攻击设置值
                    Integer bufferNum = buffer.getInteger("bufferNum");
                    //获取缓冲区溢出攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setZoneAttackCondition("缓冲区溢出攻击<="+num);
                    if(bufferNum != null){
                        checkResult.setZoneAttackVaule("缓冲区溢出攻击:"+bufferNum);
                        if (bufferNum > num){
                            checkResult.setZoneAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getZoneAttackStatus() == null || checkResult.getZoneAttackStatus() != 0) scoreTotal += score;
                } else if (id == 7) {
                    //网络蠕虫攻击
                    JSONObject worm = getCheckSafeByType(securityArray, "worm");
                    //获取网络蠕虫攻击设置值
                    Integer wormNum = worm.getInteger("wormNum");
                    //获取网络蠕虫攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setWormAttackCondition("网络蠕虫攻击<="+num);
                    if(wormNum != null){
                        checkResult.setWormAttackVaule("网络蠕虫攻击:"+wormNum);
                        if (wormNum > num){
                            checkResult.setWormAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getWormAttackStatus() == null || checkResult.getWormAttackStatus() != 0) scoreTotal += score;
                } else if (id == 8) {
                    //ip碎片攻击
                    JSONObject ip = getCheckSafeByType(securityArray, "ip");
                    //获取ip碎片攻击设置值
                    Integer ipNum = ip.getInteger("ipNum");
                    //获取ip碎片攻击达标值
                    Integer num = jsonObject.getInteger("num");
                    checkResult.setIpAttackCondition("ip碎片攻击<="+num);
                    if(ipNum != null){
                        checkResult.setIpAttackVaule("ip碎片攻击:"+ipNum);
                        if (ipNum > num){
                            checkResult.setIpAttackStatus(0);
                            checkResult.setSafe(0);
                        }
                    }
                    if (checkResult.getIpAttackStatus() == null || checkResult.getIpAttackStatus() != 0) scoreTotal += score;
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
        Map<String, List<CheckInfo>> deviceList = checkInfo.getDeviceList();
        List<CheckInfo> checkInfos = deviceList.get("8");
        if (checkItemList.contains("9") && CollectionUtils.isNotEmpty(checkInfos)){
            //获取互联网设备列表（对应ioconfig中的key）
            List<String> internetList = checkInfos.stream().map(checkInfoDevice -> checkInfoDevice.getName()).collect(Collectors.toList());
            for (Object o : internet) {
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                Integer id = jsonObject.getInteger("id");
                Integer score = jsonObject.getInteger("fraction");
                Double num = jsonObject.getDouble("num");
                //气体监测仪和避险设备数据
                JSONObject sensorJson = new JSONObject();
                JSONArray cardArr = new JSONArray();
                if (id == 0) {
                    //执法车
                    String iotName = "iot_zhifache_status";
                    if(internetList.contains(iotName)){
                        checkResult.setZhifacheCondition("执法车>="+num+"%");
                        Double value = checkIotService.getnormalRateForCard(cardArr,iotName);
                        checkResult.setZhifacheVaule("执法车:"+value+"%");
                        if(value < num) {
                            checkResult.setZhifacheStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getZhifacheStatus() == null || checkResult.getZhifacheStatus() != 0) scoreTotal += score;
                } else if (id == 1) {
                    //绿化车辆
                    String iotName = "iot_lvhuache_status";
                    if(internetList.contains(iotName)){
                        checkResult.setLvhuacheCondition("绿化车>="+num+"%");
                        Double value = checkIotService.getnormalRateForCard(cardArr,iotName);
                        checkResult.setLvhuacheVaule("绿化车:"+value+"%");
                        if(value < num) {
                            checkResult.setLvhuacheStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getLvhuacheStatus() == null || checkResult.getLvhuacheStatus() != 0) scoreTotal += score;
                } else if (id == 2) {
                    //环卫车
                    String iotName = "iot_huanweiche_status";
                    if(internetList.contains(iotName)){
                        checkResult.setHuanweicheCondition("环卫车>="+num+"%");
                        Double value = checkIotService.getnormalRateForCard(cardArr,iotName);
                        checkResult.setHuanweicheVaule("环卫车:"+value+"%");
                        if(value < num) {
                            checkResult.setHuanweicheStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getHuanweicheStatus() == null || checkResult.getHuanweicheStatus() != 0) scoreTotal += score;
                } else if (id == 3) {
                    //摄像头
                    String iotName = "iot_shexiangtou_status";
                    if(internetList.contains(iotName)){
                        checkResult.setShexiangtouCondition("摄像头>="+num+"%");
                        Double value = checkIotService.getnormalRateByDay(iotName);
                        checkResult.setShexiangtouVaule("摄像头:"+value+"%");
                        if(value < num) {
                            checkResult.setShexiangtouStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getShexiangtouStatus() == null || checkResult.getShexiangtouStatus() != 0) scoreTotal += score;
                } else if (id == 4) {
                    //执法仪
                    scoreTotal += score;
                } else if (id == 5) {
                    //对讲机
                    String iotName = "iot_zhifaduijiang_status";
                    if(internetList.contains(iotName)){
                        checkResult.setDuijiangjiCondition("对讲机>="+num+"%");
                        Double value = checkIotService.getnormalRateByDay(iotName);
                        checkResult.setDuijiangjiVaule("对讲机:"+value+"%");
                        if(value < num) {
                            checkResult.setDuijiangjiStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getDuijiangjiStatus() == null || checkResult.getDuijiangjiStatus() != 0) scoreTotal += score;
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
                    String iotName = "iot_qtjianceyi_status";
                    if(internetList.contains(iotName)){
                        checkResult.setQtjianceyiCondition("气体监测仪>="+num+"%");
                        Double value = checkIotService.getnormalRateByDay(id,sensorJson);
                        checkResult.setDuijiangjiVaule("气体监测仪:"+value+"%");
                        if(value < num) {
                            checkResult.setQtjianceyiStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getQtjianceyiStatus() == null || checkResult.getQtjianceyiStatus() != 0) scoreTotal += score;
                }else if (id == 10) {
                    //避险设备
                    String iotName = "iot_bixianshebei_status";
                    if(internetList.contains(iotName)){
                        checkResult.setBixianshebeiCondition("避险设备>="+num+"%");
                        Double value = checkIotService.getnormalRateByDay(id,sensorJson);
                        checkResult.setBixianshebeiVaule("避险设备:"+value+"%");
                        if(value < num) {
                            checkResult.setBixianshebeiStatus(0);
                            checkResult.setIot(0);
                        }
                    }
                    if (checkResult.getBixianshebeiStatus() == null || checkResult.getBixianshebeiStatus() != 0) scoreTotal += score;
                }
            }
            if (checkResult.getIot() == null) {
                checkResult.setIot(1);
            }
        }else{
            Integer score = business.getInteger("internetTotal");
            scoreTotal += score;
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
        checkResult.setScore(scoreTotal);
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
    private List<String> getDeviceNameList(Map<String, List<CheckInfo>> deviceList,String type) {
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





    /**
     * html转pdf
     * @param file
     * @throws Exception
     */
   public CheckInfo createPdf(String file,Long id)  {
       try {
           Properties prop = new Properties();
           prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
           Velocity.init(prop);
           //封装模板数据
           Map<String, Object> map = new HashMap<>();
           CheckResultLast checkResultLast = checkResultLastMapper.selectById(id);
           CheckInfo checkInfo = checkInfoMapper.selectById(checkResultLast.getCheckId());
           JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(checkResultLast));
           jsonObject.keySet().forEach(key -> {
               String value = jsonObject.getString(key);
               if (StringUtils.isNotEmpty(value)) jsonObject.put(key,value.replace("<","&lt;").replace(">","&gt;"));
           });
           jsonObject.put("checkName",checkInfo.getName());
           map.put("resultList",jsonObject);
           VelocityContext context = new VelocityContext(map);
           //渲染模板
           String out = getTemplate(context);

           Document document = new Document();
           PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file+checkInfo.getName()+".pdf"));
           document.open();
           Reader reader = new StringReader(out);
           XMLWorkerHelper.getInstance().parseXHtml(writer, document, reader);
           document.close();
           return checkInfo;
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           return null;
       }
   }

    private String getTemplate(VelocityContext context) {
        StringWriter writer = new StringWriter();
        Template tpl = Velocity.getTemplate("template/checkResult.vm", "UTF-8");
        tpl.merge(context, writer);
        String out = writer.toString();
        return out;
    }


}
