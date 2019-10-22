package com.cf.crs.controller;

import com.alibaba.fastjson.JSONArray;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.service.CheckModeService;
import com.cf.crs.service.CheckObjectService;
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
 * 考评对象
 * @author frank
 * 2019/10/22
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkObject")
@RestController
public class CheckObjectController {

    @Autowired
    CheckObjectService checkObjectService;


    @ApiOperation("获取考评对象")
    @GetMapping("/getCheckPlan")
    public ResultJson<JSONArray> getCheckObject(){
        return checkObjectService.getCheckObject();
    }

    @ApiOperation("保存考评对象")
    @PostMapping("/updateCheckObject")
    public ResultJson<String> updateCheckObject(String list){
        return checkObjectService.updateCheckObject(list);
    }
}
