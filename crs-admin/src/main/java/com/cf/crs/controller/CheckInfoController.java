package com.cf.crs.controller;

import com.cf.crs.entity.CheckInfo;
import com.cf.crs.entity.CheckMenu;
import com.cf.crs.mapper.CheckInfoMapper;
import com.cf.crs.service.CheckInfoService;
import com.cf.crs.service.CheckMenuService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/getCheckInfo")
    public ResultJson<List<CheckInfo>> getCheckInfo(){
        return checkInfoService.getCheckInfo();
    }

    @ApiOperation("新增考评对象信息")
    @GetMapping("/addCheckInfo")
    public ResultJson<String> addCheckInfo(CheckInfo checkInfo){
        return checkInfoService.addCheckInfo(checkInfo);
    }

    @ApiOperation("修改考评对象信息")
    @GetMapping("/updateCheckInfo")
    public ResultJson<String> updateCheckInfo(CheckInfo checkInfo){
        return checkInfoService.updateCheckInfo(checkInfo);
    }


}
