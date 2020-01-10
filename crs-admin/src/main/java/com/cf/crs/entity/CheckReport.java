package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author frank
 * 2019/10/22
 **/
@Data
@TableName("city_check_report")
public class CheckReport {

   /* `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
            `time` varchar(20) DEFAULT '0' COMMENT '考评时间',
            `type` int(20) NOT NULL DEFAULT '1' COMMENT '1:日 2:周 3:月 4:年',
            `reportRecord` text COMMENT '报表记录',
            `score` int(11) DEFAULT NULL COMMENT '评分',
            `displayName` varchar(255) DEFAULT NULL COMMENT '考评对象',*/

   private Integer id;

   private String day;

    private String week;

    private String month;

    private String year;

   private Integer type;

   private String reportRecord;

   private Integer score;

   private String displayName;
}
