package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CheckMenu;
import com.cf.crs.entity.CheckMode;
import com.cf.crs.mapper.CheckMenuMapper;
import com.cf.crs.mapper.CheckModeMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 考评菜单
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckMenuService {

    @Autowired
    CheckMenuMapper checkMenuMapper;

    /**
     * 获取考评任务配置
     * @return
     */
    public ResultJson<List<CheckMenu>> getCheckMenu(){
        List<CheckMenu> checkMenus = checkMenuMapper.selectList(new QueryWrapper<CheckMenu>());
        return HttpWebResult.getMonoSucResult(checkMenus);

    }

}
