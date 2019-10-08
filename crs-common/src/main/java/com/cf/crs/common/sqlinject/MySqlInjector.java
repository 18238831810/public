package com.cf.crs.common.sqlinject;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author frank
 * 2019/9/23
 **/
@Component
public class MySqlInjector extends DefaultSqlInjector{
    @Override
    public List<AbstractMethod> getMethodList() {
        List<AbstractMethod> methodList = super.getMethodList();
        methodList.add(new MyInsertBatch());
        return methodList;
    }
}
