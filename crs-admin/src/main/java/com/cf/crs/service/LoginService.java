package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.utils.AESCrypto;
import com.cf.util.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.map.MultiValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class LoginService {

    public static void main(String[] args) {
        String paasid = "znkpjczxt";
        String token = "Npn7nl2dFQ8669K7uUkG7YAu9tfS4mKa";
        Long timestamp = 1574824710L;
        String nonce = "123456789abcdefg";
        String signature = SHA256.sha256(timestamp + token + nonce + timestamp).toUpperCase();
        System.out.println(signature);
    }

    @Autowired
    RestTemplate restTemplate;

    public void integration(){
        String paasid = "znkpjczxt";
        String token = "Npn7nl2dFQ8669K7uUkG7YAu9tfS4mKa";
        Long timestamp = System.currentTimeMillis()/1000;
        String nonce = "123456789abcdefg";
        String signature = SHA256.sha256(timestamp + token + nonce + timestamp).toUpperCase();
        log.info("timestamp:{},signature:{}",timestamp,signature);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("systemCode","ZNKPGL");
        jsonObject.put("integrationKey","szcg1234");
        jsonObject.put("force",false);
        jsonObject.put("timestamp",1458793365386L);
        String url = "https://szzhcg.com/ebus/iam/integration?method={method}&request={request}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-tif-nonce",nonce);
        headers.add("x-tif-signature",signature);
        headers.add("x-tif-paasid",paasid);
        headers.add("x-tif-timestamp",String.valueOf(timestamp));
        HttpEntity<Map> httpEntity = new HttpEntity<>(null,headers);
        String s = restTemplate.postForObject(url, httpEntity, String.class,"login",jsonObject.toString());
        System.out.println(s);
    }
}
