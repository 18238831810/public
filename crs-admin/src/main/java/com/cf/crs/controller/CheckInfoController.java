package com.cf.crs.controller;

import com.cf.crs.entity.CheckInfo;
import com.cf.crs.service.CheckInfoService;
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
@RequestMapping("/city/checkInfo")
@RestController
public class CheckInfoController {

    @Autowired
    CheckInfoService checkInfoService;


    @ApiOperation("获取考评对象信息")
    @PostMapping("/getCheckInfo")
    public ResultJson<List<CheckInfo>> getCheckInfo(){
        return checkInfoService.getCheckInfo();
    }

    @ApiOperation("新增考评对象信息")
    @PostMapping("/addCheckInfo")
    public ResultJson<String> addCheckInfo(CheckInfo checkInfo){
        return checkInfoService.addCheckInfo(checkInfo);
    }

    @ApiOperation("修改考评对象信息")
    @PostMapping("/updateCheckInfo")
    public ResultJson<String> updateCheckInfo(CheckInfo checkInfo){
        return checkInfoService.updateCheckInfo(checkInfo);
    }

    @ApiOperation("删除考评对象信息或考评设备")
    @PostMapping("/deleteCheckInfo")
    @ApiImplicitParam(paramType="query", name = "id", value = "考评对象id", required = true, dataType = "Integer")
    public ResultJson<String> deleteCheckInfo(Integer id){
        return checkInfoService.deleteCheckInfo(id);
    }

    @ApiOperation("编辑考评对象信息安全")
    @PostMapping("/updateCheckInfoSecurity")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "考评对象id", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "informationSecurity", value = "信息安全（json字符串）", required = true, dataType = "Integer")

    })
    public ResultJson<String> updateCheckInfoSecurity(@ApiIgnore CheckInfo checkInfo){
        return checkInfoService.updateCheckInfoSecurity(checkInfo);
    }

    @ApiOperation("新增考评设备")
    @PostMapping("/addCheckDevice")
    public ResultJson<String> addCheckDevice(CheckInfo checkInfo){
        return checkInfoService.addCheckDevice(checkInfo);
    }

    @ApiOperation("修改考评设备")
    @PostMapping("/updateCheckDevice")
    public ResultJson<String> updateCheckDevice(CheckInfo checkInfo){
        return checkInfoService.updateCheckDevice(checkInfo);
    }

    @ApiOperation("获取考评计划")
    @PostMapping("/getCheckPlan")
    public ResultJson<List<CheckInfo>> getCheckPlan(){
        return checkInfoService.getCheckPlan();
    }

    @ApiOperation("修改考评计划(全局配置时id传0)")
    @PostMapping("/updateCheckPlan")
    public ResultJson<String> updateCheckPlan(CheckInfo checkInfo){
        return checkInfoService.updateCheckPlan(checkInfo);
    }


}
