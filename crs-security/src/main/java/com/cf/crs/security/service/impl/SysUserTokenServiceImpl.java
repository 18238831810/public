/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.service.impl;

import com.cf.crs.common.constant.Constant;
import com.cf.crs.common.service.impl.BaseServiceImpl;
import com.cf.crs.common.utils.ConvertUtils;
import com.cf.crs.common.utils.Result;
import com.cf.crs.security.mapper.SysUserTokenDao;
import com.cf.crs.security.entity.SysUserTokenEntity;
import com.cf.crs.security.oauth2.TokenGenerator;
import com.cf.crs.security.service.ShiroService;
import com.cf.crs.security.service.SysUserTokenService;
import com.cf.crs.security.user.UserDetail;
import com.cf.crs.sys.dto.SysUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserTokenServiceImpl extends BaseServiceImpl<SysUserTokenDao, SysUserTokenEntity> implements SysUserTokenService {
	/**
	 * 12小时后过期
	 */
	private final static int EXPIRE = 3600 * 12;

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	private ShiroService shiroService;

	@Autowired
	HttpServletRequest request;

	@Autowired
	HttpServletResponse response;

	@Override
	public Result createToken(SysUserDTO user) {
		String token = TokenGenerator.generateValue();
		saveToken(user, token);
		Map<String, Object> map = new HashMap<>(2);
		map.put(Constant.TOKEN_HEADER, token);
		map.put("expire", EXPIRE);
		return new Result().ok(map);
	}

	@Override
	public void logout(Long userId) {
		//生成一个token
		String token = TokenGenerator.generateValue();
		//修改token
		baseDao.updateToken(userId, token);
	}

	public void saveToken(SysUserDTO user, String token){
		UserDetail userDetail = ConvertUtils.sourceToTarget(user, UserDetail.class);
		//用户权限列表
		Set<String> permsSet = shiroService.getUserPermissions(userDetail);
		userDetail.setPermsSet(permsSet);
		redisTemplate.boundValueOps(token).set(userDetail, EXPIRE, TimeUnit.SECONDS);
		/*Subject subject = SecurityUtils.getSubject();
		subject.login(new Oauth2Token(token));*/
	}
}