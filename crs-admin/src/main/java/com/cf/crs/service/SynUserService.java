package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
public class SynUserService {


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
        String guid = "";
        if ("TARGET_ACCOUNT".equalsIgnoreCase(objectType)){
            //用户
            guid = savaOrUpdateUser(json);
        }else if("TARGET_ORGANIZATION".equalsIgnoreCase(objectType)){
            //机构
            guid = savaOrUpdateOrganization(json);
        }
        pullFinish(tokenId,json.getString("taskId"),guid);
        return false;
    }

    /**
     * 保存或跟新机构
     * @param json
     * @return
     */
    private String savaOrUpdateOrganization(JSONObject json) {
        String effectOn = json.getString("effectOn");
        CityOrganization cityOrganization = getCityOrganization(json.getJSONObject("data"));
        if ("DELETED".equalsIgnoreCase(effectOn)) {
            //删除数据
            cityOrganizationMapper.delete(new QueryWrapper<CityOrganization>().eq("code",cityOrganization.getCode()));
        }else {
            CityOrganization code = cityOrganizationMapper.selectOne(new QueryWrapper<CityOrganization>().eq("code", cityOrganization.getCode()));
            if (code == null){
                //插入
                cityOrganizationMapper.insert(cityOrganization);
            }else{
                //更新
                cityOrganizationMapper.update(cityOrganization,new UpdateWrapper<CityOrganization>().eq("code",cityOrganization.getCode()).le("updateAt",cityOrganization.getUpdateAt()));
            }
        }
        return String.valueOf(cityOrganization.getCode());
    }

    /**
     * 保存或更细用户
     * @param json
     * @return
     */
    private String savaOrUpdateUser(JSONObject json) {
        String effectOn = json.getString("effectOn");
        CityUser cityUser = getCityUser(json);
        if ("DELETED".equalsIgnoreCase(effectOn)){
            //删除数据
            cityUserMapper.delete(new QueryWrapper<CityUser>().eq("synId",cityUser.getSynId()));
        } else {
            CityUser synId = cityUserMapper.selectOne(new QueryWrapper<CityUser>().eq("synId", cityUser.getSynId()));
            if (synId == null){
                //插入
                cityUserMapper.insert(cityUser);
            }else{
                //更新
                cityUserMapper.update(cityUser,new UpdateWrapper<CityUser>().eq("synId",cityUser.getSynId()).le("updateAt",cityUser.getUpdateAt()));
            }
        }
        return cityUser.getSynId();
    }

    private CityUser getCityUser(JSONObject jsonObject) {
        JSONObject json = jsonObject.getJSONObject("data");
        CityUser cityUser = new CityUser();
        cityUser.setSynId(jsonObject.getString("id"));
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
