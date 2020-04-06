package com.cf.crs.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.WaringParam;
import com.cf.crs.service.WarningInfoService;
import com.cf.crs.service.WarningService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 考评计划
 * @author frank
 * 2019/10/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/waring")
@RestController
public class WaringController {

    @Autowired
    WarningService warningService;

    @Autowired
    WarningInfoService warningInfoService;


    @ApiOperation("首页告警统计")
    @PostMapping("/getWaring")
    public ResultJson<JSONObject> getWaring(){
        return warningService.analyWaring();
    }

    @ApiOperation("获取告警统计信息")
    @PostMapping("/listAlarms")
    public ResultJson<JSONArray> listAlarms(WaringParam waringParam){
        return warningInfoService.listAlarms(waringParam);
    }


}
