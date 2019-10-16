package com.cf.crs.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leek
 * @description 拦截器
 * @date 2019/8/13 15:06
 */
@Configuration
@Slf4j
public class ActionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("url:{},params:{}",request.getServletPath(),JSONObject.toJSONString(request.getParameterMap()));
        long startTime = System.currentTimeMillis();
        request.setAttribute("request_per_handleTime", startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request.getServletPath().equals("/error")) {
            //不打印
        } else {
            long startTime = (Long) request.getAttribute("request_per_handleTime");
            request.removeAttribute("request_per_handleTime");
            long endTime = System.currentTimeMillis();
            log.info("url:{},time:{}",request.getServletPath(),endTime - startTime);
        }
    }

}
