package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("city_check_plan")
public class CheckPlan implements Serializable {

    private Integer id;

    @ApiModelProperty(value = "cron表达式(日考评:秒 分 时 ? * 1-7  周考评:秒 分 时 ? * 周日  月考评:秒 时 分 日 * ?)")
    private String cronExpression;

    @ApiModelProperty(value = "发送邮箱")
    private String email;

}
