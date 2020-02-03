package com.cf.crs.entity;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author frank
 * 2019/10/22
 **/
@Data
@ApiModel(value = "考评对象")
public class CheckObject {

    private Integer id;

    private String object;

    private String safe;
}
