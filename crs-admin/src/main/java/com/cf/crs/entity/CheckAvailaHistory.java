package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cf.crs.mapper.CheckWaringHistoryMapper;
import lombok.Data;

import java.io.Serializable;

/**
 * 性能考评统计记录
 * @author frank
 * 2019/10/16
 **/
@Data
@TableName("city_availa_history")
public class CheckAvailaHistory extends CheckWaringHistory{



}
