package com.cf.crs.kafka;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.common.redis.RedisUtils;
import com.cf.crs.entity.CityCar;
import com.cf.crs.mapper.CityCarMapper;
import com.cf.util.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Listener {


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    CityCarMapper cityCarMapper;


    /*@KafkaListener(topics = "CLWGPS-ZF",groupId = "zf")
    private void zfBatchListener(List<ConsumerRecord<String, Object>> consumerRecords, Acknowledgment acknowledgment){
        //同步数据
        insert(consumerRecords,list -> cityCarMapper.zfBatchInsert(list));
        acknowledgment.acknowledge();
    }
    @KafkaListener(topics = "CLWGPS-HW",groupId = "hw")
    private void hwBatchListener(List<ConsumerRecord<String, Object>> consumerRecords, Acknowledgment acknowledgment){
        //同步数据
        insert(consumerRecords,list -> cityCarMapper.hwBatchInsert(list));
        acknowledgment.acknowledge();
    }
    @KafkaListener(topics = "CLWGPS-LH",groupId = "lh")
    private void lhBatchListener(List<ConsumerRecord<String, Object>> consumerRecords, Acknowledgment acknowledgment){
        //同步数据
        insert(consumerRecords,list -> cityCarMapper.lhBatchInsert(list));
        acknowledgment.acknowledge();
    }*/

    private void insert(List<ConsumerRecord<String, Object>> consumerRecords,Consumer<List<CityCar>> consumer) {
        try {
            consumerRecords.forEach(record -> {
                Object value = record.value();
                List<Object> jsonArray = (List) JSONArray.fromObject(value);
                List<CityCar> list = jsonArray.stream().filter(json -> {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(json));
                    if (StringUtils.isEmpty(jsonObject.getString("GPS_SJ"))) {
                        log.info("id:{},topic:{}",jsonObject.getString("RFID_ID"),record.topic());
                        return false;
                    }
                    /*if (jsonObject.getInteger("GPS_ZT") == null) {
                        log.info("id:{},topic:{}",jsonObject.getString("RFID_ID"),record.topic());
                        return false;
                    }*/
                    return true;
                }).map(json -> getCityCar(json)).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(list)) return;
               consumer.accept(list);
            });
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    private CityCar getCityCar(Object json) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(json));
        CityCar cityCar = new CityCar();
        cityCar.setRfid_id(jsonObject.getString("RFID_ID"));
        cityCar.setDay(jsonObject.getString("GPS_SJ").substring(0, 10));
        cityCar.setCp_hm(jsonObject.getString("CP_HM"));
        cityCar.setJd(jsonObject.getDouble("JD"));
        cityCar.setWd(jsonObject.getDouble("WD"));
        cityCar.setSd(jsonObject.getString("SD"));
        cityCar.setFx(jsonObject.getString("FX"));
        cityCar.setGps_sj(jsonObject.getString("GPS_SJ"));
        cityCar.setGps_sj_long(DateUtil.parseDate(jsonObject.getString("GPS_SJ"), DateUtil.TIMESTAMP));
        //cityCar.setLast_time(DateUtil.parseDate(jsonObject.getString("GPS_SJ"), DateUtil.TIMESTAMP));
        cityCar.setGps_zt(jsonObject.getInteger("GPS_ZT")== null?0:jsonObject.getInteger("GPS_ZT"));
        //cityCar.setMatchStatus(jsonObject.getInteger("MATCH"));
        //cityCar.setStatusList(jsonObject.getString("GPS_SJ") + "#" + jsonObject.getInteger("GPS_ZT") + ",");
        //cityCar.setTotalTime(0L);
        //if (cityCar.getGps_zt() == 0) cityCar.setTotalTime(cityCar.getGps_sj_long() - DateUtil.getStartTime(cityCar.getGps_sj_long()).getTime());
        return cityCar;
    }

}

