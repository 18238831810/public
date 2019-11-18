package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frank
 * 2019/11/18
 **/
@Slf4j
@Service
public class CheckObjectAnalyService {

    @Autowired
    CheckObjectService checkObjectService;

    @Autowired
    WaringService waringService;

    @Autowired
    CheckSqlService checkSqlService;


    /*public Map<String,JSONObject> getServersMap(){
        HashMap<String,JSONObject> map = Maps.newHashMap();
        try {
            JSONObject servers = waringService.getServerList();
            if (servers == null || servers.isEmpty()) return map;
            log.info("serverResult:{}",servers.toJSONString());
            JSONObject infrastructureDetailsView = servers.getJSONObject("InfrastructureDetailsView");
            if (infrastructureDetailsView == null ||  servers.isEmpty()) return map;
            Integer totalRecords = infrastructureDetailsView.getInteger("TotalRecords");
            if (totalRecords == null) return map;
            List details = infrastructureDetailsView.getJSONArray("Details");
            if (details == null || details.isEmpty()) return  map;
            for (Object obj:details) {
                JSONObject server = JSON.parseObject(JSON.toJSONString(obj));
                if (server == null || server.isEmpty()) continue;
                String name = server.getString("name");
                map.put(name,server);
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
        return map;
    }*/

    /*public Map<String,JSONObject> getSqlMap(Integer type){
        Map<Object, Object> map = Maps.newHashMap();
        try {
            String html = checkSqlService.getCheckSqlList(type);
            if (StringUtils.isEmpty(html)) return map;
            log.info("getCheckSqlResult:{}",html);
            Document doc = Jsoup.parse(html);
            Elements result = doc.select("response");
            if (result == null) return map;
            if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
            //请求数据成功
            Elements rowList = result.select("Monitor");
            for (Element element:rowList) {
                String status = element.attr("HEALTHSTATUS");
                if (StringUtils.isEmpty(status)) continue;
                if ("critical".equalsIgnoreCase(status)) critical+=1;
                else if ("warning".equalsIgnoreCase(status)) warning+=1;
                else if ("clear".equalsIgnoreCase(status)) clear+=1;
            }
    }*/

}
