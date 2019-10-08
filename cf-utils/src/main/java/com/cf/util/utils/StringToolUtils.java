package com.cf.util.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author spark
 * @title: StringToolUtils
 * @description: 字符串工具类
 * @date 2019/9/914:39
 */
public class StringToolUtils {

    /**
     * 三目运算
     *
     * @param frontStr
     * @param afterStr
     * @return
     */
    public static String ternaryOperation(String frontStr, String afterStr) {
        return StringUtils.isNotBlank(frontStr) ? frontStr : afterStr;
    }

}
