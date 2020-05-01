package com.cf.crs.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface CheckIotMapper {

    /**
     * 查询车辆总数
     * @param tableName
     * @return
     */
    int selectCount(@Param("tableName") String tableName);

    /**
     * 查询符合条件的车辆总数
     * @param tableName
     * @return
     */
    int selectNormalCount(@Param("tableName") String tableName,@Param("time") String time);

}
