package com.cf.crs.controller;

import com.cf.crs.service.CheckModeService;
import com.cf.crs.service.CheckObjectAnalyService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author frank
 * 2019/11/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkObjectAnaly")
@RestController
public class CheckObjectAnalyController {

    @Autowired
    CheckObjectAnalyService checkObjectAnalyService;


    @ApiOperation("获取考评对象分析结果")
    @GetMapping("/getCheckModeAnaly")
    public ResultJson<List> getAnalyResult(){
        return checkObjectAnalyService.getAnalyResult();
    }
}
