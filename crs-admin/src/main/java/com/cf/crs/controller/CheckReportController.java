package com.cf.crs.controller;

import com.cf.crs.entity.CheckReport;
import com.cf.crs.entity.CheckWarningHistory;
import com.cf.crs.service.CheckReportService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author frank
 * 2020/1/12
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkReport")
@RestController
public class CheckReportController {

    @Autowired
    CheckReportService checkReportService;

    @PostMapping("/getCheckReport")
    @ApiOperation("获取考评报表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "type", value = "1:日报表 2:周 3:月 4:年", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "time", value = "日期（日:20200106 周:20200106(每周周一) 月:202001 年:2020）", required = true, dataType = "Integer")
    })
    public ResultJson<List<CheckReport>> getReport(Integer type, String time){
        return checkReportService.getReport(type,time);
    }

    @PostMapping("/setCheckReportScore")
    @ApiOperation("修改考评报表总分")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "type", value = "1:日报表 2:周 3:月 4:年", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "time", value = "日期（日:20200106 周:20200106(每周周一) 月:202001 年:2020）", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "displayName", value = "考评对象的displayName", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "score", value = "修改的总分", required = true, dataType = "Integer")
    })
    public ResultJson<List<CheckReport>> getReport(Integer type, String time,String displayName,Integer score){
        return checkReportService.setReportScore(type, time, displayName, score);
    }

    @PostMapping("/getCheckReportWarningDetails")
    @ApiOperation("获取考评报警详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "type", value = "1:日报表 2:周 3:月 4:年", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "time", value = "日期（日:20200106 周:20200106(每周周一) 月:202001 年:2020）", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "displayName", value = "考评对象的displayName", required = true, dataType = "String")
    })
    public ResultJson<List<CheckWarningHistory>> getReportWarningDetails(Integer type, String time, String displayName){
        return checkReportService.getReportWarningDetails(type, time, displayName);
    }
}
