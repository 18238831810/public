package com.cf.crs.controller;

import com.cf.crs.service.CityUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author frank
 * 2019/12/1
 **/
@Api(tags="城管项目")
@RequestMapping("/city/user")
@RestController
public class CityUserController {

    @Autowired
    CityUserService cityUserService;

    @ApiOperation("获取所有用户")
    @GetMapping("/getUserList")
    public Object selectList(){
        return cityUserService.selectList();
    }

    @ApiOperation("设置用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "auth", value = "用户权限 0:无权限，1:管理员 2:普通权限", required = true, dataType = "Integer")
    })
    @GetMapping("/setAuth")
    public Object selectList(Integer id,Integer auth){
        return cityUserService.setAuth(id,auth);
    }
}
