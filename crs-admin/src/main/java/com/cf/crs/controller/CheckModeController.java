package com.cf.crs.controller;

import com.cf.crs.entity.CheckMode;
import com.cf.crs.entity.CheckPlan;
import com.cf.crs.service.CheckModeService;
import com.cf.crs.service.CheckPlanService;
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
@RequestMapping("/city/checkMode")
@RestController
public class CheckModeController {

    @Autowired
    CheckModeService checkModeService;


    @ApiOperation("获取考评配置")
    @GetMapping("/getCheckPlan")
    public ResultJson<List<CheckMode>> getCheckPlan(){
        return checkModeService.getCheckMode();
    }

    @ApiOperation("保存考评配置")
    @PostMapping("/updateCheckMode")
    public ResultJson<String> updateCheckPlan(String list){
        return checkModeService.updateCheckMode(list);
    }
}