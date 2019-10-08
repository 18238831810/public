package com.cf.cache.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableMapCFCache {

    /**
     *过些掉某些key,exitKeys[]={"a","b"},则如若方法执行时，对执行出来的结果remove掉key=a,key=b这两个
     * @return
     */
    String[] filterKeys() default {};
    /**
     *  哪些key不缓存,比如说方法在配置时有filterKeys[]={"a","b"},则如若方法执行时，发现结果中有包含其中一个key(a或b)
     *  则此次执行不进行缓存
     * @return
     */
    String[] exitKeys() default {};
    /**
     * 如若执行的结果中的value=指定的值，则不进行缓存
     * @return
     */
    String[] exitValues() default {};

    /**
     * 如若含有key与value匹配的则不缓存,比如{“a=b”,"c=d"}
     * @return
     */
    String[] exitKeyValues() default {};

}
