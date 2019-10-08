/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.user;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 登录用户信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
public class UserDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String realName;
    private String headUrl;
    private Integer gender;
    private String email;
    private String mobile;
    private Long deptId;
    private String password;
    private Integer status;
    private Integer superAdmin;
    /**
     * 部门数据权限
     */
    private List<Long> deptIdList;

    /**
     * 权限列表
     */
    private Set<String> permsSet;

}