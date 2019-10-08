/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.crs.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.oauth2;

import com.alibaba.fastjson.JSON;
import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.utils.Result;
import com.cf.crs.security.user.SecurityUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * oauth2过滤器
 *
 * @author Mark sunlightcs@gmail.com
 */
public class Oauth2Filter extends AuthenticatingFilter {


    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token
        String token =  SecurityUser.getRequestToken((HttpServletRequest) request);

        if (StringUtils.isBlank(token)) {
            return null;
        }

        return new Oauth2Token(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (((HttpServletRequest) request).getMethod().equals(RequestMethod.OPTIONS.name())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //获取请求token，如果token不存在，直接返回401
        String token =  SecurityUser.getRequestToken((HttpServletRequest) request);
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (StringUtils.isBlank(token)) {
            String origin = ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN);
            httpResponse.setContentType("application/json;charset=utf-8");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);

            String json = JSON.toJSONString(new Result().error(ErrorCode.UNAUTHORIZED));

            httpResponse.getWriter().print(json);

            return false;
        }
        Subject subject = SecurityUtils.getSubject();
		subject.login(new Oauth2Token(token));
        return true;
    }


    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String origin = ((HttpServletRequest) request).getHeader(HttpHeaders.ORIGIN);
        httpResponse.setContentType("application/json;charset=utf-8");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Origin", origin == null ? "*" : origin);
        try {
            //处理登录失败的异常
            Throwable throwable = e.getCause() == null ? e : e.getCause();
            Result r = new Result().error(HttpStatus.SC_UNAUTHORIZED, throwable.getMessage());

            String json = JSON.toJSONString(r);
            httpResponse.getWriter().print(json);
        } catch (IOException e1) {

        }

        return false;
    }

}