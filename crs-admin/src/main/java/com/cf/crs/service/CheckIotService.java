package com.cf.crs.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.cf.crs.mapper.CheckIotMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 考评菜单
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckIotService {


    @Autowired
    CheckIotMapper checkIotMapper;

    @Value("${check.iotDay}")
    Integer day;


    public static void main(String[] args) {
        DateTime dateTime = DateUtil.offsetDay(new Date(), -5);
        String time = dateTime.toString();
        System.out.println(time);
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
        if (count == 0) return 100.00;
        return getRateDouble(count, normalCount);
    }


    private Double getRateDouble(int count, int normalCount) {
        BigDecimal a = new BigDecimal(count);
        BigDecimal b = new BigDecimal(normalCount);
        return a.divide(b).doubleValue();
    }


}
