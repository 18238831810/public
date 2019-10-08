package com.cf.util.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;


/**
 * @author hjy
 * @create 2019-08-01 17:02
 * @Description: 验签类
 **/
public class VerifyParamData {


    /**
     * 方法用途: 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序），并且生成url参数串<br>
     * 实现步骤: <br>
     *
     * @param paraMap    要排序的Map对象
     * @param urlEncode  是否需要URLENCODE
     * @param keyToLower 是否需要将Key转换为全小写 true:key转化成小写，false:不转化
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap,
                                      boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(
                    tmpMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Entry<String, String>>() {
                @Override
                public int compare(Entry<String, String> o1,
                                   Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(
                            o2.getKey());
                }
            });
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Entry<String, String> item : infoIds) {
                if (!StringUtils.isEmpty(item.getKey())) {
                    String key = item.getKey();
                    Object valueObj = item.getValue();
                    String val = "";
                    if ("sign".equals(key)) {
                        continue;
                    }
                    if (null == valueObj) {
                        val = "";
                    } else if (valueObj instanceof String[]) {
                        String[] values = (String[]) valueObj;
                        for (int i = 0; i < values.length; i++) {
                            val = values[i] + ",";
                        }
                        val = val.substring(0, val.length() - 1);
                    } else {
                        val = valueObj.toString();
                    }
                    System.err.println(key + ":" + val);
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


    /**
     * 验证签名
     *
     * @param request request的参数map		request.getParameterMap()
     * @return
     * @Title: verifySign
     * @return: boolean        正确返回true	,否则返回false
     * @author: hjy
     * @date: 2019-08-01 17:02
     */
    public static boolean verifySign(HttpServletRequest request, String apiKey) {
        Map<String, String[]> paraMap = request.getParameterMap();
        Map<String, String> tempMap = new HashMap(paraMap);
        tempMap.put("apiKey", apiKey);
        String sign = request.getHeader("sign");
        if (StringUtils.isEmpty(sign)) {
            return false;
        }
        String url = VerifyParamData.formatUrlMap(tempMap, false, false);
        System.err.println("签名的url:" + url);
        String addSign = Md5Util.md5(url);
        System.err.println("Java签名:" + addSign);
        if (sign.toLowerCase().equals(addSign)) {
            return true;
        }
        return false;
    }


}
