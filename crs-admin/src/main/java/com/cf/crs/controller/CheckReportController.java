package com.cf.crs.controller;

import com.cf.crs.entity.CheckReport;

import com.cf.crs.service.CheckReportService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiImplicitParam;
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
    @ApiOperation("获取考评报表列表")
    public ResultJson<List<CheckReport>> getReportList(){
        return checkReportService.getReportList();
    }

    @PostMapping("/addCheckReport")
    @ApiOperation("新建考评报表列表")
    public ResultJson<String> addReportList(CheckReport checkReport){
        return checkReportService.addReportList(checkReport);
    }

    @PostMapping("/updateReportList")
    @ApiOperation("编辑考评报表列表")
    public ResultJson<String> updateReportList(CheckReport checkReport){
        return checkReportService.updateReportList(checkReport);
    }

    @PostMapping("/deleteReportList")
    @ApiOperation("删除考评报表列表")
    @ApiImplicitParam(paramType="query", name = "id", value = "考评报表id", required = true, dataType = "Integer")
    public ResultJson<String> deleteReportList(Integer id){
        return checkReportService.deleteReportList(id);
    }


}
