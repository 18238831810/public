package com.cf.crs.controller;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.service.CheckSqlService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
@RequestMapping("/city/checkSql")
@RestController
public class CheckSqlController {

    @Autowired
    CheckSqlService checkSqlService;


    @ApiOperation("获取设备列表(服务器，中间件，数据库，互联网设备，工单，业务监测，页面可用性)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "type", value = "1:数据库 2:中间件 3:服务器 4 :物联网设备 5:工单 6:业务监测 7:页面可用性", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "waringType", value = "(不传代表所有信息)1:严重 2:一般 3:正常 (工单 1:Open 2:close 3:Resolved)", required = false, dataType = "Integer")
    })
    @PostMapping("/getCheckSqlList")
    public ResultJson<List<JSONObject>> getCheckSqlList(Integer type,Integer waringType){
        return checkSqlService.getCheckList(type,waringType);
    }

}
