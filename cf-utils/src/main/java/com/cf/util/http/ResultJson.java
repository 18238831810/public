package com.cf.util.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * json返回对象，用于与接口请求方协议的对象
 */
@ApiModel(value = "响应")
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultJson<T> implements Serializable {

    @ApiModelProperty(value = "编码：200表示成功，其他值表示失败")
    private int code;

    @ApiModelProperty(value = "消息内容")
    private String code_desc;

    @ApiModelProperty(value = "响应数据")
    private T data;

    public ResultJson(T ch_msg) {
        this(200, "suc", ch_msg);
    }

}
