package com.cf.crs.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.config.config.IotConfig;
import com.cf.crs.mapper.CheckIotMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataChange;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
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


    public static void main(String[] args) {
        DateTime dateTime = DateUtil.offsetDay(new Date(), -5);
        String time = dateTime.toString();
        System.out.println(time);
    }

    public ResultJson<List<JSONObject>> getIotInfo(){
        List<JSONObject> list = Lists.newArrayList();
        String time = DateUtil.offsetDay(new Date(), day).toString();
        Map<String, String> device = iotConfig.getDevice();
        device.keySet().forEach(key->{
            int count = checkIotMapper.selectCount(key);
            //获取在线设备
            int normalCount = checkIotMapper.selectNormalCount(key, time);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("normal",normalCount);
            jsonObject.put("count",count);
            jsonObject.put("type",device.get(key));
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
        if (jsonObject.isEmpty()){
            jsonObject = restTemplate.getForObject(sensorUrl, JSONObject.class);
        }
        if (jsonObject.isEmpty() || jsonObject.getInteger("code") != 200) return 0.0;
        if (id == 9) return DataChange.obToDouble(jsonObject.getString("sensorOnlineRate").replace("%",""));
        else return DataChange.obToDouble(jsonObject.getString("roadDeviceOnlineRate").replace("%",""));
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
        return b.divide(a).doubleValue();
    }


}
