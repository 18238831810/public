package com.cf.crs.controller;

import com.cf.crs.service.CityOrganizationService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags="城管项目")
@RequestMapping("/city/organization")
@RestController
public class CityOrganizationController {

    @Autowired
    CityOrganizationService organizationService;


    @ApiOperation("设置部门角色")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "部门id", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "auth", value = "用户角色 0:无权限，1:管理员 2:普通权限(角色id,多个角色id以逗号隔开)", required = true, dataType = "String")
    })
    @RequestMapping
    public ResultJson<String> setRole(Integer id, String auth){
        return organizationService.setRole(id,auth);
    }
}
