package com.cf.crs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cf.crs.entity.CheckResult;
import com.cf.crs.entity.CheckResultLast;
import com.cf.crs.service.CheckResultService;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 考评结果
 * @author frank
 * 2019/10/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkResult")
@RestController
public class CheckResultController {

    @Autowired
    CheckResultService checkResultService;


    @ApiOperation("获取考评结果")
    @PostMapping("/getCheckResult")
    public ResultJson<List<CheckResultLast>> getCheckInfo(){
        return checkResultService.getCheckResult();
    }


    @ApiOperation("发送考评结果")
    @PostMapping("/sendCheckResult")
    public ResultJson<String> SendEmailForReslut(@ApiParam(value = "考评对象id", required = true) Long id){
        return checkResultService.sendEmailForReslut(id);
    }

    @ApiOperation("获取考评报表")
    @PostMapping("/getcheckReport")
    @ApiImplicitParams({
        @ApiImplicitParam(paramType="query", name = "id", value = "报表id", required = true, dataType = "Integer"),
        @ApiImplicitParam(paramType="query", name = "startTime", value = "开始时间", required = true, dataType = "Long"),
        @ApiImplicitParam(paramType="query", name = "endTime", value = "结束时间", required = true, dataType = "Long"),
        @ApiImplicitParam(paramType="query", name = "start", value = "报表id", required = true, dataType = "Integer"),
        @ApiImplicitParam(paramType="query", name = "length", value = "报表id", required = true, dataType = "Integer")
    })
    public ResultJson<IPage<CheckResult>> getCheckInfo(@ApiIgnore Long id, @ApiIgnore Long startTime, @ApiIgnore Long endTime, @ApiIgnore Page<CheckResult> page){
        return checkResultService.getcheckReport(id,startTime,endTime,page);
    }

    @ApiOperation("手动考评")
    @PostMapping("/startCheck")
    @ApiImplicitParam(paramType="query", name = "id", value = "考评对象id（id为空或0，则全部考评,对应一键全部考评功能）", required = false, dataType = "Integer")
    public ResultJson<List<CheckResult>> startCheck(Long id){
        checkResultService.startCheck(id,1,false);
        return HttpWebResult.getMonoSucStr();
    }

    @ApiOperation("审批考评结果")
    @PostMapping("/updateCheckResult")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "考评结果id", required = true, dataType = "Long"),
            @ApiImplicitParam(paramType="query", name = "filed", value = "考评结果字段名称", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "result", value = "0:不达标，1:达标", required = true, dataType = "Integer"),
    })
    public ResultJson<String> updateCheckResult(@ApiIgnore Long id,@ApiIgnore String filed,@ApiIgnore Integer result){
        return checkResultService.updateCheckResult(id,filed,result);
    }



}
