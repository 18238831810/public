package com.cf.crs.controller;

import com.cf.crs.entity.CheckResult;
import com.cf.crs.service.CheckResultService;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 考评结果
 * @author frank
 * 2019/10/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkResult")
@RestController
public class CheckResultController {

    @Autowired
    CheckResultService checkResultService;


    @ApiOperation("获取考评结果")
    @GetMapping("/getCheckResult")
    public ResultJson<List<CheckResult>> getCheckInfo(){
        return checkResultService.getCheckResult();
    }

    @ApiOperation("手动考评")
    @GetMapping("/startCheck")
    @ApiImplicitParam(paramType="query", name = "id", value = "考评对象id（id为空或0，则全部考评,对应一键全部考评功能）", required = false, dataType = "Integer")
    public ResultJson<List<CheckResult>> startCheck(Long id){
        checkResultService.startCheck(id,1);
        return HttpWebResult.getMonoSucStr();
    }



}
