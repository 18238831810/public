package com.cf.sql.controller;

import com.cf.sql.service.SqlDefinedService;
import com.cf.util.http.HttpWebResult;
import com.cf.util.utils.Aes;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义sql查询
 * @author frank
 * @date 2019/8/7
 */
@RestController
@RequestMapping("/public/sql")
@Slf4j
public class SqlDefinedController {

    private final String key = "Ee82Pzeg579BwQYG";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SqlDefinedService sqlDefinedService;

    @PostMapping("/encrypt")
    public Object encrypt(String sql,String sign,String key) {
        try {
            if (!hasRight(sign)) return null;
            return HttpWebResult.getMonoSucResult(Aes.encrypt(sql,key));
        } catch (Exception e) {
            return HttpWebResult.getMonoError(e.getMessage());
        }
    }

    @PostMapping("/select")
    public Object pageList(String enSql,String sign) {
        try {
            if (!hasRight(sign)) return HttpWebResult.getMonoError("没有权限");
            if (StringUtils.isNotEmpty(enSql)) enSql = Aes.decrypt(enSql,key);
            else return HttpWebResult.getMonoError("加密sql语句为空");
            if (StringUtils.isEmpty(enSql) || enSql.contains("delete") || enSql.contains("update") || enSql.contains(";")) return HttpWebResult.getMonoError("sql语句不符合要求");
            return HttpWebResult.getMonoSucResult(sqlDefinedService.definedSelect(enSql));
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
    }

    @PostMapping("/getSourceNames")
    public Object getSourceNames() {
        try {
            return sqlDefinedService.getDataSourceName();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
    }


    /**
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param: @param sign
     * @param: @return
     * @return: boolean
     */
    private boolean hasRight(String sign) {
        if (StringUtils.isEmpty(sign)) return false;
        if ("frank.han".equalsIgnoreCase(sign)) return true;
        return false;
    }

}
