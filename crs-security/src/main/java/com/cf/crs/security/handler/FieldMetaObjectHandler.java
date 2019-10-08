/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.cf.crs.security.user.SecurityUser;
import com.cf.crs.security.user.UserDetail;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 公共字段，自动填充值
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class FieldMetaObjectHandler implements MetaObjectHandler {
    private final static String CREATE_DATE = "createDate";
    private final static String CREATOR = "creator";
    private final static String UPDATE_DATE = "updateDate";
    private final static String UPDATER = "updater";
    private final static String DEPT_ID = "deptId";

    @Override
    public void insertFill(MetaObject metaObject) {
        UserDetail user = SecurityUser.getUser();
        Date date = new Date();

        //创建者
        setFieldValByName(CREATOR, user.getId(), metaObject);
        //创建时间
        setFieldValByName(CREATE_DATE, date, metaObject);

        //创建者所属部门
        Long deptId = (Long)getFieldValByName(DEPT_ID, metaObject);
        if(deptId != null){
            setFieldValByName(DEPT_ID, user.getDeptId(), metaObject);
        }

        //更新者
        setFieldValByName(UPDATER, user.getId(), metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, date, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新者
        setFieldValByName(UPDATER, SecurityUser.getUserId(), metaObject);
        //更新时间
        setFieldValByName(UPDATE_DATE, new Date(), metaObject);
    }
}