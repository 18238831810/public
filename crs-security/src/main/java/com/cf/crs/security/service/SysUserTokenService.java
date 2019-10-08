/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.service;

import com.cf.crs.security.entity.SysUserTokenEntity;
import com.cf.crs.common.service.BaseService;
import com.cf.crs.common.utils.Result;
import com.cf.crs.sys.dto.SysUserDTO;

/**
 * 用户Token
 * 
 * @author Mark sunlightcs@gmail.com
 */
public interface SysUserTokenService extends BaseService<SysUserTokenEntity> {

	/**
	 * 生成token
	 * @param user  用户ID
	 */
	Result createToken(SysUserDTO user);
	/**
	 * 退出，修改token值
	 * @param userId  用户ID
	 */
	void logout(Long userId);

}