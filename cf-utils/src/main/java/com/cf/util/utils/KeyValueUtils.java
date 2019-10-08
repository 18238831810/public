package com.cf.util.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: Larry
 * @Date: Created in 2018/4/28
 */
public class KeyValueUtils {

    private static Map<String,Map<String,String>> valueMap = new HashMap();

    static {
        initValues("k1","0,不通过","1,通过");
        initValues("k2","0,失败","1,成功");
        initValues("k3","0,真实开户","1,模拟转真实");
        initValues("k4","2,二元素","3,三元素","4,四元素");
        initValues("k5","0,信息不一致","1,信息一致");
        initValues("k6","0,不需拦截","1,已拦截","2,拦截关闭");
        initValues("k7","0,人工","4,金道","5,麦讯通","6,至臻");
        initValues("k8","0,短信","1,邮件");
        initValues("k9","0,未使用","1,已使用","2,已失效","3,发送失败","4,接收失败");
        initValues("k10","0,外汇","1,贵金属","2,指数","3,商品");
        initValues("k11","0,客户","1,直接","2,间接");
        initValues("k12","N,未激活","A,已激活","S,冻结","BL,黑名单","D,销户","T,测试");
        initValues("k13","0,待提交","1,待审批","2,已成功","3,已取消");
        initValues("k14","DRAFT,待执行","PENDING,执行中","DISABLE,禁用","END,结束");
        initValues("k15","MIN,迷你","STD,标准","PLA,铂金","ZER,巴菲特","DIA,钻石","SUP,至尊");
        initValues("k16","1,LP名单","2,未完成开户","3,提交开户提醒","4,订阅信息","5,客户留言");
        initValues("k17","1,PC","2,M","3,APP");
        initValues("k18","0,未审批","1,审批失败","2,审批通过");
        initValues("k19","0,中文","1,英文","2,越南文");
        initValues("k20","min,迷你","std,标准","pla,铂金","zer,巴菲特","dia,钻石","sup,至尊");
        initValues("k21","1,支持","2,反对");
        initValues("k22","0,未审核","1,通过","2,未通过");
    }

    public static void initValues(String key,String... arr ){
        HashMap<String, String> child = new HashMap<>();
        for (String strings : arr) {
            String[] split = strings.split(",");
            child.put(split[0],split[1]);
        }
        valueMap.put(key,child);
    }

    public static String getLabel(String pKey,Object realKey){
        if (realKey == null||StringUtils.isEmpty(realKey.toString())) {
            return "";
        }
        String value = valueMap.get(pKey).get(String.valueOf(realKey));
        return value==null?"":value;
    }

    public static String formatCTime(Object value){
        if (value == null) {
            return "";
        }
        Long aLong = Long.valueOf(value.toString());
        return aLong>0?"已激活":"未激活";
    }


    /**
     *
     * @param value
     * @return
     */
    public static String fmtUserType(Object value){
        if (value == null) return "";
        if(value.toString().indexOf("8")==0)
            return "真实";
        return "模拟";
    }
}
