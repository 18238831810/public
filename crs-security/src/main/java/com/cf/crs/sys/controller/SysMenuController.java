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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 菜单管理
 * @author frank
 */
@Api(tags="城管项目")
@RestController
@RequestMapping("/public/sys/menu")
public class SysMenuController {
	@Autowired
	private SysMenuService sysMenuService;

	@GetMapping("nav")
	@ApiOperation("分页")
	public ResultJson<List<SysMenuEntity>> getMenus(){
		return sysMenuService.getMenus();
	}



}