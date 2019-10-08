/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.sys.controller;

import com.cf.crs.common.constant.Constant;
import com.cf.crs.common.page.PageData;
import com.cf.crs.common.utils.Result;
import com.cf.crs.common.validator.AssertUtils;
import com.cf.crs.common.validator.ValidatorUtils;
import com.cf.crs.common.validator.group.DefaultGroup;
import com.cf.crs.common.validator.group.UpdateGroup;
import com.cf.crs.common.annotation.LogOperation;
import com.cf.crs.sys.dto.SysDictDTO;
import com.cf.crs.sys.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@RestController
@RequestMapping("sys/dict")
@Api(tags="数据字典")
public class SysDictController {
    @Autowired
    private SysDictService sysDictService;

    @GetMapping("page")
    @ApiOperation("字典分类")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = "dictType", value = "字典类型", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "dictName", value = "字典名称", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:dict:page")
    public Result<PageData<SysDictDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        //字典分类
        PageData<SysDictDTO> page = sysDictService.page(params);

        return new Result<PageData<SysDictDTO>>().ok(page);
    }

    @GetMapping("list")
    @ApiOperation("字典分类数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dictName", value = "字典名称", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "dictValue", value = "字典值", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:dict:page")
    public Result<List<SysDictDTO>> list(@ApiIgnore @RequestParam Map<String, Object> params){
        //字典分类数据
        List<SysDictDTO> list = sysDictService.list(params);

        return new Result<List<SysDictDTO>>().ok(list);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:dict:info")
    public Result<SysDictDTO> get(@PathVariable("id") Long id){
        SysDictDTO data = sysDictService.get(id);

        return new Result<SysDictDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:dict:save")
    public Result save(@RequestBody SysDictDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, DefaultGroup.class);

        sysDictService.save(dto);

        return new Result();
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:dict:update")
    public Result update(@RequestBody SysDictDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        sysDictService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:dict:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        sysDictService.delete(ids);

        return new Result();
    }

}