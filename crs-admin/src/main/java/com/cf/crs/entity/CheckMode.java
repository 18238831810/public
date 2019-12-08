package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "考评模型")
@TableName("check_mode")
public class CheckMode implements Serializable {

    @ApiModelProperty(value = "1:日，2:周，3:月 ,4:年")
    private Long id;

    @ApiModelProperty(value = "状态：0:不启用，1：启用")
    private Integer status;

    @ApiModelProperty(value = "考评规则")
    private String rule;



}
