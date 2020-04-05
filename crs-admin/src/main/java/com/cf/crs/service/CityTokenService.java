package com.cf.crs.service;

import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.entity.CityRole;
import com.cf.crs.entity.CityUser;
import com.cf.crs.entity.SysUser;
import com.cf.util.utils.DataUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author frank
 * 2019/12/1
 **/
@Slf4j
@Service
public class CityTokenService {


    @Autowired
    ClientLoginService clientLoginService;

    @Autowired
    HttpServletRequest request;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CityRoleService cityRoleService;

    /**
     * 获取token
     */
    public String getToken(){
        return request.getHeader("token");
    }

    /**
     * 获取用户name
     * @return
     */
    public String getUserName(){
        String token = getToken();
        if (StringUtils.isEmpty(token)) return null;
        Object o = redisUtils.get(token);
        if (o == null) return null;
        if (o instanceof SysUser){
            //本系统用户登录
            SysUser user = (SysUser)o;
            return user.getUsername();
        }else {
            //第三方登录
            CityUser user = (CityUser)o;
            return user.getUsername();
        }
    }


    /**
     * 获取用户
     * @return
     */
    public Map<String, Set> getMenuList(){
        String token = getToken();
        if (StringUtils.isEmpty(token)) return null;
        Map<String, Set> result = (Map<String, Set>)redisUtils.get(token+":menu");
        if (DataUtil.mapNotEmpty(result)) return result;
        return Maps.newHashMap();
    }

}
