/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.config.exception;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.ExceptionUtils;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.log.entity.SysLogErrorEntity;
import com.cf.crs.log.service.SysLogErrorService;
import com.cf.crs.common.utils.HttpContextUtils;
import com.cf.crs.common.utils.IpUtils;
import com.cf.crs.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class RenExceptionHandler {

	@Autowired
	private SysLogErrorService sysLogErrorService;

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RenException.class)
	public Result handleRenException(RenException ex){
		Result result = new Result();
		result.error(ex.getCode(), ex.getMsg());

		return result;
	}

	/**
	 * 权限异常
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public Result unauthorizedException(UnauthorizedException ex){
		log.error(ex.getMessage(), ex);
		return new Result().error(ErrorCode.FORBIDDEN,"权限不足");
	}

	/**
	 * 索引
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public Result handleDuplicateKeyException(DuplicateKeyException ex){
		Result result = new Result();
		result.error(ErrorCode.DB_RECORD_EXISTS);
		return result;
	}

	/**
	 * 全局异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception ex){
		log.error(ex.getMessage(), ex);
		saveLog(ex);
		return new Result().error();
	}

	/**
	 * 保存异常日志
	 */
	private void saveLog(Exception ex){
		SysLogErrorEntity log = new SysLogErrorEntity();

		//请求相关信息
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		log.setIp(IpUtils.getIpAddr(request));
		log.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
		log.setRequestUri(request.getRequestURI());
		log.setRequestMethod(request.getMethod());
		Map<String, String> params = HttpContextUtils.getParameterMap(request);
		if(MapUtil.isNotEmpty(params)){
			log.setRequestParams(JSON.toJSONString(params));
		}

		//异常信息
		log.setErrorInfo(ExceptionUtils.getErrorStackTrace(ex));

		//保存
		sysLogErrorService.save(log);
	}
}