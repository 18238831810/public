/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.user;

import com.cf.crs.common.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public class SecurityUser {


    public static Subject getSubject() {
        try {
            return SecurityUtils.getSubject();
        }catch (Exception e){
            return null;
        }
    }

    public static String getRequestToken(HttpServletRequest httpRequest) {
        //从header中获取token
        String token = httpRequest.getHeader(Constant.TOKEN_HEADER);

        //如果header中不存在token，则从参数中获取token
        if (StringUtils.isBlank(token)) {
            token = httpRequest.getParameter(Constant.TOKEN_HEADER);
        }
        return token;
    }

    /**
     * 获取用户信息
     */
    public static UserDetail getUser() {
        Subject subject = getSubject();
        if (subject == null) return new UserDetail();
        UserDetail userDetail = (UserDetail)subject.getPrincipal();
        if(userDetail == null){
            return new UserDetail();
        }
        return userDetail;
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return getUser().getId();
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId() {
        return getUser().getDeptId();
    }

}