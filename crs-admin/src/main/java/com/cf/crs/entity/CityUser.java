package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@TableName("city_user")
@ApiModel(value = "第三方登录用户")
public class CityUser implements Serializable {


    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "用户名称")
    private String user;

    @ApiModelProperty(value = "部门名称")
    private Integer organization;

    @ApiModelProperty(value = "登录名")
    private String username;

    @ApiModelProperty(value = "fullname")
    private String fullname;

    @ApiModelProperty(value = "是否可用")
    private Integer isDisabled;

    @ApiModelProperty(value = "isLocked")
    private Integer isLocked;

    @ApiModelProperty(value = "isSystem")
    private Integer isSystem;

    @ApiModelProperty(value = "isPublic")
    private Integer isPublic;

    @ApiModelProperty(value = "isMaster")
    private Integer isMaster;

    @ApiModelProperty(value = "创建日期")
    private Long createAt;

    @ApiModelProperty(value = "更新日期")
    private Long updateAt;

    @ApiModelProperty(value = "角色权限id")
    private String auth;

    @ApiModelProperty(value = "synId")
    private String synId;

    @TableField(exist = false)
    @ApiModelProperty(value = "用类型(0:第三方用户，只可查询  1:城管系统用户，可增删改查)")
    private Integer type = 0;



}
