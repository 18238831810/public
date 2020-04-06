package com.cf.crs.controller;

import com.cf.crs.entity.CheckMenu;
import com.cf.crs.service.CheckMenuService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 考评菜单
 * @author frank
 * 2019/10/18
 **/
@Api(tags="城管项目")
@RequestMapping("/city/checkMenu")
@RestController
public class CheckMenuController {

    @Autowired
    CheckMenuService checkMenuService;


    @ApiOperation("获取考评菜单")
    @PostMapping("/getCheckMenu")
    public ResultJson<List<CheckMenu>> getCheckMenu(){
        return checkMenuService.getCheckMenu();
    }


}
