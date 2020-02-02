package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.PipedReader;
import java.io.Serializable;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "菜单")
@TableName("city_menu")
public class CityMenu implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "父菜单id")
    private String parentId;

    @ApiModelProperty(value = "icon")
    private String icon;

    @ApiModelProperty(value = "菜单类型 0:菜单,1:按钮")
    private Integer type;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "1:属于普通用户菜单 0;不属于普通用户菜单")
    private Integer common;

}
