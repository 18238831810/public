package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警能考评统计记录
 * @author frank
 * 2019/10/16
 **/
@Data
@TableName("city_waring_history")
public class CheckWaringHistory implements Serializable {

    /*`id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
            `day` varchar(20) DEFAULT '0' COMMENT '天（年月日）',
            `week` varchar(20) DEFAULT '0' COMMENT '周（每周一的年月日）',
            `month` varchar(20) DEFAULT '0' COMMENT '月（年月）',
            `year` varchar(20) DEFAULT '0' COMMENT '年（年）',
            `analyRecord` varchar(100) DEFAULT '' COMMENT '告警率统计记录',
            `waringRecord` varchar(100) DEFAULT '' COMMENT '告警历史记录',*/

    private Integer id;

    private String day;

    private String week;

    private String month;

    private String year;

    private String analyRecord;

    private String waringRecord;

    private String score;

    private String displayName;

}
