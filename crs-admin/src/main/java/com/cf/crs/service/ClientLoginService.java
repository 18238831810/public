package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.config.config.ClientConfig;
import com.cf.util.http.HttpWebResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /**
     * 验证登录用户
     * @param code
     * @return
     */
    public JSONObject getUser(String code){
        JSONObject result = getTokenByCode(code);
        String access_token = result.getString("access_token");
        if (StringUtils.isEmpty(access_token)) {
            //获取token失败
            log.info("获取iam登录token失败");
            return result;
        }
        return getUserByToken(access_token);
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
