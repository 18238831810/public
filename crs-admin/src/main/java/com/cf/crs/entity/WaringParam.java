package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author frank
 * 2019/11/23
 **/
@Data
@ApiModel(value = "获取告警信息")
public class WaringParam implements Serializable {

    @ApiModelProperty(value = "严重度级别")
    private String severity;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    private String category;

    @ApiModelProperty(value = "fromTime")
    private String fromTime;
    @ApiModelProperty(value = "toTime")
    private String toTime;

}
