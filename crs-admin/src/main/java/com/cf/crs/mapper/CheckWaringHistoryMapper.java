package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.entity.CheckWaringHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface CheckWaringHistoryMapper extends BaseDao<CheckWaringHistory> {
}
