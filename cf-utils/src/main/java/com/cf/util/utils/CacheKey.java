package com.cf.util.utils;

/**
 * Copyright (c) 2017
 *
 * @ClassName: CacheKey.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: hui
 * @version: V1.0
 * @Date: 2017年7月3日 上午9:25:50
 */
public class CacheKey {

	//游客身份标识（主要用于极验）
	public static  final  String VISITOR_TOKEN_KEY ="visitorId";
	//mis后台菜单缓存
	public static final String cf_mis_menu="cf:mis:menu";
	//信用卡银行列表
	public static final String CARD_BANK_LIST ="card:band:list";
	//信用卡广告列表
	public static final String CARD_ADVERTISING_LIST ="card:advertising:list";
	//信用卡广告列表
	public static final String CARD_VERSION ="card:version";

	public static final String CF_CRS_GW_BASE_TOKEN = "cf:new:crs:gw:base:token:%s";

	//kafka,mq等开发控制
	public static final String AUTO_CTRL = "auto:ctrl";

	public static final String ACCONT_COUNTRYLIST_VN = "accont:countrylist:vn";

	public static final String ACCONT_PHONE_PREFIX_LIST_VN = "accont:mobile:prefix:list:vn";

	public static final String REAL_ACCOUNT_OPEN_START_TIME = "real:account:open:start:time";

	public static final String CFVN_OPEN_REAL_ACCOUNT_LIST = "cfvn:open:real:account:list";

}
 