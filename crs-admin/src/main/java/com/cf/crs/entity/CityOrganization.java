package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@TableName("city_organization")
public class CityOrganization implements Serializable {

    private Integer id;
    private String code;
    private String parent;
    private String organization;
    private String fullname;
    private String description;
    private Integer sequence;
    private Integer isDisabled;
    private Long createAt;
    private Long updateAt;
    private String auth;




}
