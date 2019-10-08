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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_dict")
public class SysDictEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 上级ID，一级为0
	 */
	private Long pid;
	/**
	 * 字典类型
	 */
	@TableField(value = "dict_type")
	private String dictType;
	/**
	 * 字典名称
	 */
	@TableField(value = "dict_name")
	private String dictName;
	/**
	 * 字典值
	 */
	@TableField(value = "dict_value")
	private String dictValue;
	/**
	 * 备注
	 */
	private String remark;
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
}