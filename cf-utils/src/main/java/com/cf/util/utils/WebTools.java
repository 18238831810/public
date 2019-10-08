package com.cf.util.utils;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


public class WebTools {
    
	private static final String REQUEST_HEADER_USER_AGENT_KEY = "User-Agent";
	
	public static String getFirstIpAddress(String ipaddr) {
		if (ipaddr.indexOf(",") != -1) {
			return ipaddr.substring(0, ipaddr.indexOf(","));
		} else {
			return ipaddr;
		}
	}
	
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");     

        if(isInvalidIp(ip)) {     
            ip = request.getHeader("Proxy-Client-IP");     
        }

        if(isInvalidIp(ip)) {     
            ip = request.getHeader("WL-Proxy-Client-IP");     
        }

        if(isInvalidIp(ip)) {     
            ip = request.getRemoteAddr();     
        }

        return getFirstIpAddress(ip);
    }

    private static boolean isInvalidIp(String ip) {
        final String UNKNOWN_IP = "unknown";

        return ip == null || ip.length() == 0 || UNKNOWN_IP.equalsIgnoreCase(ip);
    }
    
    public static String getCookie(HttpServletRequest request, String cookieName) {;
        Cookie cookies[] = request.getCookies();

        for(Cookie cookie : cookies) {
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        
        return null;
    }

    public static void setRedirect(HttpServletResponse httpResponse, String url) {
        httpResponse.setHeader("Location", url);
        httpResponse.setHeader("Connection", "close");
        
        httpResponse.setStatus(301);
    }
        
    public static String getParameterUrl(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<String> e = request.getParameterNames();
        
        while(e.hasMoreElements()) {
            String paramName = e.nextElement();
            
            sb.append(paramName);
            sb.append("=");
            sb.append(request.getParameter(paramName));
        }
        
        return sb.toString();
    }
    
    public static final String getDns(HttpServletRequest request) {
    	return request != null ? request.getServerName() : null;
    }
    
    public static String getUserAgent(HttpServletRequest request) {
    	return request.getHeader(REQUEST_HEADER_USER_AGENT_KEY);
    }
    

	/**
	 * Clear a list of cookie by names
	 * @param request
	 * @param response
	 * @param names
	 */
	public static void clearCookies(HttpServletRequest request, HttpServletResponse response, String... names) {
		List<String> removeList = Arrays.asList(names);
		String domain = request.getServerName().replaceFirst("www.", "");
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {				 
				if (removeList.contains(cookies[i].getName())) {
					cookies[i].setValue("");
					cookies[i].setPath("/");
					cookies[i].setMaxAge(0);
					cookies[i].setDomain(domain);					
					response.addCookie(cookies[i]);
				}
			}
		}
	}
    
}
