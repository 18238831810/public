package com.cf.anno;

import java.lang.annotation.*;

/**
 * <p>Description: 游客类，写在接口方法或类上，用于区分接口是否可以游客方式访问</p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/3/28
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tourist {
    //=true代表是游客 false代表非游客
    boolean is() default false;
}
