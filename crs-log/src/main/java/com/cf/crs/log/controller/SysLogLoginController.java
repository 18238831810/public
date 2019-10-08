/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.log.controller;

import com.cf.crs.common.page.PageData;
import com.cf.crs.common.utils.Result;
import com.cf.crs.log.dto.SysLogLoginDTO;
import com.cf.crs.log.excel.SysLogLoginExcel;
import com.cf.crs.log.service.SysLogLoginService;
import com.cf.crs.log.utils.ExcelUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 登录日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("sys/log/login")
public class SysLogLoginController {
    @Autowired
    private SysLogLoginService sysLogLoginService;

    @GetMapping("page")
    @RequiresPermissions("sys:log:login")
    public Result<PageData<SysLogLoginDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SysLogLoginDTO> page = sysLogLoginService.page(params);

        return new Result<PageData<SysLogLoginDTO>>().ok(page);
    }

    @GetMapping("export")
    @RequiresPermissions("sys:log:login")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysLogLoginDTO> list = sysLogLoginService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SysLogLoginExcel.class);
    }

}