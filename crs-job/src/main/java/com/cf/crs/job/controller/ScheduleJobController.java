/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.job.controller;

import com.cf.crs.common.page.PageData;
import com.cf.crs.common.utils.Result;
import com.cf.crs.common.validator.ValidatorUtils;
import com.cf.crs.common.validator.group.AddGroup;
import com.cf.crs.common.validator.group.DefaultGroup;
import com.cf.crs.common.validator.group.UpdateGroup;
import com.cf.crs.job.dto.ScheduleJobDTO;
import com.cf.crs.job.service.ScheduleJobService;
import com.cf.crs.common.annotation.LogOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/schedule")
public class ScheduleJobController {
	@Autowired
	private ScheduleJobService scheduleJobService;

	@GetMapping("page")
	@RequiresPermissions("sys:schedule:page")
	public Result<PageData<ScheduleJobDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
		PageData<ScheduleJobDTO> page = scheduleJobService.page(params);

		return new Result<PageData<ScheduleJobDTO>>().ok(page);
	}

	@GetMapping("{id}")
	@RequiresPermissions("sys:schedule:info")
	public Result<ScheduleJobDTO> info(@PathVariable("id") Long id){
		ScheduleJobDTO schedule = scheduleJobService.get(id);
		
		return new Result<ScheduleJobDTO>().ok(schedule);
	}

	@PostMapping
	@LogOperation("保存")
	@RequiresPermissions("sys:schedule:save")
	public Result save(@RequestBody ScheduleJobDTO dto){
		ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
		
		scheduleJobService.save(dto);
		
		return new Result();
	}

	@PutMapping
	@LogOperation("修改")
	@RequiresPermissions("sys:schedule:update")
	public Result update(@RequestBody ScheduleJobDTO dto){
		ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
				
		scheduleJobService.update(dto);
		
		return new Result();
	}

	@DeleteMapping
	@LogOperation("删除")
	@RequiresPermissions("sys:schedule:delete")
	public Result delete(@RequestBody Long[] ids){
		scheduleJobService.deleteBatch(ids);
		
		return new Result();
	}

	@PutMapping("/run")
	@LogOperation("立即执行")
	@RequiresPermissions("sys:schedule:run")
	public Result run(@RequestBody Long[] ids){
		scheduleJobService.run(ids);
		
		return new Result();
	}

	@PutMapping("/pause")
	@LogOperation("暂停")
	@RequiresPermissions("sys:schedule:pause")
	public Result pause(@RequestBody Long[] ids){
		scheduleJobService.pause(ids);
		
		return new Result();
	}

	@PutMapping("/resume")
	@LogOperation("恢复")
	@RequiresPermissions("sys:schedule:resume")
	public Result resume(@RequestBody Long[] ids){
		scheduleJobService.resume(ids);
		
		return new Result();
	}

}