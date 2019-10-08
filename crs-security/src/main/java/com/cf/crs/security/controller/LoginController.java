/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.controller;

import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.utils.Result;
import com.cf.crs.common.validator.AssertUtils;
import com.cf.crs.common.validator.ValidatorUtils;
import com.cf.crs.log.entity.SysLogLoginEntity;
import com.cf.crs.log.enums.LoginOperationEnum;
import com.cf.crs.log.enums.LoginStatusEnum;
import com.cf.crs.log.service.SysLogLoginService;
import com.cf.crs.security.user.SecurityUser;
import com.cf.crs.security.user.UserDetail;
import com.cf.crs.sys.dto.SysUserDTO;
import com.cf.crs.sys.enums.UserStatusEnum;
import com.cf.crs.security.dto.LoginDTO;
import com.cf.crs.security.password.PasswordUtils;
import com.cf.crs.security.service.CaptchaService;
import com.cf.crs.sys.service.SysUserService;
import com.cf.crs.security.service.SysUserTokenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 登录
 * 
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@Api(tags="登录管理")
public class LoginController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private CaptchaService captchaService;
	@Autowired
	private SysLogLoginService sysLogLoginService;

	@GetMapping("captcha")
	@ApiOperation(value = "验证码", produces="application/octet-stream")
	@ApiImplicitParam(paramType = "query", dataType="string", name = "uuid", required = true)
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		//uuid不能为空
		AssertUtils.isBlank(uuid, ErrorCode.IDENTIFIER_NOT_NULL);

		//生成图片验证码
		BufferedImage image = captchaService.create(uuid);

		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		out.close();
	}

	@PostMapping("login")
	@ApiOperation(value = "登录")
	public Result login(HttpServletRequest request, @RequestBody LoginDTO login) {
		//效验数据
		ValidatorUtils.validateEntity(login);

		//验证码是否正确
		boolean flag = captchaService.validate(login.getUuid(), login.getCaptcha());
		if(!flag){
			return new Result().error(ErrorCode.CAPTCHA_ERROR);
		}

		//用户信息
		SysUserDTO user = sysUserService.getByUsername(login.getUsername());

		//用户不存在
		if(user == null){
			throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
		}

		//密码错误
		if(!PasswordUtils.matches(login.getPassword(), user.getPassword())){
			throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
		}

		//账号停用
		if(user.getStatus() == UserStatusEnum.DISABLE.value()){
			throw new RenException(ErrorCode.ACCOUNT_DISABLE);
		}

		//登录成功
		return sysUserTokenService.createToken(user);
	}

	@PostMapping("logout")
	@ApiOperation(value = "退出")
	public Result logout(HttpServletRequest request) {
		UserDetail user = SecurityUser.getUser();

		//退出
		sysUserTokenService.logout(user.getId());

		//用户信息
		return new Result();
	}
	
}