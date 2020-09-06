package com.cf.crs.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
public class IotCard implements Serializable {


    private String paasid;

    private String token;

    private String url;


}
