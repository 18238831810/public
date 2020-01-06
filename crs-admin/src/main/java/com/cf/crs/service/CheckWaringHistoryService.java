package com.cf.crs.service;

import cn.afterturn.easypoi.cache.manager.IFileLoader;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckObject;
import com.cf.crs.entity.CheckWaringHistory;
import com.cf.crs.mapper.CheckWaringHistoryMapper;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

import java.rmi.MarshalledObject;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
    CheckSqlService checkSqlService;

    @Autowired
    CheckObjectService checkObjectService;

    @Autowired
    CheckWaringHistoryMapper checkWaringHistoryMapper;

    /**
     * 统计告警数据
     */
    public void synWaringHistory(){
        //获取服务器信息
        JSONObject servers = checkServerService.getServers();
        //获取sql信息
        String sqlHtml = checkSqlService.getCheckSqlList(1);
        //获取中间件信息
        String middlewareHtml = checkSqlService.getCheckSqlList(2);
        JSONArray checkObjectList = getCheckObjectList();
        for (Object typeObj:checkObjectList){
            //遍历二级菜单
            JSONObject typeJson = JSON.parseObject(JSON.toJSONString(typeObj));
            if (typeJson == null || typeJson.isEmpty()) continue;
            String name = typeJson.getString("name");
            if (StringUtils.isEmpty(name)) continue;
            String information = typeJson.getString("information");
            if (StringUtils.isEmpty(information)) continue;
            JSONArray informationList = JSONArray.parseArray(information);
            synWaringHistory(servers,sqlHtml, middlewareHtml,name,informationList);
        }
    }

    /**
     * 统计告警数据
     */
    public void synWaringHistory(JSONObject servers,String sqlHtml,String middlewareHtml,String name,JSONArray informationList){
        long now = System.currentTimeMillis();
        JSONObject analyRecord = new JSONObject();
        JSONObject waringRecord = new JSONObject();
        analyRecord.put("time",now);
        waringRecord.put("time",now);
        List<String> serverNameList = Lists.newArrayList();
        List<String> sqlNameList = Lists.newArrayList();
        List<String> middlewareNameList = Lists.newArrayList();
        for (Object obj: informationList) {
            List<String> temp;
            JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(obj));
            String deviceType = jsonObject.getString("name");
            String information = jsonObject.getString("information");
            if (StringUtils.isEmpty(information)) continue;
            JSONArray nameList = JSONArray.parseArray(information);
            if ("server".equalsIgnoreCase(deviceType)) {
                listDeviceName(obj, serverNameList, nameList);
            }else if("sql".equalsIgnoreCase(deviceType)){
                listDeviceName(obj, sqlNameList, nameList);
            }else if("middleware".equalsIgnoreCase(deviceType)){
                listDeviceName(obj, middlewareNameList, nameList);
            }
        }
        //统计服务器数据
        packRecord("server",analyRecord,waringRecord,(key,list)->waringService.scoreServe(list,servers,serverNameList));
        packRecord("sql",analyRecord,waringRecord,(key,list)->waringService.scoreSql(list,sqlHtml, sqlNameList));
        packRecord("middleware",analyRecord,waringRecord,(key,list)->waringService.scoreSql(list,middlewareHtml, middlewareNameList));
        String day = DateUtil.date2String(new Date(), DateUtil.DEFAULT);
        CheckWaringHistory dayHistory = checkWaringHistoryMapper.selectOne(new QueryWrapper<CheckWaringHistory>().eq("day", day).eq("displayName",name));
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
            checkWaringHistory.setDisplayName(name);
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
            dayHistory.setDisplayName(name);
            checkWaringHistoryMapper.updateById(dayHistory);
        }


    }


    /**
     * 统计考评对象的设备名称
     * @param obj
     * @param temp
     * @param nameList
     */
    private void listDeviceName(Object obj, List<String> temp, JSONArray nameList) {
        for (Object o : nameList) {
            JSONObject deviceJson = JSON.parseObject(JSON.toJSONString(o));
            String deviceName = deviceJson.getString("name");
            if (StringUtils.isEmpty(deviceName)) continue;
            temp.add(deviceName);
        }
    }


    /**
     * 获取考评对象
     * @return
     */
    public JSONArray getCheckObjectList(){
        CheckObject object = checkObjectService.getObject();
        if (object == null) return null;
        String result = object.getObject();
        return JSON.parseArray(result);
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
