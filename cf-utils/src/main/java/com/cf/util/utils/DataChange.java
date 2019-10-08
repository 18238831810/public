package com.cf.util.utils;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据类型转换
 * @author frank
 * @date 2019/3/29
 */
public class DataChange {
	static Logger log = LogManager.getLogger(DataChange.class);
	/**
	 * 字符串时间转long
	 * @param timeStr
	 * @return
	 */
	public static long timeToLong(String timeStr)
	{
		if(StringUtils.isBlank(timeStr)) return 0;
		try {
			return DateUtil.string2Date(timeStr, DateUtil.TIMESTAMP).getTime();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return 0;
	}
	/**
	 * 用户UI类型转成数字型
	 * @param uiType
	 * @return
	 */
	public static int uiTypeToInt(String uiType)
	{
		if(StringUtils.isBlank(uiType)) return 0;
		String regex = "[a-zA-Z]+[(](\\d+)[)]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(uiType);
		while(matcher.find())
		{
			String result = matcher.group(1);
			if(NumberUtils.isNumber(result))
				return Integer.parseInt(result);
		}
		return 0;
	}
	/**
	 * 用户登陆类型
	 * @param loginTypeStr
	 * @return
	 */
	public static int loginTypeToInt(String loginTypeStr)
	{
		
		if(loginTypeStr.toLowerCase().indexOf("login")>=0)
			return 1;
		if(loginTypeStr.toLowerCase().indexOf("logout")>=0)
			return 2;
		else return 0;
	}

	/**
	 * ob的转成int
	 * @param ob
	 * @return
	 */
	public static int obToInt(Object ob)
	{
		return obToInt( ob,0);
	}
	public static int obToInt(Object ob,int defVal)
	{
		if(ob==null) return 0;
		if(!NumberUtils.isNumber(ob.toString().trim())) return 0;
		return Integer.parseInt(ob.toString().trim());
	}
	/**
	 * ob的转成Long
	 * @param ob
	 * @return
	 */
	public static long obToLong(Object ob)
	{
		return obToLong( ob,0);
	}
	
	/**
	 * ob的转成Long
	 * @param ob
	 * @return
	 */
	public static long obToLong(Object ob,long defVal)
	{
		if(ob==null) return defVal;
		if(!NumberUtils.isNumber(ob.toString().trim())) return defVal;
		return Long.parseLong(ob.toString().trim());
	}
	
	/**
	 * ob的转成Double
	 * @param ob
	 * @return
	 */
	public static double obToDouble(Object ob)
	{
		return obToDouble(ob,0);
	}
	public static double obToDouble(Object ob, double defVal) {
		if(ob==null) return defVal;
		if(!NumberUtils.isNumber(ob.toString().trim())) return defVal;
		return Double.valueOf(ob.toString().trim());
	}
	/**
	 * ob的转成String
	 * @param ob
	 * @return
	 */
	public static String obToString(Object ob)
	{
		return obToString( ob,"");
	}
	
	/**
	 * ob的转成String
	 * @param ob
	 * @return
	 */
	public static String obToString(Object ob,String defVal)
	{
		if(ob==null) return defVal;
		return ob.toString().trim().replaceAll("'", "");
	}
	
	public static String obToIp(Object ips)
	{
		if(ips==null) return "";
		return	ips.toString().split(":")[0].replaceAll("'", "");
	}
}
