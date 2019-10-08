/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cf.crs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 部门管理
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_dept")
public class SysDeptEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 上级ID
	 */
	private Long pid;
	/**
	 * 所有上级ID，用逗号分开
	 */
	private String pids;
	/**
	 * 部门名称
	 */
	private String name;
	/**
	 * 排序
	 */
	private Integer sort;
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
	/**
	 * 上级部门名称
	 */
	@TableField(exist = false)
	private String parentName;

}