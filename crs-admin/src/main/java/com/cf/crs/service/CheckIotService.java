package com.cf.crs.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.config.config.IotConfig;
import com.cf.crs.mapper.CheckIotMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.SHA256;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考评菜单
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckIotService {

    private Map<String,String> carMap = Maps.newHashMap();

    @Autowired
    IotConfig iotConfig;

    @Autowired
    CheckIotMapper checkIotMapper;

    @Value("${check.iotDay}")
    Integer day;

    @Value("${sensorUrl:https://smartum.sz.gov.cn/szcity/pullSensorData/getSensorStatData.action?client_id=szcgGetSensor}")
    String sensorUrl;

    @Autowired
    RestTemplate restTemplate;

    @PostConstruct
    public void PostConstruct(){
        carMap.put("iot_zhifache_status","1");
        carMap.put("iot_lvhuache_status","2");
        carMap.put("iot_huanweiche_status","3");
    }

    public Double getnormalRateForCard(JSONArray jsonArray,String iotName){
        if (jsonArray == null || jsonArray.isEmpty()) {
            jsonArray = getCardStatus();
        }
        if (jsonArray == null || jsonArray.isEmpty()) return 0.00;
        String type = carMap.get(iotName);
        for (Object o : jsonArray) {
            Map map = (HashMap) o;
            if(type.equalsIgnoreCase(DataChange.obToString(map.get("bgroup_id")))){
                return getRateDouble(DataChange.obToInt(map.get("totalnum")),DataChange.obToInt(map.get("onlineNum")));
            }
        }
        return 0.00;
    }


    /**
     * 车联在线状态(1:执法，2：绿化，3：环卫)
     * @return
     */
    public JSONArray getCardStatus() {
        String paasid = iotConfig.getCard().getPaasid();
        String token = iotConfig.getCard().getToken();
        Long timestamp = System.currentTimeMillis()/1000;
        String nonce = "123456789abcdefg";
        String signature = SHA256.sha256(timestamp + token + nonce + timestamp).toUpperCase();
        log.info("timestamp:{},signature:{}",timestamp,signature);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-tif-nonce",nonce);
        headers.add("x-tif-signature",signature);
        headers.add("x-tif-paasid",paasid);
        headers.add("x-tif-timestamp",String.valueOf(timestamp));
        JSONArray result = new RestTemplate().postForObject(iotConfig.getCard().getUrl(), new HttpEntity<>(null,headers), JSONArray.class);
        log.info("card result:{}",result.toJSONString());
        return result;
    }



    public ResultJson<List<JSONObject>> getIotInfo(){
        List<JSONObject> list = Lists.newArrayList();
        String time = DateUtil.offsetDay(new Date(), day).toString();
        Map<String, String> device = iotConfig.getDevice();
        device.keySet().forEach(key->{
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject forObject = restTemplate.getForObject(sensorUrl, JSONObject.class);
            if (forObject != null && !forObject.isEmpty() && forObject.getInteger("code") == 200) data = forObject.getJSONObject("data");
            if ("iot_qtjianceyi_status".equalsIgnoreCase(key)){
                jsonObject.put("normal",data.getInteger("sensorOnLineCount"));
                jsonObject.put("count",data.getInteger("sensorCount"));
                jsonObject.put("type",device.get(key));
            }else if("iot_bixianshebei_status".equalsIgnoreCase(key)){
                jsonObject.put("normal",data.getInteger("roadDeviceOnLineCount"));
                jsonObject.put("count",data.getInteger("roadDeviceCount"));
                jsonObject.put("type",device.get(key));
            }else{
                int count = checkIotMapper.selectCount(key);
                //获取在线设备
                int normalCount = checkIotMapper.selectNormalCount(key, time);

                jsonObject.put("normal",normalCount);
                jsonObject.put("count",count);
                jsonObject.put("type",device.get(key));
            }
            list.add(jsonObject);
        });
        return HttpWebResult.getMonoSucResult(list);
    }

    /**
     *
     * @param id 9:气体监测仪 10:避险设备
     * @param jsonObject
     * @return
     */
    public Double getnormalRateByDay(Integer id,JSONObject jsonObject){
        try {
            if (jsonObject.isEmpty()){
                jsonObject = restTemplate.getForObject(sensorUrl, JSONObject.class);
            }
            if (jsonObject.isEmpty() || jsonObject.getInteger("code") != 200) return 0.0;
            JSONObject data = jsonObject.getJSONObject("data");
            if (data == null || data.isEmpty()) return 0.0;
            if (id == 9) return DataChange.obToDouble(data.getString("sensorOnlineRate").replace("%",""));
            else return DataChange.obToDouble(data.getString("roadDeviceOnlineRate").replace("%",""));
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return 0.0;
        }
    }

    /**
     * 获取网络设备在线率
     * @param tableName
     * @return
     */
    public Double getnormalRateByDay(String tableName){
        String time = DateUtil.offsetDay(new Date(), day).toString();
        return getnormalRate(tableName,time);
    }

    public static void main(String[] args) {
        Integer id = 10;
        JSONObject jsonObject = new JSONObject();
        if (jsonObject.isEmpty()){
            jsonObject = new RestTemplate().getForObject("https://smartum.sz.gov.cn/szcity/pullSensorData/getSensorStatData.action?client_id=szcgGetSensor", JSONObject.class);
        }
        JSONObject data = jsonObject.getJSONObject("data");
        if (id == 9) System.out.println(DataChange.obToDouble(data.getString("sensorOnlineRate").replace("%","")));
        else System.out.println(DataChange.obToDouble(data.getString("roadDeviceOnlineRate").replace("%","")));


        String paasid = "znkp";
        String token = "u52sHzNTaF2rZRUWaPONvXXD475iMSYl";
        Long timestamp = System.currentTimeMillis()/1000;
        String nonce = "123456789abcdefg";
        String signature = SHA256.sha256(timestamp + token + nonce + timestamp).toUpperCase();
        log.info("timestamp:{},signature:{}",timestamp,signature);
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-tif-nonce",nonce);
        headers.add("x-tif-signature",signature);
        headers.add("x-tif-paasid",paasid);
        headers.add("x-tif-timestamp",String.valueOf(timestamp));

        String url = "https://smartum.sz.gov.cn/ebus/minemap_lbs/LBSserver/iov/home/getBusiness";
        JSONArray result = new RestTemplate().postForObject(url, new HttpEntity<>(null,headers), JSONArray.class);
        System.out.println(result.toJSONString());
    }


    /**
     * 获取网络设备在线率
     * @param tableName
     * @param time
     * @return
     */
    public Double getnormalRate(String tableName,String time){
        //获取设备总量
        int count = checkIotMapper.selectCount(tableName);
        //获取在线设备
        int normalCount = checkIotMapper.selectNormalCount(tableName, time);
        if (count == 0) return 100.0;
        return getRateDouble(count, normalCount);
    }


    private Double getRateDouble(int count, int normalCount) {
        BigDecimal a = new BigDecimal(count);
        BigDecimal b = new BigDecimal(normalCount);
        return b.divide(a,2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
