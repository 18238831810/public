/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.log.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cf.crs.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_log_operation")
public class SysLogOperationEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户操作
	 */
	private String operation;
	/**
	 * 请求URI
	 */
	@TableField(value = "request_uri")
	private String requestUri;
	/**
	 * 请求方式
	 */
	@TableField(value = "request_method")
	private String requestMethod;
	/**
	 * 请求参数
	 */
	@TableField(value = "request_params")
	private String requestParams;
	/**
	 * 请求时长(毫秒)
	 */
	@TableField(value = "request_time")
	private Integer requestTime;
	/**
	 * 用户代理
	 */
	@TableField(value = "user_agent")
	private String userAgent;
	/**
	 * 操作IP
	 */
	private String ip;
	/**
	 * 状态  0：失败   1：成功
	 */
	private Integer status;
	/**
	 * 用户名
	 */
	@TableField(value = "creator_name")
	private String creatorName;
}