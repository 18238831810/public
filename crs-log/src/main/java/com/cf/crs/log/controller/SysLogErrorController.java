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
import com.cf.crs.log.dto.SysLogErrorDTO;
import com.cf.crs.log.excel.SysLogErrorExcel;
import com.cf.crs.log.service.SysLogErrorService;
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
 * 异常日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("sys/log/error")
public class SysLogErrorController {
    @Autowired
    private SysLogErrorService sysLogErrorService;

    @GetMapping("page")
    @RequiresPermissions("sys:log:error")
    public Result<PageData<SysLogErrorDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SysLogErrorDTO> page = sysLogErrorService.page(params);

        return new Result<PageData<SysLogErrorDTO>>().ok(page);
    }

    @GetMapping("export")
    @RequiresPermissions("sys:log:error")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysLogErrorDTO> list = sysLogErrorService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SysLogErrorExcel.class);
    }

}