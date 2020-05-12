package com.cf.crs.controller;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.CheckInfo;
import com.cf.crs.service.CheckInfoService;
import com.cf.crs.service.CheckIotService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 考评对象信息
 * @author frank
 * 2019/10/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/iot")
@RestController
public class CheckIotController {

    @Autowired
    CheckIotService checkIotService;


    @ApiOperation("获取物联网设备信息信息")
    @PostMapping("/getIotInfo")
    public ResultJson<List<JSONObject>> getCheckInfo(){
        return checkIotService.getIotInfo();
    }




}
