package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
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
    @Value("${check.order.url}")
    private String orderUrl;
    @Value("${check.order.apikey}")
    private String orderAppkey;

    @Autowired
    CheckSqlService checkSqlService;

    @Autowired
    CheckServerService checkServerService;


    public ResultJson<JSONObject> analyWaring(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("server",analyServer(null));
        jsonObject.put("sql",analySql(1,null));
        jsonObject.put("middleware",analySql(2,null));
        jsonObject.put("order",analyOrder());
        return HttpWebResult.getMonoSucResult(jsonObject);
    }


    public JSONObject analyServer(List record){
        JSONObject servers = checkServerService.getServers();
        return scoreServe(record, servers,null);
    }


    public JSONObject scoreServe(List record, JSONObject servers,List<String> deviceNameList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (servers == null || servers.isEmpty()) return jsonObject;
            log.info("serverResult:{}",servers.toJSONString());
            JSONObject infrastructureDetailsView = servers.getJSONObject("InfrastructureDetailsView");
            if (infrastructureDetailsView == null ||  servers.isEmpty()) return jsonObject;
            Integer totalRecords = infrastructureDetailsView.getInteger("TotalRecords");
            if (totalRecords == null) return jsonObject;
            jsonObject.put("totalRecords",totalRecords);
            List details = infrastructureDetailsView.getJSONArray("Details");
            if (details == null || details.isEmpty()) return  jsonObject;
            analyServer(jsonObject, details,record,deviceNameList);
        } catch (Exception e) {
           log.info(e.getMessage(),e);
        }
        return jsonObject;
    }

    private void analyServer(JSONObject jsonObject, List details,List record,List<String> deviceNameList) {
        Integer critical = 0;
        Integer warning = 0;
        Integer clear = 0;
        for (Object obj:details) {
            JSONObject server = JSON.parseObject(JSON.toJSONString(obj));
            if (server == null || server.isEmpty()) continue;
            Integer severity = server.getInteger("severity");
            if (CollectionUtils.isEmpty(deviceNameList) || !deviceNameList.contains(server.getString("name"))) continue;
            if (severity == null) continue;
            if (record != null) record.add(server);
            if (severity == 1) critical+=1;
            else if (severity == 5) clear+=1;
            else warning+=1;
        }
        jsonObject.put("critical",critical);
        jsonObject.put("warning",warning);
        jsonObject.put("clear",clear);
    }

    public JSONObject analySql(Integer type,List record){
        String html = checkSqlService.getCheckSqlList(type);
        return scoreSql(record, html,null);
    }

    public JSONObject scoreSql(List record, String html,List<String> deviceNameList) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (StringUtils.isEmpty(html)) return jsonObject;
            log.info("getCheckSqlResult:{}",html);
            Document doc = Jsoup.parse(html);
            Elements result = doc.select("response");
            if (result == null) return jsonObject;
            if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
            //请求数据成功
            Elements rowList = result.select("Monitor");
            analySql(jsonObject, rowList,record,deviceNameList);
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
        return jsonObject;
    }

    private void analySql(JSONObject jsonObject, Elements rowList,List record,List<String> deviceNameList) {
        Integer critical = 0;
        Integer warning = 0;
        Integer clear = 0;
        Integer count = 0;
        for (Element element:rowList) {
            String status = element.attr("HEALTHSTATUS");
            count+=1;
            if (StringUtils.isEmpty(status)) continue;
            if (record != null) {
                JSONObject history = new JSONObject();
                String displayname = element.attr("DISPLAYNAME");
                if (CollectionUtils.isEmpty(deviceNameList) || !deviceNameList.contains(displayname)) continue;
                history.put("status",status);
                history.put("healthmessage",element.attr("HEALTHMESSAGE"));
                history.put("lastalarmtime",element.attr("LASTALARMTIME"));
                history.put("displayName",displayname);
                record.add(history);
            }
            if ("critical".equalsIgnoreCase(status)) critical+=1;
            else if ("warning".equalsIgnoreCase(status)) warning+=1;
            else if ("clear".equalsIgnoreCase(status)) clear+=1;
        }
        jsonObject.put("totalRecords",count);
        jsonObject.put("critical",critical);
        jsonObject.put("warning",warning);
        jsonObject.put("clear",clear);
    }

    public JSONObject getOrderJson(){
        try {
            String url = orderUrl + "/sdpapi/request?TECHNICIAN_KEY={TECHNICIAN_KEY}&OPERATION_NAME=GET_REQUESTS&format=json&INPUT_DATA={INPUT_DATA}";
            JSONObject input = new JSONObject();
            JSONObject operation = new JSONObject();
            JSONObject details = new JSONObject();
            details.put("from",0);
            details.put("limit",0);
            details.put("filterby","All_Requests");
            operation.put("details",details);
            input.put("operation",operation);
            String forObject = restTemplate.getForObject(url, String.class, orderAppkey, input.toJSONString());
            log.info("order:{}",forObject);
            if (StringUtils.isNotEmpty(forObject)) return JSON.parseObject(forObject);
            return new JSONObject();
        } catch (RestClientException e) {
            log.error(e.getMessage(),e);
            return new JSONObject();
        }
    }

    public JSONObject analyOrder(){
        JSONObject orderJson = getOrderJson();
        if (orderJson == null || orderJson.isEmpty()) return new JSONObject();
        JSONObject operation = orderJson.getJSONObject("operation");
        if (operation == null || operation.isEmpty()) return new JSONObject();
        JSONArray details = operation.getJSONArray("details");
        if (details == null || details.isEmpty()) return new JSONObject();
        JSONObject result = new JSONObject();
        int count = 0;
        int open = 0;
        int resolved = 0;
        int closed = 0;
        for (Object obj : details) {
            count += 1;
            JSONObject detail = JSON.parseObject(JSON.toJSONString(obj));
            String status = detail.getString("STATUS");
            if ("open".equalsIgnoreCase(status)) open += 1;
            else if ("resolved".equalsIgnoreCase(status)) resolved += 1;
            else if ("closed".equalsIgnoreCase(status)) closed += 1;
        }
        result.put("totalRecords",count);
        result.put("open",open);
        result.put("resolved",resolved);
        result.put("closed",closed);
        return result;
    }

}
