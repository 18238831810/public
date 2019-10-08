/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.service.impl;

import com.cf.crs.security.user.UserDetail;
import com.cf.crs.common.constant.Constant;
import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.service.impl.BaseServiceImpl;
import com.cf.crs.common.utils.ConvertUtils;
import com.cf.crs.common.utils.HttpContextUtils;
import com.cf.crs.common.utils.TreeUtils;
import com.cf.crs.sys.mapper.SysMenuDao;
import com.cf.crs.sys.dto.SysMenuDTO;
import com.cf.crs.sys.entity.SysMenuEntity;
import com.cf.crs.sys.enums.SuperAdminEnum;
import com.cf.crs.sys.service.SysLanguageService;
import com.cf.crs.sys.service.SysMenuService;
import com.cf.crs.sys.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuDao, SysMenuEntity> implements SysMenuService {
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysLanguageService sysLanguageService;

	@Override
	public SysMenuDTO get(Long id) {
		SysMenuEntity entity = baseDao.getById(id, HttpContextUtils.getLanguage());

		SysMenuDTO dto = ConvertUtils.sourceToTarget(entity, SysMenuDTO.class);

		return dto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysMenuDTO dto) {
		SysMenuEntity entity = ConvertUtils.sourceToTarget(dto, SysMenuEntity.class);

		//保存菜单
		insert(entity);
		saveLanguage(entity.getId(), "name", entity.getName());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysMenuDTO dto) {
		SysMenuEntity entity = ConvertUtils.sourceToTarget(dto, SysMenuEntity.class);

		//上级菜单不能为自身
		if(entity.getId().equals(entity.getPid())){
			throw new RenException(ErrorCode.SUPERIOR_MENU_ERROR);
		}

		//更新菜单
		updateById(entity);
		saveLanguage(entity.getId(), "name", entity.getName());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id) {
		//删除菜单
		deleteById(id);

		//删除角色菜单关系
		sysRoleMenuService.deleteByMenuId(id);
	}

	@Override
	public List<SysMenuDTO> getAllMenuList(Integer type) {
		List<SysMenuEntity> menuList = baseDao.getMenuList(type, HttpContextUtils.getLanguage());

		List<SysMenuDTO> dtoList = ConvertUtils.sourceToTarget(menuList, SysMenuDTO.class);

		return TreeUtils.build(dtoList, Constant.MENU_ROOT);
	}

	@Override
	public List<SysMenuDTO> getUserMenuList(UserDetail user, Integer type) {
		List<SysMenuEntity> menuList;

		//系统管理员，拥有最高权限
		if(user.getSuperAdmin() == SuperAdminEnum.YES.value()){
			menuList = baseDao.getMenuList(type, HttpContextUtils.getLanguage());
		}else {
			menuList = baseDao.getUserMenuList(user.getId(), type, HttpContextUtils.getLanguage());
		}

		List<SysMenuDTO> dtoList = ConvertUtils.sourceToTarget(menuList, SysMenuDTO.class);

		return TreeUtils.build(dtoList);
	}

	@Override
	public List<SysMenuDTO> getListPid(Long pid) {
		List<SysMenuEntity> menuList = baseDao.getListPid(pid);

		return ConvertUtils.sourceToTarget(menuList, SysMenuDTO.class);
	}

	private void saveLanguage(Long tableId, String fieldName, String fieldValue){
		sysLanguageService.saveOrUpdate("sys_menu", tableId, fieldName, fieldValue, HttpContextUtils.getLanguage());
	}

}