package com.cf.crs.config.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author frank
 * 2019/12/6
 **/
@Data
@Configuration
@EnableConfigurationProperties(ClientConfig.class)
@ConfigurationProperties(prefix = "client")
public class ClientConfig {

    private String url;

    private String clientId;

    private String clientSecret;
}
