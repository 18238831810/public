package com.cf.util.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取国际化message数据
 * @author frank
 * 2019/3/29
 **/
public class MessageUtil {

    private static final ThreadLocal<Map<String,String>> threadLocal = new ThreadLocal<>();

    /**
     * 获取国际化数据
     * @param messageSource   要获取的国际化配置对象
     * @param message    国际化数据key
     * @return
     */
    public static String getMessage(MessageSource messageSource,String message){
        return messageSource.getMessage(message,null, org.springframework.util.StringUtils.parseLocale(getLang()));
    }
    
	/**
	 * 
	 * @Title: getMessage   
	 * @Description: 带参数国际化
	 * @param: @param messageSource
	 * @param: @param args
	 * @param: @param message   
	 * @throws
	 */
    public static String getMessage(MessageSource messageSource,String message,Object[] args){
        return messageSource.getMessage(message,args, org.springframework.util.StringUtils.parseLocale(getLang()));
    }

    /**
     * 设置语言标识
     * @param key
     * @param value
     */
    public static void setMessageSource(String key,String value){
        Map<String, String> map = threadLocal.get();
        if (map == null) {
            map = new HashMap<String, String>();
            threadLocal.set(map);
        }
        map.put(key, value);
    }

    public static String getLang(){
        if(threadLocal.get()==null) return null;
        String lang= threadLocal.get().get("lang");
        if(StringUtils.isBlank(lang)) return null;
        return lang;
    }

    /**
     * 清空threadlocal
     */
    public static void remove(){
        threadLocal.remove();
    }

}
