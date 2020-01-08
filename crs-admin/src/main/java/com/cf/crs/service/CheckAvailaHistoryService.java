package com.cf.crs.service;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.mapper.CheckWaringHistoryMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    WaringService waringService;

    @Autowired
    CheckServerService checkServerService;

    @Autowired
    CheckSqlService checkSqlService;

    @Autowired
    CheckObjectService checkObjectService;

    @Autowired
    CheckWaringHistoryMapper checkWaringHistoryMapper;

    @Autowired
    CheckWaringHistoryService checkWaringHistoryService;

    /**
     * 获取服务器性能考评分数
     * @param type （1:天 2：上周 3：上月）
     */
    public Integer checkAvailabilt(List<String> serverNameList,Integer type){
        int total = 0;
        int score = 0;
        for (String  deviceName: serverNameList) {
            JSONObject jsonObject = checkServerService.checkAvailabilt(deviceName);
            JSONObject availProps = jsonObject.getJSONObject("availProps");
            if (availProps == null || availProps.isEmpty()) continue;
            total += 1;
            int result = 0;
            if (type == 0) result = availProps.getIntValue("昨天");
            else if (type == 0) result = availProps.getIntValue("最近一周");
            else if (type == 0) result = availProps.getIntValue("上月");
            score += result;
        }
        return score/total;
    }

    public void syn(){
        checkWaringHistoryService.updateWaringHistory((name,serverNameList,sqlNameList,middlewareNameList)->{
            int score = 0;
            int total = 0;
            for (String sqlName: sqlNameList) {
                String html = checkSqlService.getMonitorData(sqlName);
                if (StringUtils.isEmpty(html)) return;
                log.info("getCheckSqlResult:{}",html);
                Document doc = Jsoup.parse(html);
                Elements result = doc.select("response");
                if (result == null) return;
                if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
                //请求数据成功
                Elements rowList = result.select("Monitorinfo");
                if (rowList == null || rowList.isEmpty()) return;
                total += 1;
                Integer resultScore = DataChange.obToInt(rowList.get(0).attr("TODAYUNAVAILPERCENT"));
                score += (100 - resultScore);
            }

        });

    }

}
