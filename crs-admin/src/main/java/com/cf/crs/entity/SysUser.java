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
@TableName("sys_user")
public class SysUser implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String auth;
}
