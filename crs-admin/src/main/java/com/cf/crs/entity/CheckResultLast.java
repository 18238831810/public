package com.cf.crs.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@TableName("check_result_last")
@Slf4j
@Data
@ApiModel(value = "最新考评结果")
public class CheckResultLast implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "考评对象id")
    private Long checkId;

    @ApiModelProperty(value = "业务健康度(所有考评结果（0:不达标，1:达标）)")
    private Integer health;

    @ApiModelProperty(value = "信息安全")
    private Integer safe;

    @ApiModelProperty(value = "物联网设备")
    private Integer iot;

    @ApiModelProperty(value = "服务器设备")
    private Integer serverDevice;

    @ApiModelProperty(value = "数据库")
    private Integer sqlDevice;

    @ApiModelProperty(value = "中间件")
    private Integer middleware;

    @ApiModelProperty(value = "网络设备")
    private Integer Internet;

    @ApiModelProperty(value = "考评结果")
    private Integer result;

    @ApiModelProperty(value = "考评类型（1:日 2:周 3:月 4:年）")
    private Integer type;

    @ApiModelProperty(value = "考评时间")
    private Long time;

    @TableField(exist=false)
    @ApiModelProperty(value = "考评对象名称")
    private String name;


    @ApiModelProperty(value = "服务器当前值")
    private String serverVaule;

    @ApiModelProperty(value = "服务器达标条件")
    private String serverCondition;

    @ApiModelProperty(value = "数据库当前值")
    private String sqlVaule;

    @ApiModelProperty(value = "数据库达标条件")
    private String sqlCondition;

    @ApiModelProperty(value = "中间件当前值")
    private String middlewareVaule;

    @ApiModelProperty(value = "中间件达标条件")
    private String middlewareCondition;

    @ApiModelProperty(value = "业务可用性当前值")
    private String businessVaule;

    @ApiModelProperty(value = "业务可用性达标条件")
    private String businessCondition;

    @ApiModelProperty(value = "业务可用性达标状态")
    private Integer businessStatus;

    @ApiModelProperty(value = "业务监测当前值")
    private String responseVaule;

    @ApiModelProperty(value = "业务监测达标条件")
    private String responseCondition;

    @ApiModelProperty(value = "业务监测达标状态")
    private Integer responseStatus;

    @ApiModelProperty(value = "数据质量当前值")
    private String dataQualityVaule;

    @ApiModelProperty(value = "数据质量达标条件")
    private String dataQualityCondition;

    @ApiModelProperty(value = "数据质量达标状态")
    private Integer dataQualityStatus;

    @ApiModelProperty(value = "数据共享当前值")
    private String dataSharingVaule;

    @ApiModelProperty(value = "数据共享达标条件")
    private String dataSharingCondition;

    @ApiModelProperty(value = "数据共享达标状态")
    private Integer dataSharingStatus;

    @ApiModelProperty(value = "安全漏洞当前值")
    private String securityBreachVaule;

    @ApiModelProperty(value = "安全漏洞达标条件")
    private String securityBreachCondition;

    @ApiModelProperty(value = "安全漏洞达标状态")
    private Integer securityBreachStatus;

    @ApiModelProperty(value = "病毒攻击当前值")
    private String virusAttackVaule;

    @ApiModelProperty(value = "病毒攻击达标条件")
    private String virusAttackCondition;

    @ApiModelProperty(value = "病毒攻击达标状态")
    private Integer virusAttackStatus;

    @ApiModelProperty(value = "端口扫描当前值")
    private String portScanVaule;

    @ApiModelProperty(value = "端口扫描达标条件")
    private String portScanCondition;

    @ApiModelProperty(value = "端口扫描达标状态")
    private Integer portScanStatus;

    @ApiModelProperty(value = "强力攻击当前值")
    private String forceAttackVaule;

    @ApiModelProperty(value = "强力攻击达标条件")
    private String forceAttackCondition;

    @ApiModelProperty(value = "强力攻击达标状态")
    private Integer forceAttackStatus;

    @ApiModelProperty(value = "木马攻击当前值")
    private String trojanAttackVaule;

    @ApiModelProperty(value = "木马攻击达标条件")
    private String trojanAttackCondition;

    @ApiModelProperty(value = "木马攻击达标状态")
    private Integer trojanAttackStatus;

    @ApiModelProperty(value = "拒绝访问当前值")
    private String deniedAttackVaule;

    @ApiModelProperty(value = "拒绝访问达标条件")
    private String deniedAttacCondition;

    @ApiModelProperty(value = "拒绝访问达标状态")
    private Integer deniedAttacStatus;

    @ApiModelProperty(value = "缓冲区溢出当前值")
    private String zoneAttackVaule;

    @ApiModelProperty(value = "缓冲区溢出达标条件")
    private String zoneAttacCondition;

    @ApiModelProperty(value = "缓冲区溢出达标状态")
    private Integer zoneAttacStatus;

    @ApiModelProperty(value = "蠕虫攻击当前值")
    private String wormAttackVaule;

    @ApiModelProperty(value = "蠕虫攻击达标条件")
    private String wormAttacCondition;

    @ApiModelProperty(value = "蠕虫攻击达标状态")
    private Integer wormAttacStatus;

    @ApiModelProperty(value = "ip碎片当前值")
    private String ipAttackVaule;

    @ApiModelProperty(value = "ip碎片达标条件")
    private String ipAttacCondition;

    @ApiModelProperty(value = "ip碎片达标状态")
    private Integer ipAttacStatus;

    @ApiModelProperty(value = "执法车当前值")
    private String zhifacheVaule;

    @ApiModelProperty(value = "执法车达标条件")
    private String zhifacheCondition;

    @ApiModelProperty(value = "执法车达标状态")
    private Integer zhifacheStatus;

    @ApiModelProperty(value = "绿化车当前值")
    private String lvhuacheVaule;

    @ApiModelProperty(value = "绿化车达标条件")
    private String lvhuacheCondition;

    @ApiModelProperty(value = "绿化车达标状态")
    private Integer lvhuacheStatus;

    @ApiModelProperty(value = "环卫车当前值")
    private String huanweicheVaule;

    @ApiModelProperty(value = "环卫车达标条件")
    private String huanweicheCondition;

    @ApiModelProperty(value = "环卫车达标状态")
    private Integer huanweicheStatus;

    @ApiModelProperty(value = "摄像头当前值")
    private String shexiangtouVaule;

    @ApiModelProperty(value = "摄像头达标条件")
    private String shexiangtouCondition;

    @ApiModelProperty(value = "摄像头达标状态")
    private Integer shexiangtouStatus;

    @ApiModelProperty(value = "执法仪当前值")
    private String zhifayiVaule;

    @ApiModelProperty(value = "执法仪达标条件")
    private String zhifayiCondition;

    @ApiModelProperty(value = "执法仪达标状态")
    private Integer zhifayiStatus;

    @ApiModelProperty(value = "对讲机当前值")
    private String duijiangjiVaule;

    @ApiModelProperty(value = "对讲机达标条件")
    private String duijiangjiCondition;

    @ApiModelProperty(value = "对讲机达标状态")
    private Integer duijiangjiStatus;

    @ApiModelProperty(value = "环卫工牌当前值")
    private String hwgongpaiVaule;

    @ApiModelProperty(value = "环卫工牌达标条件")
    private String hwgongpaiCondition;

    @ApiModelProperty(value = "环卫工牌达标状态")
    private Integer hwgongpaiStatus;

    @ApiModelProperty(value = "公厕一体机当前值")
    private String gcyitijiVaule;

    @ApiModelProperty(value = "公厕一体机达标条件")
    private String gcyitijiCondition;

    @ApiModelProperty(value = "公厕一体机达标状态")
    private Integer gcyitijiStatus;

    @ApiModelProperty(value = "果壳箱监测仪当前值")
    private String gkxjianceyiVaule;

    @ApiModelProperty(value = "果壳箱监测仪达标条件")
    private String gkxjianceyiCondition;

    @ApiModelProperty(value = "果壳箱监测仪达标状态")
    private Integer gkxjianceyiStatus;

    @ApiModelProperty(value = "气体监测仪当前值")
    private String qtjianceyiVaule;

    @ApiModelProperty(value = "气体监测仪达标条件")
    private String qtjianceyiCondition;

    @ApiModelProperty(value = "气体监测仪达标状态")
    private Integer qtjianceyiStatus;

    @ApiModelProperty(value = "避险设备当前值")
    private String bixianshebeiVaule;

    @ApiModelProperty(value = "避险设备达标条件")
    private String bixianshebeiCondition;

    @ApiModelProperty(value = "避险设备达标状态")
    private Integer bixianshebeiStatus;
}
