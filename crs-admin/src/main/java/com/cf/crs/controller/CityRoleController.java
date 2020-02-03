package com.cf.crs.controller;

import com.cf.crs.entity.CityRole;
import com.cf.crs.service.CityRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/getRoleList")
    public Object selectList(){
        return cityRoleService.getRoleList();
    }

    @ApiOperation("新增角色")
    @PostMapping("/addRole")
    public Object addRole(CityRole cityRole){
        return cityRoleService.addRole(cityRole);
    }
    @ApiOperation("修改角色")
    @PostMapping("/updateRole")
    public Object updateRole(CityRole cityRole){
        return cityRoleService.updateRole(cityRole);
    }
    @ApiOperation("删除角色")
    @PostMapping("/deleteRole")
    public Object deleteRole(Integer id){
        return cityRoleService.deleteRole(id);
    }
}
