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
import com.cf.crs.log.dto.SysLogOperationDTO;
import com.cf.crs.log.excel.SysLogOperationExcel;
import com.cf.crs.log.service.SysLogOperationService;
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
 * 操作日志
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("sys/log/operation")
public class SysLogOperationController {
    @Autowired
    private SysLogOperationService sysLogOperationService;

    @GetMapping("page")
    @RequiresPermissions("sys:log:operation")
    public Result<PageData<SysLogOperationDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<SysLogOperationDTO> page = sysLogOperationService.page(params);

        return new Result<PageData<SysLogOperationDTO>>().ok(page);
    }

    @GetMapping("export")
    @RequiresPermissions("sys:log:operation")
    public void export(@ApiIgnore @RequestParam Map<String, Object> params, HttpServletResponse response) throws Exception {
        List<SysLogOperationDTO> list = sysLogOperationService.list(params);

        ExcelUtils.exportExcelToTarget(response, null, list, SysLogOperationExcel.class);
    }

}