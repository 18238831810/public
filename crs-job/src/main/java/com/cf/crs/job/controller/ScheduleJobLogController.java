/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.job.controller;

import com.cf.crs.job.service.ScheduleJobLogService;
import com.cf.crs.common.constant.Constant;
import com.cf.crs.common.page.PageData;
import com.cf.crs.common.utils.Result;
import com.cf.crs.job.dto.ScheduleJobLogDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 定时任务日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/scheduleLog")
public class ScheduleJobLogController {
	@Autowired
	private ScheduleJobLogService scheduleJobLogService;

	@GetMapping("page")
	@RequiresPermissions("sys:schedule:log")
	public Result<PageData<ScheduleJobLogDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
		PageData<ScheduleJobLogDTO> page = scheduleJobLogService.page(params);
		
		return new Result<PageData<ScheduleJobLogDTO>>().ok(page);
	}

	@GetMapping("{id}")
	@RequiresPermissions("sys:schedule:log")
	public Result<ScheduleJobLogDTO> info(@PathVariable("id") Long id){
		ScheduleJobLogDTO log = scheduleJobLogService.get(id);
		
		return new Result<ScheduleJobLogDTO>().ok(log);
	}
}