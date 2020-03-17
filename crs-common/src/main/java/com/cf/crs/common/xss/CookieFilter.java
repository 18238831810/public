/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.common.xss;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * cookie过滤
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
public class CookieFilter implements Filter {



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String scheme = req.getScheme();
        log.info("cookie filter:{}", scheme);
        if ("HTTPS".equalsIgnoreCase(scheme)){
            Cookie[] cookies = req.getCookies();
            for(Cookie cookie : cookies) {
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
            }
        }
        chain.doFilter(request,response);
    }

}
