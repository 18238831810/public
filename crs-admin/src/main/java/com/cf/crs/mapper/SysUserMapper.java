package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.CityUser;
import com.cf.crs.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface SysUserMapper extends BaseDao<SysUser> {
}
