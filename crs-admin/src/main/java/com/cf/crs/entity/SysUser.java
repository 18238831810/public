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
@TableName("sys_user")
@ApiModel(value = "城管用户")
public class SysUser implements Serializable {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "登录名")
    private String username;

    @ApiModelProperty(value = "用户名称")
    private String user;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "权限id")
    private String auth;
}
