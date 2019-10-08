/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName:  GeneratorDao
 * @Description: 数据库接口
 * @author: spark
 * @date:   Aug 16, 2019 5:10:24 PM
 *
 */
public interface GeneratorDao {
    List<Map<String, Object>> queryList(Map<String, Object> map);

    Map<String, String> queryTable(String tableName);

    List<Map<String, String>> queryColumns(String tableName);
}
