package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author frank
 * 绿化车
 */
@Data
@TableName(value = "city_car_lh")
public class CityCar implements Serializable {

    private Integer id;

    //唯一标识
    private String rfid_id;

    //天
    private String day;

    //号码牌
    private String cp_hm;

    //经度
    private Double jd;

    //纬度
    private Double wd;

    //速度
    private String sd;

    //方向角
    private String fx;

    //时间
    private String gps_sj;

    //时间
    private Long gps_sj_long;

    //上一次状态变更时间
    private long last_time;

    //状态
    private Integer gps_zt;

    //匹配状态
    private Integer matchStatus;

    //状态变更列表
    private String statusList;

    //时间统计
    private Long totalTime;
}
