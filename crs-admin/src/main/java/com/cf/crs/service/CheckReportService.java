package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckReport;
import com.cf.crs.entity.CheckWarningHistory;
import com.cf.crs.mapper.CheckAvailaHistoryMapper;
import com.cf.crs.mapper.CheckReportMapper;
import com.cf.crs.mapper.CheckWarningHistoryMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    CheckWarningHistoryMapper checkWarningHistoryMapper;

    @Autowired
    CheckWarningHistoryService checkWarningHistoryService;

    @Autowired
    CheckAvailaHistoryService checkAvailaHistoryService;

    public static void main(String[] args) {
        int s = LocalDate.now().getDayOfWeek().getValue();
        System.out.println(s);
        String weekByDate = DateUtil.getWeekByDate((DateUtil.getYesterday()));
        System.out.println(weekByDate);
    }

    public void synData(){
        //生成昨天的日表报数据
        createByDay(null);
        //生成上周的周报表数据
        int day = LocalDate.now().getDayOfWeek().getValue();
        if (day == 1) {
            //生成上周的周报表数据
            createByWeek(null,2);
        }
        int month = LocalDate.now().getDayOfMonth();
        if (month == 1) {
            //生成上周的周报表数据
            createByWeek(null,3);
        }
    }

    /**
     * 生成日报表
     */
   public void createByDay(String day){
       checkWarningHistoryService.updateWaringHistory((name, serverNameList, sqlNameList, middlewareNameList)->{
           if (CollectionUtils.isEmpty(serverNameList) && CollectionUtils.isEmpty(sqlNameList) && CollectionUtils.isEmpty(middlewareNameList)) return;
           String time = day;
           if (StringUtils.isEmpty(time))  time = DateUtil.date2String(DateUtil.getYesterday(), DateUtil.DEFAULT);
           //获取日报警评分
           JSONObject waringJson = checkWarningHistoryService.checkByDay(time, name);
           //获取可用性评分
           JSONObject avaJson = checkAvailaHistoryService.checkByDay(time, name);
           if(!DataUtil.jsonNotEmpty(waringJson) && !DataUtil.jsonNotEmpty(avaJson)) return;
           JSONObject jsonObject = new JSONObject();
           if (DataUtil.jsonNotEmpty(waringJson)) jsonObject.put("warning",waringJson);
           if (DataUtil.jsonNotEmpty(avaJson)) jsonObject.put("availa",avaJson);

           insertOrUpdateReport(name, jsonObject, time,1);
       });
   }


    /**
     * 生成周,月报表
     * @param week  日期
     * @param type  2:周 3:月 4:年
     */
    public void createByWeek(String week,Integer type){
        checkWarningHistoryService.updateWaringHistory((name, serverNameList, sqlNameList, middlewareNameList)->{
            String time = week;
            if (StringUtils.isEmpty(time))  time = DateUtil.getWeekByDate((DateUtil.getYesterday()));
            if (type == 3) time = time.substring(0,6);
            else if (type == 4) time = time.substring(0,4);
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
        Integer avaServerSum = 0,avaSqlSum = 0,avaMiddlewareSum = 0,warServerCriticalSum = 0,warSqlCriticalSum = 0,warMiddlewareCriticalSum = 0,warServerWarningSum = 0,warSqlWarningSum = 0,warSqlWarningCount = 0;
        Integer warMiddlewareWarningSum = 0,avaServerCount = 0,avaSqlCount = 0,avaMiddlewareCount = 0,warServerCriticalCount = 0,warSqlCriticalCount = 0,warMiddlewareCriticalCount = 0,warServerWarningCount = 0,warMiddlewareWarningCount = 0;
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
                    avaMiddlewareSum += middleware;
                    avaMiddlewareCount += 1;
                }
                Integer server = availa.getInteger("server");
                if (server != null) {
                    avaServerSum += server;
                    avaServerCount += 1;
                }
            }

            JSONObject waring = jsonObject.getJSONObject("warning");
            if (DataUtil.jsonNotEmpty(waring)){
                //sql
                JSONObject sql = waring.getJSONObject("sql");
                if (DataUtil.jsonNotEmpty(sql)) {
                    Integer critical = sql.getInteger("critical");
                    if (critical != null) {
                        warSqlCriticalSum += critical;
                        warSqlCriticalCount += 1;
                    }
                    Integer war = sql.getInteger("warning");
                    if (war != null) {
                        warSqlWarningSum += war;
                        warSqlWarningCount += 1;
                    }
                }

                JSONObject server = waring.getJSONObject("server");
                if (DataUtil.jsonNotEmpty(server)) {
                    Integer critical = server.getInteger("critical");
                    if (critical != null) {
                        warServerCriticalSum += critical;
                        warServerCriticalCount += 1;
                    }
                    Integer war = server.getInteger("warning");
                    if (war != null) {
                        warServerWarningSum += war;
                        warServerWarningCount += 1;
                    }
                }

                JSONObject middleware = waring.getJSONObject("middleware");
                if (DataUtil.jsonNotEmpty(middleware)) {
                    Integer critical = middleware.getInteger("critical");
                    if (critical != null) {
                        warMiddlewareCriticalCount += critical;
                        warMiddlewareCriticalCount += 1;
                    }
                    Integer war = middleware.getInteger("warning");
                    if (war != null) {
                        warMiddlewareWarningSum += war;
                        warMiddlewareWarningCount += 1;
                    }
                }

            }

        }
        return getResultJson(avaServerSum, avaSqlSum, avaMiddlewareSum, warServerCriticalSum, warSqlCriticalSum, warMiddlewareCriticalSum, warServerWarningSum, warSqlWarningSum, warMiddlewareWarningSum, avaServerCount, avaSqlCount, avaMiddlewareCount, warServerCriticalCount, warSqlCriticalCount, warMiddlewareCriticalCount, warServerWarningCount, warSqlWarningCount, warMiddlewareWarningCount);
    }

    private JSONObject getResultJson(Integer avaServerSum, Integer avaSqlSum, Integer avaMiddlewareSum, Integer warServerCriticalSum, Integer warSqlCriticalSum, Integer warMiddlewareCriticalSum, Integer warServerWarningSum, Integer warSqlWarningSum, Integer warMiddlewareWarningSum, Integer avaServerCount, Integer avaSqlCount, Integer avaMiddlewareCount, Integer warServerCriticalCount, Integer warSqlCriticalCount, Integer warMiddlewareCriticalCount, Integer warServerWarningCount, Integer warSqlWarningCount, Integer warMiddlewareWarningCount) {
        JSONObject ava = new JSONObject();
        if (avaServerCount > 0) ava.put("server",avaServerSum/avaServerCount);
        if (avaSqlCount > 0) ava.put("sql",avaSqlSum/avaSqlCount);
        if (avaMiddlewareCount > 0) ava.put("middleware",avaMiddlewareSum/avaMiddlewareCount);
        JSONObject war = new JSONObject();
        JSONObject server = getWaringDeviceJson(warServerCriticalSum, warServerWarningSum, warServerCriticalCount, warServerWarningCount);
        JSONObject sql = getWaringDeviceJson(warSqlCriticalSum, warSqlWarningSum, warSqlCriticalCount, warSqlWarningCount);
        JSONObject middleware = getWaringDeviceJson(warMiddlewareCriticalSum, warMiddlewareWarningSum, warMiddlewareCriticalCount, warMiddlewareWarningCount);
        war.put("server",server);
        war.put("sql",sql);
        war.put("middleware",middleware);
        JSONObject jsonObject = new JSONObject();
        if (DataUtil.jsonNotEmpty(ava)) jsonObject.put("availa",ava);
        if (DataUtil.jsonNotEmpty(war)) jsonObject.put("warning",war);
        return jsonObject;
    }

    private JSONObject getWaringDeviceJson(int warServerCriticalSum, int warServerWarningSum, int warServerCriticalCount, int warServerWarningCount) {
        JSONObject server = new JSONObject();
        if (warServerCriticalCount > 0) server.put("critical",warServerCriticalSum/warServerCriticalCount);
        if (warServerWarningCount > 0) server.put("warning",warServerWarningSum/warServerWarningCount);
        return server;
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

    public ResultJson<List<CheckReport>> getReport(Integer type, String time){
        List<CheckReport> list = checkReportMapper.selectList(new QueryWrapper<CheckReport>().eq("type", type).eq(checkType(type), time));
        return HttpWebResult.getMonoSucResult(list);
    }


    public ResultJson<List<CheckReport>> setReportScore(Integer type, String time,String displayName,Integer score){
        checkReportMapper.update(null,new UpdateWrapper<CheckReport>().eq("type", type).eq(checkType(type), time).eq("displayName",displayName).set("score",score));
        return HttpWebResult.getMonoSucStr();
    }


    public ResultJson<List<CheckWarningHistory>> getReportWarningDetails(Integer type, String time,String displayName){
        List<CheckWarningHistory> list = checkWarningHistoryMapper.selectList(new QueryWrapper<CheckWarningHistory>().eq(checkType(type), time).eq("displayName", displayName));
        return HttpWebResult.getMonoSucResult(list);
    }

}
