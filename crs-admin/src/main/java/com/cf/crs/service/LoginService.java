package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.utils.AESCrypto;
import com.cf.util.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class LoginService {

    @Autowired
    RestTemplate restTemplate;

    public void integration(){
       /* const paasid = 'iam';
const token = 'XC28M4LP38nppGincusRSh4pq1jnsBxK'; //'yy09Hqe2OjtaApaSRV7S1tzXlgiV4Pv1';
const timestamp = (Date.now() / 1000).toFixed();
const nonce = '123456789abcdefg';
const signature = CryptoJS.SHA256(timestamp + token + nonce + timestamp).toString(CryptoJS.enc.Hex).toUpperCase();

        pm.globals.set("paasid", paasid);
        pm.globals.set("timestamp", timestamp);
        pm.globals.set("signature", signature);
        pm.globals.set("nonce", nonce);*/

        String paasid = "iam";
        String token = "XC28M4LP38nppGincusRSh4pq1jnsBxK";
        Long timestamp = System.currentTimeMillis()/1000;
        String nonce = "123456789abcdefg";
        String signature = AESCrypto.parseByte2HexStr(SHA256.sha(timestamp+token+nonce+timestamp)).toUpperCase();
        String url = "https://szzhcg.com/ebus/iam/integration?method=login&request={%20%22systemCode%22%20:%20%22ZNKPGL%22,%20%22integrationKey%22%20:%20%22szcg1234%22,%20%22force%22%20:%20false,%20%22timestamp%22%20:%201458793365386%20}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        headers.add("x-tif-nonce",nonce);
        headers.add("x-tif-signature",signature);
        headers.add("x-tif-paasid",paasid);
        headers.add("x-tif-timestamp",String.valueOf(timestamp));




        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(null,headers);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, httpEntity, String.class);
        System.out.println(JSON.toJSONString(stringResponseEntity));
    }
}
