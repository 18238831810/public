package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.cf.crs.entity.EmailSenderProperties;
import com.cf.crs.mapper.EmailSenderMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮箱服务器
 * @author frank
 * 2019/10/16
 **/
@Service
@Slf4j
public class EmailSenderService {

    @Autowired
    private EmailSenderMapper emailSenderMapper;

    /**
     * 获取邮箱服务配置
     * @return
     */
    public ResultJson<EmailSenderProperties> getEmailProperties(){
        EmailSenderProperties emailSenderProperties = emailSenderMapper.selectById(1);
        return HttpWebResult.getMonoSucResult(emailSenderProperties);
    }

    /**
     * 获取邮箱服务配置
     * @return
     */
    public ResultJson<EmailSenderProperties> saveEmailProperties(EmailSenderProperties emailSenderProperties){
        emailSenderProperties.setId(1);
        emailSenderMapper.updateById(emailSenderProperties);
        return HttpWebResult.getMonoSucResult(emailSenderProperties);
    }


    /**
     * 发送邮件
     * @return
     */
    public ResultJson<String> sendEmail(String title, String content, String to, MultipartFile[] file){
        //获取邮件sender
        EmailSenderProperties emailSenderProperties = emailSenderMapper.selectById(1);
        log.info("发送邮件服务配置:{},{}", title, JSON.toJSONString(emailSenderProperties));
        JavaMailSender sender = getJavaMailSender(emailSenderProperties);
        MimeMessage message=  sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom(emailSenderProperties.getFromEmail());
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content);
            //验证文件数据是否为空
            if (file != null && file.length > 0) helper.addAttachment(title, file[0]);
            sender.send(message);
            log.info("{}- 文本邮件发送成功",title);
            return HttpWebResult.getMonoSucStr();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError("发送邮件失败");
        }
    }

    /**
     * 发送邮件
     * @return
     */
    public ResultJson<String> sendEmail(String title, String content, String to, String filePath){
        //获取邮件sender
        EmailSenderProperties emailSenderProperties = emailSenderMapper.selectById(1);
        log.info("发送邮件服务配置:{},{}", title, JSON.toJSONString(emailSenderProperties));
        JavaMailSender sender = getJavaMailSender(emailSenderProperties);
        MimeMessage message=  sender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom(emailSenderProperties.getFromEmail());
            helper.setTo(to);
            helper.setSubject(title);
            helper.setText(content);
            //验证文件数据是否为空
            helper.addAttachment(title+".pdf", new FileSystemResource(new File(filePath)));
            log.info("发送附件->{}",filePath);
            sender.send(message);
            log.info("{}-文本邮件发送成功",title);
            return HttpWebResult.getMonoSucStr();
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError("发送邮件失败");
        }
    }


    /**
     * 获取邮件sender
     * @param emailSenderProperties
     * @return
     */
    private JavaMailSender getJavaMailSender(EmailSenderProperties emailSenderProperties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(emailSenderProperties.getHost());
        Integer port = emailSenderProperties.getPort();
        if (DataUtil.checkIsUsable(port)) sender.setPort(emailSenderProperties.getPort());
        sender.setUsername(emailSenderProperties.getUsername());
        sender.setPassword(emailSenderProperties.getPassword());
        return sender;
    }

}
