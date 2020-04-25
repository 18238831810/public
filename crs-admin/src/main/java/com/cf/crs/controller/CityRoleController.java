package com.cf.crs.controller;

import com.cf.crs.entity.CityRole;
import com.cf.crs.service.CityRoleService;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author frank
 * 2019/12/1
 **/
@Api(tags="城管项目")
@RequestMapping("/city/role")
@RestController
public class CityRoleController {

    @Autowired
    CityRoleService cityRoleService;

    @ApiOperation("获取所有角色")
    @PostMapping("/getRoleList")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "登录用户", required = false, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "name", value = "用户名称", required = false, dataType = "String")
    })
    public ResultJson<List<CityRole>> selectList(@ApiIgnore Long id,@ApiIgnore String name){
        return cityRoleService.getRoleList(id,name);
    }

    @ApiOperation("新增角色")
    @PostMapping("/addRole")
    public ResultJson<String> addRole(CityRole cityRole){
        return cityRoleService.addRole(cityRole);
    }


    @ApiOperation("修改角色")
    @PostMapping("/updateRole")
    public ResultJson<String> updateRole(CityRole cityRole){
        return cityRoleService.updateRole(cityRole);
    }


    @ApiOperation("删除角色")
    @PostMapping("/deleteRole")
    public ResultJson<String> deleteRole(Integer id){
        return cityRoleService.deleteRole(id);
    }
}
