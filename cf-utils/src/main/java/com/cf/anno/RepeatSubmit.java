package com.cf.anno;

import java.lang.annotation.*;

/**
 * @author spark
 * @title: RepeatSubmit
 * @description: 防止重复提交注解
 * @date 2019/5/27
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatSubmit {
    int seconds() default 3;   //时间秒
    int maxCount() default 1;  //最大次数
}
