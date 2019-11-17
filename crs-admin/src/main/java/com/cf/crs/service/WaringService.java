package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author frank
 * 2019/11/17
 **/
@Slf4j
@Service
public class WaringService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${check.server.url}")
    private String url;

    @Value("${check.server.name}")
    private String name;

    @Value("${check.server.apikey}")
    private String apikey;

    @Autowired
    CheckSqlService checkSqlService;


    public ResultJson<JSONObject> analyWaring(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server",analyServer());
        jsonObject.put("sql",analySql(1));
        jsonObject.put("middleware",analySql(2));
        return HttpWebResult.getMonoSucResult(jsonObject);
    }

    public JSONObject getServerList(){
        String serverUrl = url+"/api/json/discovery/getInfrastructureDetailsView?apiKey="+apikey+"&categoryName="+name;
        log.info("checkServerUrl:{}",serverUrl);
        return restTemplate.getForObject(serverUrl, JSONObject.class);
    }

    public JSONObject analyServer(){
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject servers = getServerList();
            if (servers == null || servers.isEmpty()) return jsonObject;
            log.info("serverResult:{}",servers.toJSONString());
            JSONObject infrastructureDetailsView = servers.getJSONObject("InfrastructureDetailsView");
            if (infrastructureDetailsView == null ||  servers.isEmpty()) return jsonObject;
            Integer totalRecords = infrastructureDetailsView.getInteger("TotalRecords");
            if (totalRecords == null) return jsonObject;
            jsonObject.put("totalRecords",totalRecords);
            List details = infrastructureDetailsView.getJSONArray("Details");
            if (details == null || details.isEmpty()) return  jsonObject;
            analyServer(jsonObject, details);
        } catch (Exception e) {
           log.info(e.getMessage(),e);
        }
        return jsonObject;
    }

    private void analyServer(JSONObject jsonObject, List details) {
        Integer critical = 0;
        Integer warning = 0;
        Integer clear = 0;
        for (Object obj:details) {
            JSONObject server = JSON.parseObject(JSON.toJSONString(obj));
            if (server == null || server.isEmpty()) continue;
            Integer severity = server.getInteger("severity");
            if (severity == null) continue;
            if (severity == 1) critical+=1;
            else if (severity == 5) clear+=1;
            else warning+=1;
        }
        jsonObject.put("critical",critical);
        jsonObject.put("warning",warning);
        jsonObject.put("clear",clear);
    }

    public JSONObject analySql(Integer type){
        JSONObject jsonObject = new JSONObject();
        try {
            String html = checkSqlService.getCheckSqlList(type);
            if (StringUtils.isEmpty(html)) return jsonObject;
            log.info("getCheckSqlResult:{}",html);
            Document doc = Jsoup.parse(html);
            Elements result = doc.select("response");
            if (result == null) return jsonObject;
            if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
            //请求数据成功
            Elements rowList = result.select("Monitor");
            analySql(jsonObject, rowList);
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
        return jsonObject;
    }

    private void analySql(JSONObject jsonObject, Elements rowList) {
        Integer critical = 0;
        Integer warning = 0;
        Integer clear = 0;
        for (Element element:rowList) {
            String status = element.attr("HEALTHSTATUS");
            if (StringUtils.isEmpty(status)) continue;
            if ("critical".equalsIgnoreCase(status)) critical+=1;
            else if ("warning".equalsIgnoreCase(status)) warning+=1;
            else if ("clear".equalsIgnoreCase(status)) clear+=1;
        }
        jsonObject.put("critical",critical);
        jsonObject.put("warning",warning);
        jsonObject.put("clear",clear);
    }

}
