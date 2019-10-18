package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 邮箱配置
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "邮箱服务配置")
@TableName("city_email")
public class EmailSenderProperties implements Serializable {

    private Integer id;

    @ApiModelProperty(value = "邮箱服务名称")
    private String host;

    @ApiModelProperty(value = "邮箱服务端口")
    private Integer port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "用户密码")
    private String password;

    @ApiModelProperty(value = "发送者邮箱")
    private String fromEmail;
}
