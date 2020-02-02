package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CityMenu;
import com.cf.crs.mapper.CityMenuMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author frank
 * 2019/12/1
 **/
@Slf4j
@Service
public class CityMenuService {

    @Autowired
    CityMenuMapper cityMenuMapper;

    public ResultJson<List<CityMenu>> getMenuList(){
        return HttpWebResult.getMonoSucResult(cityMenuMapper.selectList(new QueryWrapper<CityMenu>()));
    }
}
