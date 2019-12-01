/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.controller;

import com.cf.crs.sys.entity.SysMenuEntity;
import com.cf.crs.sys.service.SysMenuService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * 菜单管理
 * @author frank
 */
//@Api(tags="城管项目")
@RestController
@RequestMapping("/public/sys/menu")
public class SysMenuController {
	@Autowired
	private SysMenuService sysMenuService;

	@GetMapping("/nav")
	//@ApiOperation("菜单栏")
	public ResultJson<List<SysMenuEntity>> getMenus(){
		return sysMenuService.getMenus();
	}

	@GetMapping("/add")
	@ApiOperation("添加菜单栏")
	public ResultJson<String> addMenu(SysMenuEntity sysMenuEntity){
		return sysMenuService.addMenu(sysMenuEntity);
	}

	@GetMapping("/edit")
	//@ApiOperation("编辑菜单栏")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", paramType = "query", required = true, dataType="String") ,
			@ApiImplicitParam(name = "编辑名称",  paramType = "query",required = true, dataType="String") ,
	})
	public ResultJson<String> editMenu(@ApiIgnore String id,@ApiIgnore String name){
		return sysMenuService.editMenu(id,name);
	}

	@GetMapping("/delete")
	//@ApiOperation("删除菜单栏")
	//@ApiImplicitParams({
	//		@ApiImplicitParam(name = "id", paramType = "query", required = true, dataType="String") ,
	//})
	public ResultJson<String> deleteMenu(@ApiIgnore String id){
		return sysMenuService.deleteMenu(id);
	}



}