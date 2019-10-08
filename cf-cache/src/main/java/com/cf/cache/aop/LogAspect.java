package com.cf.cache.aop;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/5/5
 */
@Aspect
@Component
@Slf4j
/*@Profile({"uat","dev"})*/
public class LogAspect implements Ordered {

    ThreadLocal<Long> local =new ThreadLocal<>();

    private static final long toLong=1000;

    // ..表示包及子包 该方法代表controller层的所有方法
    @Pointcut("execution(public * com.cf.cache.controller..*.*(..))")
    public void controllerMethod() {
        log.info("controllerMethod");
    }


    @Before("controllerMethod()")
    public void logRequestInfo(JoinPoint joinPoint) throws Exception {

        HttpServletRequest request =  getRequest();

        StringBuffer requestLog = new StringBuffer();
        if(request!=null)
        requestLog.append("controller.detail->")
                .append("URL = {" + request.getRequestURI() + "},\t")
                .append("HTTP_METHOD = {" + request.getMethod() + "},\t")
                .append("IP = {" + request.getRemoteAddr() + "},\t")
                .append("CLASS_METHOD = {" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "},\t");

        if(joinPoint.getArgs().length == 0) {
            requestLog.append("ARGS = {} ");
        } else {
            requestLog.append("ARGS = " + new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(joinPoint.getArgs()[0]) + "");
        }
        log.info(requestLog.toString());
    }

    private HttpServletRequest getRequest()
    {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes==null) return null;
        return  attributes.getRequest();
    }

    @After("controllerMethod()")
    public void after() {
        HttpServletRequest request =  getRequest();
        long cost =System.currentTimeMillis()-local.get();
        if(cost>toLong)
             log.info("url->[{}] cost->[{}]s", request.getRequestURI(),cost);
        local.remove();
    }

    @Before("controllerMethod()")
    public void before() {
        local.set( System.currentTimeMillis());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
