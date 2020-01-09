package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author frank
 * 2019/11/18
 **/
@Slf4j
@Service
public class CheckServerService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${check.server.url}")
    private String url;

    @Value("${check.server.name}")
    private String name;

    @Value("${check.server.apikey}")
    private String apikey;

    public JSONObject getServers(){
        String serverUrl = url+"/api/json/discovery/getInfrastructureDetailsView?apiKey="+apikey+"&categoryName="+name;
        log.info("checkServerUrl:{}",serverUrl);
        return restTemplate.getForObject(serverUrl, JSONObject.class);
    }

    public List<JSONObject> getServerList(){
        List array = Lists.newArrayList();
        JSONObject servers = getServers();
        if (servers == null || servers.isEmpty()) return array;
        log.info("serverResult:{}",servers.toJSONString());
        JSONObject infrastructureDetailsView = servers.getJSONObject("InfrastructureDetailsView");
        if (infrastructureDetailsView == null ||  servers.isEmpty()) return array;
        Integer totalRecords = infrastructureDetailsView.getInteger("TotalRecords");
        if (totalRecords == null) return array;
        JSONArray details = infrastructureDetailsView.getJSONArray("Details");
        return details.stream().map(obj-> JSON.parseObject(JSON.toJSONString(obj))).collect(Collectors.toList());
    }

    /**
     * 获取对应告警的的服务器数据
     * @param waringType
     * @return
     */
    public ResultJson<List<JSONObject>> serverList(Integer waringType){
        List<JSONObject> serverList = getServerList();
        if (waringType == null) return HttpWebResult.getMonoSucResult(getServerList());
        List<JSONObject> list = serverList.stream().filter(jsonObject -> {
            int severity = jsonObject.getIntValue("severity");
            if (waringType == 1) return severity == 1;
            else if (waringType == 3) return severity == 5;
            else return severity != 1 && severity != 5;
        }).collect(Collectors.toList());
        return HttpWebResult.getMonoSucResult(list);
    }

    /**
     * 性能考评结果
     * @param deviceName
     * @return
     */
    public JSONObject checkAvailabilt(String deviceName){
        String serverUrl = url+"/api/json/device/getAvailabiltyGraphData?apiKey="+apikey+"&deviceName="+deviceName+"&period=YESTERDAY";
        log.info("checkServerAvailabiltUrl:{}",serverUrl);
        return restTemplate.getForObject(serverUrl, JSONObject.class);
    }
}
