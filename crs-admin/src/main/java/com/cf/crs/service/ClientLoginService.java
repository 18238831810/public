package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.config.config.ClientConfig;
import com.cf.crs.entity.CityUser;
import com.cf.crs.entity.SysUser;
import com.cf.crs.mapper.CityUserMapper;
import com.cf.crs.mapper.SysUserMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author frank
 * 2019/12/6
 **/
@Service
@Slf4j
public class ClientLoginService {

    @Autowired
    ClientConfig clientConfig;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CityUserMapper cityUserMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    RedisUtils redisUtils;

    public static void main(String[] args) {
        String md5Password = DigestUtils.md5DigestAsHex("SzcgKp#@4479".getBytes());
        System.out.println(md5Password);
    }

    public ResultJson login(String userName,String password,String code){
        //第三方登录
        if (StringUtils.isNotEmpty(code)) return getUser(code);

        //自己用户登录
        SysUser sysUser = sysUserMapper.selectOne(new QueryWrapper<SysUser>().eq("username", userName));
        if (sysUser == null) return HttpWebResult.getMonoError("用户名错误");
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equalsIgnoreCase(sysUser.getPassword())) return HttpWebResult.getMonoError("密码错误");
        sysUser.setPassword(null);
        return createToken(sysUser);
    }


    /**
     * 验证第三方登录用户
     * @param code
     * @return
     */
    public ResultJson getUser(String code){
        //获取第三方登录信息
        JSONObject result = getTokenByCode(code);
        String access_token = result.getString("access_token");
        if (StringUtils.isEmpty(access_token)) {
            //获取token失败
            log.info("获取iam登录token失败");
            return HttpWebResult.getMonoError(result.getString("msg"));
        }
        JSONObject user = getUserByToken(access_token);
        String loginName = user.getString("loginName");
        if (StringUtils.isEmpty(loginName)) return HttpWebResult.getMonoError(user.getString("msg"));

        //验证第三方用户登录权限
        CityUser cityUser = cityUserMapper.selectOne(new QueryWrapper<CityUser>().eq("username", loginName));
        if (cityUser == null) return HttpWebResult.getMonoError("不存在此用户");
        String auth = cityUser.getAuth();
        if (StringUtils.isEmpty(auth) || "0".equalsIgnoreCase(auth)) return HttpWebResult.getMonoError("此用户没有登录权限");
        return createToken(cityUser);
    }

    private ResultJson createToken(Object sysUser) {
        String token = CacheKey.USER_TOKEN + ":"+System.currentTimeMillis();
        //验证成功，返回token和用户信息
        redisUtils.set(token,sysUser,60*60*2);
        return HttpWebResult.getMonoSucResult(token,sysUser);
    }

    /**
     * 根据token获取
     * @param access_token
     * @return
     */
    private JSONObject getUserByToken(String access_token) {
        String userUrl = clientConfig.getUrl() + "/idp/oauth2/getUserInfo?access_token={access_token}&client_id={client_id}";
        JSONObject userInfo = restTemplate.getForObject(userUrl, JSONObject.class, access_token, clientConfig.getClientId());
        log.info("userInfo:{}",JSON.toJSONString(userInfo));
        return userInfo;
    }

    /**
     * 获取登录token
     * @param code
     * @return
     */
    private JSONObject getTokenByCode(String code){
        String url = clientConfig.getUrl() + "/idp/oauth2/getToken?client_id={client_id}&client_secret={client_secret}&grant_type=authorization_code&code={code}";
        log.info("code:{}",code);
        JSONObject result = restTemplate.postForObject(url, null,JSONObject.class, clientConfig.getClientId(), clientConfig.getClientSecret(), code);
        log.info("code login result:{}",JSON.toJSONString(result));
        return result;
    }
}
