package com.cf.cache.util;


import org.apache.commons.lang3.StringUtils;

/**
 * 线程分等级执行,目前分6个等级，相关于6个线程，在实际的EnableCFCache注解时，对fiexd值进行分类
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/11
 */
public enum BetweenTimeKey {
    ZeroToThirtySeconds("one",0,30,10),
    ThirtyToFiveMinute("two",30,5*60,30),
    FiveToThirtyMinute("three",5*60,30*60,5*60),
    ThirtyMinuteToMaxHour("four",30*60,Long.MAX_VALUE,30*60);

    //区间最小值
    private long min;
    //区间最大值
    private long max;
    //线程执行间隔
    private long interval;
    //区间key
    private String key;


    BetweenTimeKey(String key, long min,long max,long interval) {
        this.key=key;
        this.min = min;
        this.max = max;
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }


    public long getMin() {
        return min;
    }


    public long getMax() {
        return max;
    }


    public String getKey() {
        return key;
    }

    /**
     * 根据定时执行时长，获取对应的枚举
     * @param seconds
     * @return
     */
    public static BetweenTimeKey getByValue(long seconds)
    {
        for (BetweenTimeKey betweenTimeKey: BetweenTimeKey.values())
        {
            if(betweenTimeKey.min<=seconds && betweenTimeKey.max>seconds)
                return betweenTimeKey;
        }
        return BetweenTimeKey.ThirtyMinuteToMaxHour;
    }

    /**
     * 根据key键，获取相应的枚举
     * @param key
     * @return
     */
    public static BetweenTimeKey getByKey(String  key)
    {
        if(StringUtils.isBlank(key))return BetweenTimeKey.ThirtyMinuteToMaxHour;
        for (BetweenTimeKey betweenTimeKey: BetweenTimeKey.values())
        {
            if(betweenTimeKey.key.equalsIgnoreCase(key))
                return betweenTimeKey;
        }
        return BetweenTimeKey.ThirtyMinuteToMaxHour;
    }

}
