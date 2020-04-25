package com.cf.crs.controller;

import com.cf.crs.entity.CityUser;
import com.cf.crs.entity.SysUser;
import com.cf.crs.service.CityUserService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PostMapping("/getUserList")
    public ResultJson<List<CityUser>> selectList(){
        return cityUserService.selectList();
    }

    @ApiOperation("设置用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "auth", value = "用户角色 0:无权限，1:管理员 2:普通权限(角色id,多个角色id以逗号隔开)", required = true, dataType = "String")
    })
    @PostMapping("/setAuth")
    public Object setRole(Integer id,String auth){
        return cityUserService.setRole(id,auth);
    }


    @ApiOperation("更改用户信息")
    @PostMapping("/updateUser")
    public ResultJson<String> updateUser(SysUser sysUser){
        return cityUserService.updateUser(sysUser);
    }

    @ApiOperation("新增用户信息")
    @PostMapping("/addUser")
    public ResultJson<String> addUser(SysUser sysUser){
        return cityUserService.addUser(sysUser);
    }


    @ApiOperation("删除用户信息")
    @PostMapping("/addUser")
    @ApiImplicitParam(paramType="query", name = "id", value = "用户id", required = true, dataType = "Integer")
    public ResultJson<String> deleteUser(Long id){
        return cityUserService.deleteUser(id);
    }


    @ApiOperation("更改当前用户密码")
    @PostMapping("/updatePassword")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "用户id", required = true, dataType = "Integer"),
            @ApiImplicitParam(paramType="query", name = "oldPassword", value = "旧密码", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "newPassword", value = "新密码", required = true, dataType = "String")
    })
    public ResultJson<String> updatePassword(Long id,String oldPassword,String newPassword){
        return cityUserService.updatePassword(id,oldPassword,newPassword);
    }
}
