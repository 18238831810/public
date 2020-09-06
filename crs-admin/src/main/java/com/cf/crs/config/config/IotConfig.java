package com.cf.crs.config.config;

import com.cf.crs.entity.IotCard;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 网络设备
 * @author frank
 * 2019/12/6
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "iot")
@EnableConfigurationProperties(IotConfig.class)
public class IotConfig {

    /**
     * 网络设备
     */
    private Map<String,String> device;

    /**
     * 车辆在线信息
     */
    private IotCard card;
}
