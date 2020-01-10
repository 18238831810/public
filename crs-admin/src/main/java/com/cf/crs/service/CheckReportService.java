package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckReport;
import com.cf.crs.mapper.CheckAvailaHistoryMapper;
import com.cf.crs.mapper.CheckReportMapper;
import com.cf.crs.mapper.CheckWaringHistoryMapper;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckReportService {

    @Autowired
    CheckReportMapper checkReportMapper;

    @Autowired
    CheckAvailaHistoryMapper checkAvailaHistoryMapper;

    @Autowired
    CheckWaringHistoryMapper checkWaringHistoryMapper;

    @Autowired
    CheckWaringHistoryService checkWaringHistoryService;

    @Autowired
    CheckAvailaHistoryService checkAvailaHistoryService;

    /**
     * 生成日报表
     */
   public void createByDay(String day){
       checkWaringHistoryService.updateWaringHistory((name,serverNameList,sqlNameList,middlewareNameList)->{
           if (CollectionUtils.isEmpty(serverNameList) && CollectionUtils.isEmpty(sqlNameList) && CollectionUtils.isEmpty(middlewareNameList)) return;
           String time = day;
           if (StringUtils.isEmpty(time))  time = DateUtil.date2String(DateUtil.getYesterday(), DateUtil.DEFAULT);
           //获取日报警评分
           JSONObject waringJson = checkWaringHistoryService.checkByDay(time, name);
           //获取可用性评分
           JSONObject avaJson = checkAvailaHistoryService.checkByDay(time, name);
           if(!DataUtil.jsonNotEmpty(waringJson) && !DataUtil.jsonNotEmpty(avaJson)) return;
           JSONObject jsonObject = new JSONObject();
           if (DataUtil.jsonNotEmpty(waringJson)) jsonObject.put("waring",waringJson);
           if (DataUtil.jsonNotEmpty(avaJson)) jsonObject.put("availa",avaJson);

           insertOrUpdateReport(name, jsonObject, time,1);
       });
   }


    /**
     * 生成周,月报表
     * @param week  日期
     * @param type  2:周 3:月
     */
    public void createByWeek(String week,Integer type){
        checkWaringHistoryService.updateWaringHistory((name,serverNameList,sqlNameList,middlewareNameList)->{
            String time = week;
            if (StringUtils.isEmpty(time))  time = DateUtil.date2String(DateUtil.getYesterday(), DateUtil.DEFAULT);
            //查询所有day统计取平均值
            List<CheckReport> list = checkReportMapper.selectList(new QueryWrapper<CheckReport>().eq("type", 1).eq(checkType(type), time).eq("displayName", name));
            if (CollectionUtils.isEmpty(list)) return;
            JSONObject jsonObject = getScoreJson(list);
            insertOrUpdateReport(name,jsonObject,time,type);
        });
   }


    /**
     * 统计评分（周，月）
     * @param list
     * @return
     */
    private JSONObject getScoreJson(List<CheckReport> list) {
        int avaServerSum = 0;
        int avaSqlSum = 0;
        int avaMiddlewareSum = 0;
        int warServerSum = 0;
        int warSqlSum = 0;
        int warMiddlewareSum = 0;
        int avaServerCount = 0;
        int avaSqlCount = 0;
        int avaMiddlewareCount = 0;
        int warServerCount = 0;
        int warSqlCount = 0;
        int warMiddlewareCount = 0;
        for (CheckReport checkReport : list) {
            String reportRecord = checkReport.getReportRecord();
            if(StringUtils.isEmpty(reportRecord)) continue;
            JSONObject jsonObject = JSON.parseObject(reportRecord);
            if (!DataUtil.jsonNotEmpty(jsonObject)) continue;
            JSONObject availa = jsonObject.getJSONObject("availa");
            if (DataUtil.jsonNotEmpty(availa)){
                Integer sql = availa.getInteger("sql");
                if (sql != null) {
                    avaSqlSum += sql;
                    avaSqlCount += 1;
                }
                Integer middleware = availa.getInteger("middleware");
                if (middleware != null) {
                    avaMiddlewareSum += sql;
                    avaMiddlewareCount += 1;
                }
                Integer server = availa.getInteger("server");
                if (server != null) {
                    avaServerSum += server;
                    avaServerCount += 1;
                }
            }
            JSONObject waring = jsonObject.getJSONObject("waring");
            if (DataUtil.jsonNotEmpty(waring)){
                Integer sql = waring.getInteger("sql");
                if (sql != null) {
                    warSqlSum += sql;
                    warSqlCount += 1;
                }
                Integer middleware = waring.getInteger("middleware");
                if (middleware != null) {
                    warMiddlewareSum += sql;
                    warMiddlewareCount += 1;
                }
                Integer server = waring.getInteger("server");
                if (server != null) {
                    warServerSum += server;
                    warServerCount += 1;
                }
            }

        }
        return getScoreJson(avaServerSum, avaSqlSum, avaMiddlewareSum, warServerSum, warSqlSum, warMiddlewareSum, avaServerCount, avaSqlCount, avaMiddlewareCount, warServerCount, warSqlCount, warMiddlewareCount);
    }


    /**
     * 统计评分（周，月）
     * @return
     */
    private JSONObject getScoreJson(int avaServerSum, int avaSqlSum, int avaMiddlewareSum, int warServerSum, int warSqlSum, int warMiddlewareSum, int avaServerCount, int avaSqlCount, int avaMiddlewareCount, int warServerCount, int warSqlCount, int warMiddlewareCount) {
        JSONObject ava = new JSONObject();
        if (avaServerCount > 0) ava.put("server",avaServerSum/avaServerCount);
        if (avaSqlCount > 0) ava.put("sql",avaSqlSum/avaSqlCount);
        if (avaMiddlewareCount > 0) ava.put("middleware",avaMiddlewareSum/avaMiddlewareCount);
        JSONObject war = new JSONObject();
        if (warServerCount > 0) war.put("server",warServerSum/warServerCount);
        if (warSqlCount > 0) war.put("sql",warSqlSum/warSqlCount);
        if (warMiddlewareCount > 0) war.put("middleware",warMiddlewareSum/warMiddlewareCount);
        JSONObject jsonObject = new JSONObject();
        if (DataUtil.jsonNotEmpty(ava)) jsonObject.put("availa",ava);
        if (DataUtil.jsonNotEmpty(war)) jsonObject.put("waring",war);
        return jsonObject;
    }


    private void insertOrUpdateReport(String name, JSONObject jsonObject, String time,Integer type) {
        CheckReport checkReport = checkReportMapper.selectOne(new QueryWrapper<CheckReport>().eq("type", type).eq(checkType(type), time).eq("displayName", name));
        if (checkReport == null){
            //创建
            CheckReport dayCheckReport = new CheckReport();
            dayCheckReport.setType(type);
            dayCheckReport.setDay(time);
            if (type == 1) {
                dayCheckReport.setMonth(time.substring(0,6));
                dayCheckReport.setYear(time.substring(0,4));
                dayCheckReport.setWeek(DateUtil.getWeekByDate(DateUtil.string2Date(time,DateUtil.DEFAULT)));
            }else if (type == 2) dayCheckReport.setWeek(time);
            else if (type == 3) dayCheckReport.setMonth(time);
            dayCheckReport.setDisplayName(name);
            dayCheckReport.setReportRecord(jsonObject.toJSONString());
            checkReportMapper.insert(dayCheckReport);
        }else {
            //更新
            checkReportMapper.update(null,new UpdateWrapper<>(checkReport).eq("id",checkReport.getId()).set("reportRecord",jsonObject.toJSONString()));
        }
    }

    private String checkType(Integer type){
        if (type == 1) return "day";
        else if(type == 2) return "week";
        else if(type == 3) return "month";
        else return "year";
    }

}
