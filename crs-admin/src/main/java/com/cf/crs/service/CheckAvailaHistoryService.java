package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CheckAvailaHistory;
import com.cf.crs.mapper.CheckAvailaHistoryMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 告警历史数据同步
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Service
public class CheckAvailaHistoryService {

    @Autowired
    WarningService warningService;

    @Autowired
    CheckServerService checkServerService;

    @Autowired
    CheckSqlService checkSqlService;

    @Autowired
    CheckObjectService checkObjectService;

    @Autowired
    CheckAvailaHistoryMapper checkAvailaHistoryMapper;

    @Autowired
    CheckWarningHistoryService checkWarningHistoryService;


    public void synSqlAndMiddlewareScore(){
        checkWarningHistoryService.updateWaringHistory((name, serverNameList, sqlNameList, middlewareNameList)->{
            Integer sqlScore = getAvgScoreForSql(sqlNameList);
            Integer middlewareScore = getAvgScoreForSql(middlewareNameList);
            Integer serverScore = checkAvailabilt(serverNameList, 1);
            if (sqlScore == null && middlewareScore == null && serverScore == null) return;
            String day = DateUtil.date2String(new Date(), DateUtil.DEFAULT);
            CheckAvailaHistory dayHistory = checkAvailaHistoryMapper.selectOne(new QueryWrapper<CheckAvailaHistory>().eq("day", day).eq("displayName", name));
            if (dayHistory == null){
                //插入
                CheckAvailaHistory checkAvailaHistory = new CheckAvailaHistory();
                checkAvailaHistory.setDay(day);
                checkAvailaHistory.setMonth(day.substring(0,6));
                checkAvailaHistory.setYear(day.substring(0,4));
                checkAvailaHistory.setWeek(DateUtil.getWeekByDate(new Date()));
                checkAvailaHistory.setDisplayName(name);
                JSONObject jsonObject = new JSONObject();
                if (sqlScore != null) jsonObject.put("sql",sqlScore);
                if (middlewareScore != null) jsonObject.put("middleware",middlewareScore);
                if (serverScore != null) jsonObject.put("server",middlewareScore);
                checkAvailaHistory.setScore(jsonObject.toJSONString());
                checkAvailaHistoryMapper.insert(checkAvailaHistory);
            }else{
                //更新
                JSONObject jsonObject = new JSONObject();
                String score = dayHistory.getScore();
                if (StringUtils.isNotEmpty(score)) {
                    jsonObject = JSON.parseObject(score);
                }
                if (sqlScore != null) jsonObject.put("sql",sqlScore);
                if (middlewareScore != null) jsonObject.put("middleware",middlewareScore);
                if (serverScore != null) jsonObject.put("server",serverScore);
                dayHistory.setScore(jsonObject.toJSONString());
                checkAvailaHistoryMapper.updateById(dayHistory);
            }

        });
    }

    private Integer getAvgScoreForSql(List<String> sqlNameList){
        Integer sqlScore = 0;
        Integer SqlTotal = 0;
        for (String sqlName: sqlNameList) {
            String html = checkSqlService.getMonitorData(sqlName);
            if (StringUtils.isEmpty(html)) return null;
            log.info("sql Availab result:{}",html);
            Document doc = Jsoup.parse(html);
            Elements result = doc.select("response");
            if (result == null) return null;
            if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
            //请求数据成功
            Elements rowList = result.select("Monitorinfo");
            if (rowList == null || rowList.isEmpty()) return null;
            SqlTotal += 1;
            Integer resultScore = DataChange.obToInt(rowList.get(0).attr("TODAYUNAVAILPERCENT"));
            sqlScore += (100 - resultScore);
        }
        if (SqlTotal <= 0) return null;
        return sqlScore/SqlTotal;
    }

    /**
     * 获取服务器性能考评分数
     * @param type （1:天 2：上周 3：上月）
     */
    public Integer checkAvailabilt(List<String> serverNameList,Integer type){
        int total = 0;
        int score = 0;
        for (String  deviceName: serverNameList) {
            JSONObject jsonObject = checkServerService.checkAvailabilt(deviceName);
            log.info("server Availab result:{}",JSON.toJSONString(jsonObject));
            JSONObject availProps = jsonObject.getJSONObject("availProps");
            if (availProps == null || availProps.isEmpty()) continue;
            total += 1;
            int result = 0;
            if (type == 1) result = availProps.getIntValue("今天");
            else if (type == 2) result = availProps.getIntValue("最近一周");
            else if (type == 3) result = availProps.getIntValue("上月");
            score += result;
        }
        if (total <= 0) return null;
        return score/total;
    }

    /**
     * 考评每天的告警评分
     */
    public JSONObject checkByDay(String day,String displayName){
        if (StringUtils.isEmpty(day))  day = DateUtil.date2String(DateUtil.getYesterday(), DateUtil.DEFAULT);
        CheckAvailaHistory checkAvailaHistory = checkAvailaHistoryMapper.selectOne(new QueryWrapper<CheckAvailaHistory>().eq("day", day).eq("displayName",displayName));
        if (checkAvailaHistory == null) return null;
        return JSON.parseObject(checkAvailaHistory.getScore());

    }

}
