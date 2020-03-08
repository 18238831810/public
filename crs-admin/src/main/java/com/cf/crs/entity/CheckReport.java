package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.security.PrivateKey;

/**
 * @author frank
 * 2019/10/22
 **/
@Data
@ApiModel(value = "考评报表列表")
@TableName("check_report")
public class CheckReport {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "报表名称")
    private String name;

    @ApiModelProperty(value = "报表时间（时间段用/隔开）")
    private String checkTime;

    @ApiModelProperty(value = "考评对象id（多个id用逗号隔开）")
    private String checkObjectIdList;

    @ApiModelProperty(value = "考评项目（需要考评选项的id,多个id字符串隔开)")
    private String checkItems;

    @ApiModelProperty(value = "报表计划（多个cron表达式）")
    private String checkPlan;

    @ApiModelProperty(value = "考评开始时间")
    private Long checkStartTime;

    @ApiModelProperty(value = "考评结束时间")
    private Long checkEndTime;

}
