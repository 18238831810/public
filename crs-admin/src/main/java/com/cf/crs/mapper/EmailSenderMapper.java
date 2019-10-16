package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.EmailSenderProperties;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface EmailSenderMapper extends BaseDao<EmailSenderProperties> {
}
