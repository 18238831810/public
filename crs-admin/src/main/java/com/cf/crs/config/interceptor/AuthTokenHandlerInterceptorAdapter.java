package com.cf.crs.config.interceptor;

import com.cf.crs.service.CityRoleService;
import com.cf.crs.service.CityTokenService;
import com.cf.util.exception.CfMisAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/3/28
 */
@Slf4j
public class AuthTokenHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CityTokenService cityTokenService;

    private static String loginUrl = "/city/user/login";

    @Autowired
    CityRoleService cityRoleService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        //登录接口放过，其他接口拦截教验权限
        if (requestURI.indexOf(loginUrl) != -1) return true;
        //获取token
        String token = cityTokenService.getToken();
        if (StringUtils.isBlank(token) || !redisTemplate.hasKey(token) || !redisTemplate.hasKey(token+":menu")) {
            log.error("token.error:[{}]", request.getRequestURI());
            throw new UnauthorizedException();
        }
        redisTemplate.expire(token,2, TimeUnit.HOURS);
        redisTemplate.expire(token+":menu",2, TimeUnit.HOURS);
        return true;
    }





    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("请求url:{}", request.getServletPath());
    }

}
