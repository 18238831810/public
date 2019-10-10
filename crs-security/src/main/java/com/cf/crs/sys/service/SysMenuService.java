/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.sys.entity.SysMenuEntity;
import com.cf.crs.sys.mapper.SysMenuMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单管理
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysMenuService {

	@Autowired
	SysMenuMapper sysMenuMapper;

	public ResultJson<List<SysMenuEntity>> getMenus(){
		List<SysMenuEntity> list = sysMenuMapper.selectList(new QueryWrapper<SysMenuEntity>());
		return HttpWebResult.getMonoSucResult(list);
	}


}
