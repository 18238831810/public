package com.cf.crs.service;

import com.alibaba.fastjson.JSONArray;
import com.cf.crs.entity.WaringParam;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class WarningInfoService {

    @Value("${check.server.url}")
    String ServerUrl;

    @Value("${check.server.apikey}")
    String apiKey;

    @Autowired
    RestTemplate restTemplate;

    public ResultJson<JSONArray> listAlarms(WaringParam waringParam){
        try {
            String url = ServerUrl + "/api/json/alarm/listAlarms?apiKey=" + apiKey;
            if (StringUtils.isNotEmpty(waringParam.getSeverity())) url += ("&severity=" + waringParam.getSeverity());
            if (StringUtils.isNotEmpty(waringParam.getDeviceName())) url += ("&deviceName=" + waringParam.getDeviceName());
            if (StringUtils.isNotEmpty(waringParam.getCategory())) url += ("&Category=" + waringParam.getCategory());
            if (StringUtils.isNotEmpty(waringParam.getFromTime())) url += ("&fromTime=" + waringParam.getFromTime());
            if (StringUtils.isNotEmpty(waringParam.getToTime())) url += ("&toTime=" + waringParam.getToTime());
            log.info("waringUrl:{}",url);
            JSONArray forObject = restTemplate.getForObject(url, JSONArray.class);
            return HttpWebResult.getMonoSucResult(forObject);
        } catch (RestClientException e) {
            log.error(e.getMessage(),e);
            return HttpWebResult.getMonoSucResult(new JSONArray());
        }
    }

}
