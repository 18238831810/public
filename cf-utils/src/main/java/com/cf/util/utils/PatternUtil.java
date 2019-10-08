package com.cf.util.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/22.
 */
public class PatternUtil {
    /**
     * url分割截取来源、媒介、广告、广告组、关键字等
     * @param key
     * @param url
     * @return
     */
    public static String url(String key,String url){
        String value = "";
        Pattern p = Pattern.compile(key+"([^&]*)");// key开头，&结尾的文档
        Matcher m = p.matcher(url);
        if (m.find()) {
            value = m.group(1);
        }
        return value;
    }


    public static void main(String[] args){

        String url = "utm_source=334&utm_medium=a1&utm_campaign=qq-danguanjianci-pc&utm_content=qiquanduanyu&utm_term=sqpcpc.00002";

         System.out.println(url("utm_term=",url));

    }
}
