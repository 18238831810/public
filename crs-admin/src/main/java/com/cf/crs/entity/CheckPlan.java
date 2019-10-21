package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 考评计划管理
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "考评计划管理")
public class CheckPlan implements Serializable {

    @ApiModelProperty(value = "1:日报表，2:周报表，3:月报表 ,4:年报表")
    private Long type;

    @ApiModelProperty(value = "cron表达式(日考评:秒 分 时 ? * 1-7 *  周考评:秒 分 时 ? * 周日 *  月考评:秒 时 分 日 * ? *)")
    private String cronExpression;

    @ApiModelProperty(value = "发送邮箱")
    private String email;

}
