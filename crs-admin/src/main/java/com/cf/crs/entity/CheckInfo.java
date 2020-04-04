package com.cf.crs.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 考评模型
 * @author frank
 * 2019/10/16
 **/
@Data
@ApiModel(value = "考评对象信息")
@TableName("check_info")
public class CheckInfo implements Serializable {


    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "所属考评对象id（0:考评对象,非0:设备）")
    private Integer parentId;

    @ApiModelProperty(value = "名称")
    private String  name;

    @ApiModelProperty(value = "显示名称")
    private String  displayName;

    @ApiModelProperty(value = "设备类型（0:考评对象，1：服务器，2：数据库 3：中间件）")
    private Integer type;

    @ApiModelProperty(value = "信息安全")
    private String informationSecurity;

    @ApiModelProperty(value = "考评结果接收邮件地址")
    private String email;

    @ApiModelProperty(value = "0:非自动审批 1：自动审批")
    private Integer automatic;

    @ApiModelProperty(value = "考评项目（需要考评选项的id,多个id字符串隔开)")
    private String checkItems;

    @TableField(exist = false)
    @ApiModelProperty(value = "考评设备")
    private Map<String, List<CheckInfo>> deviceList;

    @ApiModelProperty(value = "考评计划（cron表达式）")
    private String checkPlan;

    @ApiModelProperty(value = "考评计划开始时间")
    private Long checkStartTime;

    @ApiModelProperty(value = "考评计划结束时间")
    private Long checkEndTime;

    @ApiModelProperty(value = "上次考评时间")
    private Long lastCheckTime;

    @ApiModelProperty(value = "上次考评结果")
    private Integer lastCheckResult;





}
