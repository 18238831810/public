/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.mapper;

import com.cf.crs.sys.entity.SysMenuEntity;
import com.cf.crs.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单管理
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysMenuMapper extends BaseDao<SysMenuEntity> {

	/**
	 * 查询用户权限列表
	 * @param userId  用户ID
	 */
	List<String> getUserPermissionsList(Long userId);

	/**
	 * 查询所有权限列表
	 */
	List<String> getPermissionsList();


}
