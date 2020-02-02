package com.cf.crs.controller;

import com.cf.crs.service.CityMenuService;
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
@RequestMapping("/city/menu")
@RestController
public class CityMenuController {

    @Autowired
    CityMenuService cityMenuService;

    @ApiOperation("获取所有菜单")
    @GetMapping("/getUserList")
    public Object selectList(){
        return cityMenuService.getMenuList();
    }
}
