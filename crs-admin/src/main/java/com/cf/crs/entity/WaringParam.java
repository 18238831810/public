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
    private Integer severity;

    @ApiModelProperty(value = "设备名称")
    private Integer deviceName;

    @ApiModelProperty(value = "设备类型")
    private Integer category;

    @ApiModelProperty(value = "fromTime")
    private Integer fromTime;
    @ApiModelProperty(value = "toTime")
    private Integer toTime;

}
