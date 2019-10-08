package com.cf.util.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description: HTTP工具类</p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/1
 */
public class HttpCookieUtil {

    /**
     * 获取某个cookie的值
     * @param request
     * @param key
     * @return
     */
    public Cookie  getCookie(HttpServletRequest request,String key)
    {
        if(request==null) return null;
        Cookie[] cookie=	request.getCookies();
        if(cookie==null ||cookie.length<=0) return null;
        //查询登陆的cookie
        for (Cookie cookie2 : cookie) {
            if(key.equals(cookie2.getName()))
            {
               return cookie2;
            }
        }
        return null;
    }

}
