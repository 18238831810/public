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
import lombok.Data;

import java.io.Serializable;

/**
 * 国际化
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("sys_language")
public class SysLanguageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	@TableField(value = "table_name")
	private String tableName;
	/**
	 * 表主键
	 */
	@TableField(value = "table_id")
	private Long tableId;
	/**
	 * 字段名
	 */
	@TableField(value = "field_name")
	private String fieldName;
	/**
	 * 字段值
	 */
	@TableField(value = "field_value")
	private String fieldValue;
	/**
	 * 语言
	 */
	private String language;

}