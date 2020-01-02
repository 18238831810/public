package com.cf.util.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	public final static String DEFAULT = "yyyyMMdd";
	public final static String YEAR_MONTH = "yyyyMM";
	public final static String YEAR_LINE_MONTH = "yyyy-MM";
	public final static String CHINA = "yyyy年MM月dd";
	public final static String PATTERN = "yyyy-MM-dd";
	public final static String TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
	public final static String MINTIMESTAMP = "yyyy-MM-dd HH:mm";
	public final static String MISTIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String HOURMISECOND = "HHmmss";
	public final static String HH_MM_SS = "HH:mm:ss";
	public final static String HH = "HH:mm";
	public final static String MMDD ="MM/dd";

	
	/**
	 * 日期转字符串，例如：2014-05-12/2014-5-12 ==》 “2014-05-12”
	 * 
	 * @param date
	 *            需要转字符串的日期
	 * @return 日期字符串
	 */
	public static String date2String(Date date, String type) {
		if (type == null) type = PATTERN;
		SimpleDateFormat df = new SimpleDateFormat(type);
		return df.format(date);
	}
	
	public static Date string2Date(String strDate, String pattern)
	{
		if(StringUtils.isBlank(strDate)) return null;
		try {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			return df.parse(strDate);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	/**
	 * 时区转换
	 * @param date         时间
	 * @param timeZoneId   时区
	 * @param pattern      日期字符串，格式：yyyy-MM-dd
	 * @return
	 */
	public static String getTimeZone(Date date, String timeZoneId, String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		//非空判断
		if (StringUtils.isEmpty(timeZoneId)){
			timeZoneId = "GMT+8";
		}
		try {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		}catch (Exception e){
		}
		return sdf.format(date);
	}

	/**
	 * 时区转换
	 * @param dateValue    时间字符串
	 * @param timeZoneId   时区
	 * @param pattern      日期字符串，格式：yyyy-MM-dd
	 * @return
	 */
	public static String getTimeZone(String dateValue, String timeZoneId, String pattern){
		Date date = DateUtil.string2Date(dateValue, pattern);
		if (date == null) return "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		//非空判断
		if (StringUtils.isEmpty(timeZoneId)){
			timeZoneId = "GMT+8";
		}
		try {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		}catch (Exception e){
		}
		return sdf.format(date);
	}


	/**
	 * 
	 * @param dateValue 日期字符串，格式：yyyy-MM-dd
	 * @return
	 */
	public static long parseDate(String dateValue){
		return parseDate(dateValue, null);
	}
	
	/**
	 * 
	 * @param dateValue 日期字符串
	 * @param dateFormat 日期格式，默认：yyyy-MM-dd
	 * @return
	 */
	public static long parseDate(String dateValue, String dateFormat){
		long date = 0;
		try {
			date = new SimpleDateFormat(dateFormat == null ? PATTERN : dateFormat).parse(dateValue).getTime();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return date;
	}
	// 毫秒转时间字符串
	public static String timesToDate(long times, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(times));
	}


	// 获取当前是哪一年
	public static int getCurrentYear() {
		Calendar c = Calendar.getInstance();
		//c.setTime(new Date());
		return c.get(Calendar.YEAR);
	}

	// 获取当前是月 从1开始
	public static int getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		//c.setTime(new Date());
		return c.get(Calendar.MONTH) + 1;
	}

	public static Calendar getBoreFullDay(int day) 
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, day);
		return cal;
	}
	public static Calendar getBoreDay(Calendar cal,int day) {
		cal.add(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

	public static Calendar getBoreDay(int day) {
		Calendar cal = Calendar.getInstance();
		return getBoreDay( cal, day);
	}
	
	public static Calendar getBoreDay(long times ,int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(times);
		return  getBoreDay(cal,day) ;
	}
	public static Calendar getBoreMinue(int minue) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minue);
		return cal;
	}

	public static Calendar getBoreHour(int hour) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, hour);
		return cal;
	}
	
	public static Calendar getCalendar(long times)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(times);
		return cal;
	}
	
	public static Calendar getBoreMinue(long time , int minue) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.add(Calendar.MINUTE, minue);
		return cal;
	}
	
	
	public static Calendar getBoreMonth(long times,int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(times);
		if(month!=0)
		{
			cal.add(Calendar.MONTH, month);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal;
	}
	
	public static Calendar getBoreMonth(int month) {
		Calendar cal = Calendar.getInstance();
		if(month!=0)
		{
			cal.add(Calendar.MONTH, month);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal;
	}
	
	
	
	public static int getYear(long millis){
		Calendar c = Calendar.getInstance();
		if(millis > 0){
			c.setTimeInMillis(millis);
		}
		return c.get(Calendar.YEAR);
	}
	
	
	public static int getMonth(long millis){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		return c.get(Calendar.MONTH) + 1;
	}
	
	
	
	/**
	 * time 秒
	 * @param time
	 * @return ["01", "56", "36"]时  分  秒
	 */
	public static String[] getRemain(long time){
		if(time > 0){
			long hour = time / 3600;
			long minute = (time - hour * 3600) / 60;
			long seconds = time - hour * 3600 - minute * 60;

			return new String[]{hour < 9 ? "0" + hour : String.valueOf(hour), minute < 10 ? "0" + minute : String.valueOf(minute), seconds < 10 ? "0" + seconds : String.valueOf(seconds)};	
		}
		return new String[]{"00", "00", "00"};
	}
	/**
	 * 获取当前时间的前一天
	 * @return
	 */
	public static Date getYesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
	/**
	 * 获取当前时间的前一个星期
	 * @return
	 */
	public static Date getLastWeek() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		return cal.getTime();
	}


	/**
	 * 获取设定时间的当天开始时间
	 * @param millisTime   设定时间
	 * @return
	 */
	public static Date getStartTime(long millisTime) {
		Calendar todayStart = Calendar.getInstance();
		todayStart.setTimeInMillis(millisTime);
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime();
	}

	/**
	 * 获取设定时间的当天开始时间
	 * @param millisTime   设定时间
	 * @return
	 */
	public static Date getEndTime(long millisTime) {
		Calendar todayEnd = Calendar.getInstance();
		todayEnd.setTimeInMillis(millisTime);
		todayEnd.set(Calendar.HOUR_OF_DAY, 23);
		todayEnd.set(Calendar.MINUTE, 59);
		todayEnd.set(Calendar.SECOND, 59);
		todayEnd.set(Calendar.MILLISECOND, 999);
		return todayEnd.getTime();
	}

	/**
	 * 时间格式化（注意LocalDate只能获得到天数）
	 * @param localDate
	 * @param type
	 * @return
	 */
	public static String formatDate(LocalDate localDate,String type){
		if (StringUtils.isBlank(type)) type = PATTERN;
		return localDate.format(DateTimeFormatter.ofPattern(type));
	}
	/**
	 * 时间格式化（注意LocalDate只能获得到天数）
	 * @param time
	 * @param type
	 * @return
	 */
	public static LocalDate localDateParse(String time,String type){
	    if (StringUtils.isBlank(time)) return null;
		if (StringUtils.isBlank(type)) type = PATTERN;
		return LocalDate.parse(time,DateTimeFormatter.ofPattern(type));
	}

	/**
	 * 时间格式化（注意LocalTime只能获得到时分秒）
	 * @param localTime
	 * @param type
	 * @return
	 */
	public static String formatDate(LocalTime localTime,String type){
		if (StringUtils.isBlank(type)) type = HH_MM_SS;
		return localTime.format(DateTimeFormatter.ofPattern(type));
	}

	/**
	 * 时间格式化（注意LocalTime只能获得到时分秒）
	 * @param time
	 * @param type
	 * @return
	 */
	public static LocalTime localTimeParse(String time,String type){
		try {
			if (StringUtils.isBlank(time)) return null;
			if (StringUtils.isBlank(type)) type = HH_MM_SS;
			return LocalTime.parse(time,DateTimeFormatter.ofPattern(type));
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 时间格式化
	 * @param localDateTimeTime
	 * @param type
	 * @return
	 */
	public static String formatDate(LocalDateTime localDateTimeTime, String type){
		if (StringUtils.isBlank(type)) type = TIMESTAMP;
		return localDateTimeTime.format(DateTimeFormatter.ofPattern(type));
	}

	/**
	 * 时间格式化
	 * @param time
	 * @param type
	 * @return
	 */
	public static LocalDateTime localDateTimeParse(String time,String type){
		try {
			if (StringUtils.isBlank(time)) return null;
			if (StringUtils.isBlank(type)) type = TIMESTAMP;
			return LocalDateTime.parse(time,DateTimeFormatter.ofPattern(type));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param dateValue 日期字符串，格式：yyyy-MM-dd HH:mm:ss.SSS+00 （格林时间）
	 * @return 加8小时转为北京时间
	 */
	public static long parseDateForGts2(String dateValue){
		try {
			if (StringUtils.isEmpty(dateValue)) return 0L;
			dateValue = dateValue.trim().substring(0,23);
			long time = parseDate(dateValue, MISTIMESTAMP);
			return time==0?0:time+8000*60*60;
		} catch (Exception e) {
			return 0L;
		}
	}
	/**
	 * @param dateValue 日期字符串，格式：yyyy-MM-dd HH:mm:ss.SSS+00
	 * @return
	 */
	public static long parseDateForKafka(String dateValue){
		try {
			if (StringUtils.isEmpty(dateValue)) return 0L;
			dateValue = dateValue.trim().substring(0,23);
			return parseDate(dateValue, MISTIMESTAMP);
		} catch (Exception e) {
			return 0L;
		}
	}
	/**
	 * @param dateValue 日期字符串，格式：yyyy-MM-dd HH:mm:ss.SSS+00
	 * @return
	 */
	public static long parseDateForTimestamp(String dateValue){
		Long time = 0L;
		try {
			if (StringUtils.isEmpty(dateValue)) return 0L;
			if (dateValue.length() < 25) {
				dateValue = dateValue.trim().substring(0,19).replace("+","0");
				time = parseDate(dateValue,TIMESTAMP);
			}else{
				dateValue = dateValue.trim().substring(0,23).replace("+","0");
				time = parseDate(dateValue, MISTIMESTAMP);
			}
			return time==0?0:time+8000*60*60;
		} catch (Exception e) {
			return 0L;
		}
	}
	/**
	 *
	 * @param dateValue long型时间，格式：yyyy-MM-dd HH:mm:ss.SSS+00
	 * @return
	 */
	public static LocalDateTime timeToLocalDateTime(Long dateValue){
		if (dateValue == null) return null;
		return localDateTimeParse(timesToDate(dateValue,TIMESTAMP),null);
	}

	/**
	 * 将long型时间变时分秒为0的long型时间
	 * @param dateValue long型时间
	 * @return
	 */
	public static Long timeToDay(Long dateValue){
		if (dateValue == null) return 0L;
		if (dateValue <= 0) return 0L;
		return getStartTime(dateValue).getTime();
	}

	/**
	 * 获得本周的最后一天，周日
	 *
	 * @return
	 */
	public static Date getCurrentWeekDayEndTime() {
		Calendar c = Calendar.getInstance();
		try {
			int weekday = c.get(Calendar.DAY_OF_WEEK);
			c.add(Calendar.DATE, 8 - weekday);
			SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
			c.setTime(longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c.getTime();
	}
	
	/**
	 * 
	 * @Title: getMonth   
	 * @Description: 获取月份
	 * @param: @param date
	 */
	public static String getMonth(Date date){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String monthString = formatter.format(date);
		return monthString;
	}

	/**
	 * 判断指定时间的间隔是否在指定的天数之内
	 * @param fromDate
	 * @param toDate
	 * @param num
	 * @return
	 */
	public static boolean isBetweenDay(Date fromDate, Date toDate, int num) {
		if (fromDate == null || toDate == null || num == 0) {
			return false;
		}
		if (fromDate.after(toDate)) {
			return false;
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(fromDate);
		cal.add(Calendar.DATE, num);
		return cal.getTime().after(toDate);
	}

	/**
	 * 获取当前时区的小时
	 * @param timeZone
	 * @return
	 */
	public static String getHourString(String timeZone){
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
		String dateString = formatter.format(new Date());
		return dateString;
	}

	/**
	 * 时区转化转化字符串时间
	 * @param fromTimeZone
	 * @param toTimeZone
	 * @param dateString
	 * @return
	 */
	public static String getZoneDateString(String fromTimeZone,String toTimeZone,String dateString){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		format.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
		try{
			Date date1 = format.parse(dateString);
			format2.setTimeZone(TimeZone.getTimeZone(toTimeZone));
			dateString = format2.format(date1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateString;
	}

	/**
	 * 把字符串格式成date
	 * 格式:yyyy-MM-dd HH:mm:ss
	 * @param datetime
	 * @return
	 */
	public static Date getDateOfyyyyMMddHHmmss(String datetime) {
		Date date=new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = formatter.parse(datetime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 是否超过24小时
	 * @param checktime
	 * @return
	 */
	public static boolean checkOver24Date(Long checktime) {
		boolean b = true;
		Date d = new Date();
		Long lastTime = d.getTime() - 24 * 60 * 1000 * 60;
		if (checktime > lastTime) b = false;
		return b;
	}


	public static String getWeekByDate(Date time) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		return date2String(cal.getTime(),DEFAULT);
	}


}
