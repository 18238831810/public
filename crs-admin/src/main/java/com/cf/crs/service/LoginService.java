package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class LoginService {


    @Autowired
    RestTemplate restTemplate;

    /**
     * 登录
     * @return
     */
    public JSONObject login(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemCode","ZNKPGL");
        jsonObject.put("integrationKey","szcg1234");
        jsonObject.put("force",false);
        jsonObject.put("timestamp",1458793365386L);
        return post(jsonObject, "login");
    }

    /**
     * 拉取数据
     * @return
     */
    public JSONObject pullTask(String tokenId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tokenId",tokenId);
        jsonObject.put("systemCode","ZNKPGL");
        jsonObject.put("timestamp",1458793365386L);
        return post(jsonObject, "pullTask");
    }

    /**
     * 完成拉取状态
     * @return
     */
    public JSONObject pullFinish(String tokenId,String taskId,String guid){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tokenId",tokenId);
        jsonObject.put("taskId",taskId);
        jsonObject.put("guid",guid);
        jsonObject.put("success",true);
        jsonObject.put("systemCode","ZNKPGL");
        jsonObject.put("timestamp",1458793365386L);
        return post(jsonObject, "pullFinish");
    }

    /**
     * 拉取数据
     * @return
     */
    public JSONObject logout(String tokenId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tokenId",tokenId);
        jsonObject.put("systemCode","ZNKPGL");
        jsonObject.put("timestamp",1458793365386L);
        return post(jsonObject, "logout");
    }

    private JSONObject post(JSONObject jsonObject,String method) {
        String paasid = "znkpjczxt";
        String token = "Npn7nl2dFQ8669K7uUkG7YAu9tfS4mKa";
        Long timestamp = System.currentTimeMillis()/1000;
        String nonce = "123456789abcdefg";
        String signature = SHA256.sha256(timestamp + token + nonce + timestamp).toUpperCase();
        log.info("timestamp:{},signature:{}",timestamp,signature);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-tif-nonce",nonce);
        headers.add("x-tif-signature",signature);
        headers.add("x-tif-paasid",paasid);
        headers.add("x-tif-timestamp",String.valueOf(timestamp));
        HttpEntity<Map> httpEntity = new HttpEntity<>(null,headers);
        String url = "https://szzhcg.com/ebus/iam/integration?method={method}&request={request}";
        JSONObject result = restTemplate.postForObject(url, httpEntity, JSONObject.class,method,jsonObject.toString());
        log.info("result:{}",JSON.toJSONString(result));
        return result;

    }
}
