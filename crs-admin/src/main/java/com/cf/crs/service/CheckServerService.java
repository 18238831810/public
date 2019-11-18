package com.cf.crs.service;

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
        List details = infrastructureDetailsView.getJSONArray("Details");
        return details;
    }

    public ResultJson<List<JSONObject>> serverList(){
        return HttpWebResult.getMonoSucResult(getServerList());
    }
}
