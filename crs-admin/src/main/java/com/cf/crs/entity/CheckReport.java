package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author frank
 * 2019/10/22
 **/
@Data
@ApiModel(value = "考评报表")
@TableName("city_check_report")
public class CheckReport {

    private Integer id;

    @ApiModelProperty(value = "日")
    private String day;

    @ApiModelProperty(value = "周")
    private String week;

    @ApiModelProperty(value = "月")
    private String month;

    @ApiModelProperty(value = "年")
    private String year;

    @ApiModelProperty(value = "考评类型 1:日 2:周 3:月 4:年")
    private Integer type;

    @ApiModelProperty(value = "考评评分详情")
    private String reportRecord;

    @ApiModelProperty(value = "考评总分")
    private Integer score;

    @ApiModelProperty(value = "考评对象标识")
    private String displayName;
}
