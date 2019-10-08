/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cf.crs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关系
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_role_menu")
public class SysRoleMenuEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 角色ID
	 */
	@TableField(value = "role_id")
	private Long roleId;
	/**
	 * 菜单ID
	 */
	@TableField(value = "menu_id")
	private Long menuId;

}
