/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 基础Dao
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
public interface BaseDao<T> extends BaseMapper<T> {

    Integer myInsertBatch(@Param("list") List<T> list);

}
