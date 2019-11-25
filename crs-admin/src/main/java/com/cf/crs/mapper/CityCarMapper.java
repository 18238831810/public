package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.entity.CityCar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface CityCarMapper extends BaseDao<CityCar> {

    int lhBatchInsert(@Param("list") List<CityCar> list);

    int hwBatchInsert(@Param("list") List<CityCar> list);

    int zfBatchInsert(@Param("list") List<CityCar> list);

}
