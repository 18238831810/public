package com.cf.crs.controller;

import com.cf.crs.entity.EmailSenderProperties;
import com.cf.crs.service.EmailSenderService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 邮箱服务配置
 * @author frank
 * 2019/10/16
 **/
@Api(tags="城管项目")
@RestController
@RequestMapping("/city/email")
public class EmailSenderController {

    @Autowired
    EmailSenderService emailSenderService;

    @PostMapping("/getEmailProperties")
    @ApiOperation("获取邮箱服务配置")
    public ResultJson<EmailSenderProperties> getEmailProperties(){
        return emailSenderService.getEmailProperties();
    }


    @PostMapping("/saveEmailProperties")
    @ApiOperation("保存邮箱服务配置")
    public ResultJson<EmailSenderProperties> saveEmailProperties(EmailSenderProperties emailSenderProperties){
        return emailSenderService.saveEmailProperties(emailSenderProperties);
    }




}
