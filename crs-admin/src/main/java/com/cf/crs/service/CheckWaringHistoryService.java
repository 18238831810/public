package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckWaringHistory;
import com.cf.crs.mapper.CheckWaringHistoryMapper;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 告警历史数据同步
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Service
public class CheckWaringHistoryService {

    @Autowired
    WaringService waringService;

    @Autowired
    CheckServerService checkServerService;

    @Autowired
    CheckWaringHistoryMapper checkWaringHistoryMapper;

    /**
     * 统计告警数据
     */
    public void synWaringHistory(){
        long now = System.currentTimeMillis();
        JSONObject analyRecord = new JSONObject();
        JSONObject waringRecord = new JSONObject();
        analyRecord.put("time",now);
        waringRecord.put("time",now);

        //统计服务器数据
        packRecord("server",analyRecord,waringRecord,(key,list)->waringService.analyServer(list));
        packRecord("sql",analyRecord,waringRecord,(key,list)->waringService.analySql(1,list));
        packRecord("middleware",analyRecord,waringRecord,(key,list)->waringService.analySql(2,list));
        String day = DateUtil.date2String(new Date(), DateUtil.DEFAULT);
        CheckWaringHistory dayHistory = checkWaringHistoryMapper.selectOne(new QueryWrapper<CheckWaringHistory>().eq("day", day));
        if (dayHistory == null){
            //插入数据
            CheckWaringHistory checkWaringHistory = new CheckWaringHistory();
            checkWaringHistory.setDay(day);
            checkWaringHistory.setMonth(day.substring(0,6));
            checkWaringHistory.setYear(day.substring(0,4));
            checkWaringHistory.setWeek(DateUtil.getWeekByDate(new Date()));
            ArrayList<Object> analyRecords = Lists.newArrayList();
            analyRecords.add(analyRecord);
            checkWaringHistory.setAnalyRecord(JSONArray.toJSONString(analyRecords));
            ArrayList<Object> waringRecords = Lists.newArrayList();
            waringRecords.add(waringRecord);
            checkWaringHistory.setWaringRecord(JSONArray.toJSONString(waringRecords));
            checkWaringHistoryMapper.insert(checkWaringHistory);
        }else {
            String analyRecords = dayHistory.getAnalyRecord();
            JSONArray analyList = new JSONArray();
            if (StringUtils.isNotEmpty(analyRecords)) analyList = JSONArray.parseArray(analyRecords);
            analyList.add(analyRecord);
            dayHistory.setAnalyRecord(JSONArray.toJSONString(analyList));

            String waringRecords = dayHistory.getWaringRecord();
            JSONArray waringList = new JSONArray();
            if (StringUtils.isNotEmpty(waringRecords)) waringList = JSONArray.parseArray(waringRecords);
            waringList.add(waringRecord);
            dayHistory.setWaringRecord(JSONArray.toJSONString(waringList));

            checkWaringHistoryMapper.updateById(dayHistory);
        }


    }

    /**
     * 封装告警记录
     * @param name
     * @param analyRecord
     * @param waringRecord
     * @param biFunction
     */
    private void packRecord(String name, JSONObject analyRecord, JSONObject waringRecord, BiFunction<Integer,List,JSONObject> biFunction){
        ArrayList<JSONObject> serverList = Lists.newArrayList();
        JSONObject serverObject = biFunction.apply(1,serverList);
        waringRecord.put(name,serverList);
        JSONObject analyResult = trantAnalyData(serverObject);
        analyRecord.put(name,analyResult);
    }

    /**
     * 打分
     * @param jsonObject
     * @return
     */
    private JSONObject trantAnalyData(JSONObject jsonObject){
        JSONObject result = new JSONObject();
        Integer criticalInt = 100;
        Integer clearlInt = 100;
        Integer warningInt = 100;
        if (DataUtil.jsonNotEmpty(jsonObject)){
            Integer totalRecords = jsonObject.getInteger("totalRecords");
            Integer critical = jsonObject.getInteger("critical");
            Integer clear = jsonObject.getInteger("clear");
            Integer warning = jsonObject.getInteger("warning");
            criticalInt = 100*(totalRecords - critical)/totalRecords;
            clearlInt = 100*(totalRecords - clear)/totalRecords;
            warningInt = 100*(totalRecords - warning)/totalRecords;
        }
        result.put("critical",criticalInt);
        result.put("clear",clearlInt);
        result.put("warning",warningInt);
        return result;
    }

    /**
     * 考评每天的告警评分
     */
    public void checkByDay(String day){
        if (StringUtils.isEmpty(day))  day = DateUtil.date2String(DateUtil.getYesterday(), DateUtil.DEFAULT);
        CheckWaringHistory dayHistory = checkWaringHistoryMapper.selectOne(new QueryWrapper<CheckWaringHistory>().eq("day", day));
        if (dayHistory == null) return;
        String analyRecord = dayHistory.getAnalyRecord();
        if (StringUtils.isEmpty(analyRecord)) return;
        JSONArray analyList = JSONArray.parseArray(analyRecord);
        JSONObject server = new JSONObject();
        JSONObject sql = new JSONObject();
        JSONObject middleware = new JSONObject();
        for (Object o : analyList) {
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
            sumWaring(server, jsonObject,"server");
            sumWaring(sql, jsonObject,"sql");
            sumWaring(middleware, jsonObject,"middleware");
        }
        int size = analyList.size();
        averageWaring(server,size);
        averageWaring(sql,size);
        averageWaring(middleware,size);
        JSONObject score = new JSONObject();
        score.put("server",server);
        score.put("sql",sql);
        score.put("middleware",middleware);
        checkWaringHistoryMapper.update(null,new UpdateWrapper<CheckWaringHistory>().set("score",JSON.toJSONString(score)).eq("id",dayHistory.getId()));
    }

    /**
     * 累加求和
     * @param server
     * @param jsonObject
     * @param name
     */
    private void sumWaring(JSONObject server, JSONObject jsonObject,String name) {
        JSONObject serverObj = jsonObject.getJSONObject(name);
        Integer critical = serverObj.getIntValue("critical");
        Integer clear = serverObj.getIntValue("clear");
        Integer warning = serverObj.getIntValue("warning");
        server.put("critical",critical + server.getIntValue("critical"));
        server.put("clear",clear + server.getIntValue("clear"));
        server.put("warning",warning + server.getIntValue("warning"));
    }

    /**
     * 求平均值
     * @param server
     * @param size
     */
    private void averageWaring(JSONObject server,Integer size) {
        Integer critical = server.getIntValue("critical");
        Integer clear = server.getIntValue("clear");
        Integer warning = server.getIntValue("warning");
        server.put("critical",critical/size);
        server.put("clear",clear/size);
        server.put("warning",warning/size);
    }


}
