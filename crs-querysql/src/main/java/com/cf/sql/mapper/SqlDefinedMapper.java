package com.cf.sql.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 自定义sql查询
 * 2018/8/7
 *@author frank
*/
@Mapper
public interface SqlDefinedMapper{

    List<Map<String,Object>> definedSelect(@Param("sql") String sql);

}