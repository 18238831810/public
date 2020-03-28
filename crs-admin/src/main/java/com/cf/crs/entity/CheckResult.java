package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "考评结果")
@TableName("check_result")
public class CheckResult implements Serializable {


    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "考评对象id")
    private Long checkId;

    @ApiModelProperty(value = "业务健康度(所有考评结果（0:不达标，1:达标）)")
    private Integer health;

    @ApiModelProperty(value = "信息安全")
    private Integer safe;

    @ApiModelProperty(value = "物联网设备")
    private Integer iot;

    @ApiModelProperty(value = "服务器设备")
    private Integer serverDevice;

    @ApiModelProperty(value = "数据库")
    private Integer sqlDevice;

    @ApiModelProperty(value = "中间件")
    private Integer middleware;

    @ApiModelProperty(value = "网络设备")
    private Integer Internet;

    @ApiModelProperty(value = "考评结果")
    private Integer result;

    @ApiModelProperty(value = "考评类型（1:日 2:周 3:月 4:年）")
    private Integer type;

    @ApiModelProperty(value = "考评时间")
    private Long time;

    @TableField(exist=false)
    @ApiModelProperty(value = "考评对象名称")
    private String name;







}
