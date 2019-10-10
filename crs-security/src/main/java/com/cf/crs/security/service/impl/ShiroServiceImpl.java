/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.service.impl;

import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.security.mapper.SysUserTokenDao;
import com.cf.crs.security.service.ShiroService;
import com.cf.crs.security.user.UserDetail;
import com.cf.crs.sys.mapper.SysMenuMapper;
import com.cf.crs.sys.mapper.SysRoleDataScopeDao;
import com.cf.crs.sys.mapper.SysUserDao;
import com.cf.crs.sys.dto.SysUserDTO;
import com.cf.crs.sys.entity.SysUserEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private SysUserTokenDao sysUserTokenDao;
    @Autowired
    private SysRoleDataScopeDao sysRoleDataScopeDao;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public Set<String> getUserPermissions(UserDetail user) {
        //系统管理员，拥有最高权限
        List<String> permissionsList;
//        if(null != user.getSuperAdmin() && user.getSuperAdmin() == SuperAdminEnum.YES.value()) {
        if("admin".equals(user.getUsername())) {
            permissionsList = sysMenuMapper.getPermissionsList();
        }else{
            permissionsList = sysMenuMapper.getUserPermissionsList(user.getId());
        }

        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String permissions : permissionsList){
            if(StringUtils.isBlank(permissions)){
                continue;
            }
            permsSet.addAll(Arrays.asList(permissions.trim().split(",")));
        }

        return permsSet;
    }

    @Override
    public SysUserDTO getByToken(String token) {
        Object value = redisUtils.get(token);
        if (value == null) return null;
        return (SysUserDTO)value;
    }

    @Override
    public SysUserEntity getUser(Long userId) {
        return sysUserDao.selectById(userId);
    }

    @Override
    public SysUserEntity getUser(String username) {
        return sysUserDao.getByUsername(username);
    }

    @Override
    public List<Long> getDataScopeList(Long userId) {
        return sysRoleDataScopeDao.getDataScopeList(userId);
    }
}