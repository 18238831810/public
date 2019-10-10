/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.sys.entity.SysMenuEntity;
import com.cf.crs.sys.mapper.SysMenuMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单管理
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Service
@Slf4j
public class SysMenuService {

	@Autowired
	SysMenuMapper sysMenuMapper;

	public ResultJson<List<SysMenuEntity>> getMenus(){
		List<SysMenuEntity> list = sysMenuMapper.selectList(new QueryWrapper<SysMenuEntity>());
		return HttpWebResult.getMonoSucResult(list);
	}


	public ResultJson<String> addMenu(SysMenuEntity sysMenuEntity){
		try {
			sysMenuMapper.insert(sysMenuEntity);
			return HttpWebResult.getMonoSucResult("suc");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return HttpWebResult.getMonoError(e.getMessage());
		}
	}

	public ResultJson<String> editMenu(String id,String name){
		try {
			sysMenuMapper.update(null,new UpdateWrapper<SysMenuEntity>().eq("id", id).set("name", name));
			return HttpWebResult.getMonoSucResult("suc");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return HttpWebResult.getMonoError(e.getMessage());
		}
	}

	public ResultJson<String> deleteMenu(String id){
		try {
			sysMenuMapper.deleteById(id);
			return HttpWebResult.getMonoSucResult("suc");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return HttpWebResult.getMonoError(e.getMessage());
		}
	}


}
