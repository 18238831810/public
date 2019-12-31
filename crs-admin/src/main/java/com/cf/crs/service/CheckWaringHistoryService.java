package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;

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

    /**
     * 同步告警数据
     */
    public void synWaringHistory(){
        long now = System.currentTimeMillis();
        JSONObject analyRecord = new JSONObject();
        JSONObject waringRecord = new JSONObject();
        waringRecord.put("time",now);
        //统计服务器数据
        ArrayList<JSONObject> serverList = Lists.newArrayList();
        JSONObject jsonObject = waringService.analyServer(serverList);
        waringRecord.put("server",serverList);



    }

    private JSONObject trantAnalyData(JSONObject jsonObject){

        if (!DataUtil.jsonNotEmpty(jsonObject)){

        }

        return null;
    }



}
