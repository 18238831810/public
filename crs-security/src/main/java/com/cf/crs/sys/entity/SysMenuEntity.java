/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cf.crs.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 菜单管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("sys_menu")
@ApiModel(value = "菜单管理")
public class SysMenuEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 父菜单ID，一级菜单为0
	 */
	@ApiModelProperty(value = "上级ID")
	private Long pid;
	/**
	 * 菜单名称
	 */
	@ApiModelProperty(value = "菜单名称")
	@TableField(exist = false)
	private String name;
	/**
	 * 菜单URL
	 */
	@ApiModelProperty(value = "菜单URL")
	private String url;
	/**
	 * 授权(多个用逗号分隔，如：sys:user:list,sys:user:save)
	 */
	@ApiModelProperty(value = "授权(多个用逗号分隔，如：sys:user:list,sys:user:save)")
	private String permissions;
	/**
	 * 类型   0：菜单   1：按钮
	 */
	@ApiModelProperty(value = "类型  0:主菜单 1:window服务器 2:linux服务器 3:数据库")
	private Integer type;
	/**
	 * 菜单图标
	 */
	@ApiModelProperty(value = "菜单图标")
	private String icon;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "排序")
	private Integer sort;
	/**
	 * 排序
	 */
	@ApiModelProperty(value = "帮助")
	private String help;
	/**
	 * 更新者
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updater;
	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE,value = "update_date")
	private Date updateDate;

}