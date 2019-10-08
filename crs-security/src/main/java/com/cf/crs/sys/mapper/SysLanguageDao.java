/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.mapper;

import com.cf.crs.sys.entity.SysLanguageEntity;
import com.cf.crs.common.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * 国际化
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysLanguageDao extends BaseDao<SysLanguageEntity> {

    SysLanguageEntity getLanguage(SysLanguageEntity entity);

    void updateLanguage(SysLanguageEntity entity);

}