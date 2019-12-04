package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CityOrganization;
import com.cf.crs.entity.CityUser;
import com.cf.crs.mapper.CityOrganizationMapper;
import com.cf.crs.mapper.CityUserMapper;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.DataUtil;
import com.cf.util.utils.DateUtil;
import com.cf.util.utils.SHA256;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    CityUserMapper cityUserMapper;

    @Autowired
    CityOrganizationMapper cityOrganizationMapper;



    /**
     * 同步数据入口
     */
    public void synUserData(){
        String tokenId = getToken();
        if (StringUtils.isEmpty(tokenId)) return;
        while (true){
            if (pullData(tokenId)) break;
        }
        logout(tokenId);
    }

    /**
     * 拉取数据
     * @param tokenId
     * @return
     */
    private boolean pullData(String tokenId) {
        JSONObject json = pullTask(tokenId);
        if (!DataUtil.jsonNotEmpty(json)) return true;
        String success = json.getString("success");
        if (!"true".equalsIgnoreCase(success)) return true;
        //保存数据
        String objectType = json.getString("objectType");
        //回传guid
        Integer guidFlag = null;
        if ("TARGET_ACCOUNT".equalsIgnoreCase(objectType)){
            //用户
            guidFlag = savaOrUpdateUser(json);
        }else if("TARGET_ORGANIZATION".equalsIgnoreCase(objectType)){
            //机构
            guidFlag = savaOrUpdateOrganization(json);
        }
        pullFinish(tokenId,json.getString("taskId"),String.valueOf(guidFlag));
        return false;
    }

    /**
     * 保存或跟新机构
     * @param json
     * @return
     */
    private Integer savaOrUpdateOrganization(JSONObject json) {
        Integer guidFlag;
        Integer guid = json.getInteger("guid");
        CityOrganization cityOrganization = getCityOrganization(json.getJSONObject("data"));
        if (DataUtil.checkIsUsable(guid)) {
            //存在guid，更新数据
            cityOrganizationMapper.update(cityOrganization,new UpdateWrapper<CityOrganization>().eq("code",cityOrganization.getCode()).le("updateAt",cityOrganization.getUpdateAt()));
        }else {
            //插入数据
            cityOrganizationMapper.insert(cityOrganization);
        }
        guidFlag = cityOrganization.getCode();
        return guidFlag;
    }

    /**
     * 保存或更细用户
     * @param json
     * @return
     */
    private Integer savaOrUpdateUser(JSONObject json) {
        Integer guidFlag;
        Integer guid = json.getInteger("guid");
        CityUser cityUser = getCityUser(json.getJSONObject("data"));
        if (DataUtil.checkIsUsable(guid)) {
            //存在guid，更新数据
            cityUserMapper.update(cityUser,new UpdateWrapper<CityUser>().eq("id",guid).le("updateAt",cityUser.getUpdateAt()));
            guidFlag = guid;
        }else {
            //插入数据
            cityUserMapper.insert(cityUser);
            guidFlag = cityUser.getId();
        }
        return guidFlag;
    }

    private CityUser getCityUser(JSONObject json) {
        CityUser cityUser = new CityUser();
        cityUser.setUser(json.getString("_user"));
        cityUser.setOrganization(json.getInteger("_organization"));
        cityUser.setUsername(json.getString("username"));
        cityUser.setFullname(json.getString("fullname"));
        cityUser.setIsDisabled(json.getBoolean("isDisabled")?1:0);
        cityUser.setIsLocked(json.getBoolean("isLocked")?1:0);
        cityUser.setIsSystem(json.getBoolean("isSystem")?1:0);
        cityUser.setIsPublic(json.getBoolean("isPublic")?1:0);
        cityUser.setIsMaster(json.getBoolean("isMaster")?1:0);
        cityUser.setCreateAt(DateUtil.parseDate(json.getString("createAt"),DateUtil.MISTIMESTAMP));
        cityUser.setUpdateAt(DateUtil.parseDate(json.getString("updateAt"),DateUtil.MISTIMESTAMP));
        return cityUser;
    }
    private CityOrganization getCityOrganization(JSONObject json) {
        CityOrganization cityOrganization = new CityOrganization();
        cityOrganization.setCode(json.getInteger("code"));
        cityOrganization.setParent(DataChange.obToInt(json.get("_parent"),0));
        cityOrganization.setOrganization(json.getString("_organization"));
        cityOrganization.setFullname(json.getString("fullname"));
        cityOrganization.setDescription(json.getString("description"));
        cityOrganization.setSequence(json.getInteger("sequence"));
        cityOrganization.setIsDisabled(json.getBoolean("isDisabled")?1:0);
        cityOrganization.setCreateAt(DateUtil.parseDate(json.getString("createAt"),DateUtil.MISTIMESTAMP));
        cityOrganization.setUpdateAt(DateUtil.parseDate(json.getString("updateAt"),DateUtil.MISTIMESTAMP));
        return cityOrganization;
    }

    /**
     * 获取tokenId
     * @return
     */
    public String getToken(){
        JSONObject login = login();
        if (!DataUtil.jsonNotEmpty(login)) {
            log.info("登录失败");
            return null;
        }
        log.info("login:{}",JSON.toJSONString(login));
        String success = login.getString("success");
        if (!"true".equalsIgnoreCase(success))return null;
        return login.getString("tokenId");
    }

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
     * 退出登录
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
