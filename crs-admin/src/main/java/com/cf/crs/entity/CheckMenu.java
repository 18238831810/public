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
@ApiModel(value = "考评菜单")
@TableName("check_menu")
public class CheckMenu implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "父级菜单id（0标示是最顶级菜单）")
    private Integer parentId;

    @ApiModelProperty(value = "菜单名称")
    private String  name;

    @ApiModelProperty(value = "菜单类型（1:数据库 2:中间件 3:服务器 4 :物联网设备 5:工单（暂时不用） 6:业务监测 7:页面可用性 8:物联网设备)")
    private Integer type;

    @ApiModelProperty(value = "菜单等级")
    private Integer level;

    @ApiModelProperty(value = "是否是最底层菜单（0：不是 1：是）")
    private Integer isLast;




}
