package com.cf.crs.controller;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.service.CheckModeService;
import com.cf.crs.service.WaringService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    WaringService waringService;


    @ApiOperation("获取告警统计信息")
    @GetMapping("/getWaring")
    public ResultJson<JSONObject> getWaring(){
        return waringService.analyWaring();
    }

}
