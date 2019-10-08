/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.crs.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.cf.crs.security.oauth2;

import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.security.user.SecurityUser;
import com.cf.crs.security.user.UserDetail;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 认证
 *
 * @author Mark sunlightcs@gmail.com
 */
@Component
public class Oauth2Realm extends AuthorizingRealm {

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof Oauth2Token;
    }


    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        UserDetail user = SecurityUser.getUser();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(user.getPermsSet());
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token){
        final Oauth2Token accessToken = (Oauth2Token) token;
        UserDetail userDetail = (UserDetail)redisTemplate.boundValueOps(accessToken.getPrincipal()).get();
        if (userDetail == null) throw new RenException(ErrorCode.ACCOUNT_PASSWORD_ERROR);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDetail, accessToken.getPrincipal(), getName());
        return info;
    }


}