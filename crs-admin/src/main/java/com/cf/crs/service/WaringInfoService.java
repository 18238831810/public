package com.cf.crs.service;

import com.alibaba.fastjson.JSONArray;
import com.cf.crs.entity.WaringParam;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author frank
 * 2019/11/23
 **/
@Slf4j
@Service
public class WaringInfoService {

    @Value("${check.server.url}")
    String ServerUrl;

    @Value("${check.server.apikey}")
    String apiKey;

    @Autowired
    RestTemplate restTemplate;

    public ResultJson<JSONArray> listAlarms(WaringParam waringParam){
        try {
            String url = ServerUrl + "/api/json/alarm/listAlarms?apiKey=" + apiKey +"&severity={severity}&deviceName={deviceName}&Category={Category}&fromTime={fromTime}&toTime={toTime}";
            JSONArray forObject = restTemplate.getForObject(url, JSONArray.class, waringParam.getSeverity(), waringParam.getDeviceName(), waringParam.getCategory(), waringParam.getFromTime(), waringParam.getToTime());
            return HttpWebResult.getMonoSucResult(forObject);
        } catch (RestClientException e) {
            log.error(e.getMessage(),e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
    }

}
