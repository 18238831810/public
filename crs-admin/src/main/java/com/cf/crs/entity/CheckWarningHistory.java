package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 告警能考评统计记录
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "告警记录")
@TableName("city_warning_history")
public class CheckWarningHistory implements Serializable {

    /*`id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
            `day` varchar(20) DEFAULT '0' COMMENT '天（年月日）',
            `week` varchar(20) DEFAULT '0' COMMENT '周（每周一的年月日）',
            `month` varchar(20) DEFAULT '0' COMMENT '月（年月）',
            `year` varchar(20) DEFAULT '0' COMMENT '年（年）',
            `analyRecord` varchar(100) DEFAULT '' COMMENT '告警率统计记录',
            `waringRecord` varchar(100) DEFAULT '' COMMENT '告警历史记录',*/

    private Integer id;

    @ApiModelProperty(value = "日")
    private String day;

    @ApiModelProperty(value = "周")
    private String week;

    @ApiModelProperty(value = "月")
    private String month;

    @ApiModelProperty(value = "年")
    private String year;

    @ApiModelProperty(value = "考评得分记录")
    private String analyRecord;

    @ApiModelProperty(value = "告警记录")
    private String warningRecord;

    @ApiModelProperty(value = "得分")
    private String score;

    @ApiModelProperty(value = "考评对象标识")
    private String displayName;

}
