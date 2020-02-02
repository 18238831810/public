package com.cf.crs.entity;

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
public class CityUser implements Serializable {

    private Integer id;
    private String user;
    private Integer organization;
    private String username;
    private String fullname;
    private Integer isDisabled;
    private Integer isLocked;
    private Integer isSystem;
    private Integer isPublic;
    private Integer isMaster;
    private Long createAt;
    private Long updateAt;
    private String auth;
    private String synId;



}
