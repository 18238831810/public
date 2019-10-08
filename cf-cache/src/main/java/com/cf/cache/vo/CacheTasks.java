package com.cf.cache.vo;

import com.cf.cache.aop.EnableCFCache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * <p>Description: </p>
 * <p>Company: yingchuang</p>
 *
 * @author lantern
 * @date 2019/4/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheTasks {
    private String key;
    private Method method;
    private Object[] args;
    private EnableCFCache enableCFCache;
    private String signClassName;
    //是否没有参数也可以直接运行没有参数
    private boolean nonArgs;
}
