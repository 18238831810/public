/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.service;


import com.cf.crs.sys.entity.SysRoleEntity;
import com.cf.crs.common.page.PageData;
import com.cf.crs.common.service.BaseService;
import com.cf.crs.sys.dto.SysRoleDTO;

import java.util.List;
import java.util.Map;


/**
 * 角色
 * 
 * @author Mark sunlightcs@gmail.com
 */
public interface SysRoleService extends BaseService<SysRoleEntity> {

	PageData<SysRoleDTO> page(Map<String, Object> params);

	List<SysRoleDTO> list(Map<String, Object> params);

	SysRoleDTO get(Long id);

	void save(SysRoleDTO dto);

	void update(SysRoleDTO dto);

	void delete(Long[] ids);

}
