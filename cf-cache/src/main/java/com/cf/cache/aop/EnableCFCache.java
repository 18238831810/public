package com.cf.cache.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 如若需要定时刷新，uuid一定要有唯一值，fixed>0 同时方便参数中最后一个参数必须为boolean值,用于代表刷新或从缓存中取(true刷新，false直接 从缓存中)
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/3
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableCFCache {

    /**
     * key前缀
     */
    String prefix() default "cache:";
    /**
     * key主体，spel表示，例：#id（取形参中id的值）
     */
    String fieldKey() default  "";
    /**
     * 过期时间,单位秒
     */
    int expire() default 3600;

    /**
     * 条件
     * @return
     */
    String condition() default "";

    CacheOperation operation() default CacheOperation.QUERY;

    /**
     * 缓存操作类型
     */
    enum CacheOperation {
        QUERY, // 查询
        UPDATE, // 修改
        DELETE // 删除
    }

    CacheType type() default CacheType.STRING;

    /**
     * 缓存操作类型
     */
    enum CacheType {
        STRING, //key->value 型的String
        MAP // key->key->value型的MAP
    }

    /**
     * 定时刷新的时间
     * @return 单位秒
     */
    long fixed()  default 0;

    /**
     * 是否是刷新操作。此值不需要设计，如何设置为true,那则每次会从DB中取
     * @return
     */
     boolean flush() default  false;

}
