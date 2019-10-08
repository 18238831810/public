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
 * 异常日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_log_error")
public class SysLogErrorEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

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
	 * 用户代理
	 */
	@TableField(value = "user_agent")
	private String userAgent;
	/**
	 * 操作IP
	 */
	private String ip;
	/**
	 * 异常信息
	 */
	@TableField(value = "error_info")
	private String errorInfo;

}