package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CityUser;
import com.cf.crs.mapper.CityUserMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author frank
 * 2019/12/1
 **/
@Slf4j
@Service
public class CityUserService {

    @Autowired
    CityUserMapper cityUserMapper;

    /**
     * 获取所有的city用户
     * @return
     */
    public ResultJson<List<CityUser>> selectList(){
        List<CityUser> list = cityUserMapper.selectList(new QueryWrapper<CityUser>().eq("isDisabled", 0));
        return HttpWebResult.getMonoSucResult(list);
    }

    public ResultJson<String> setAuth(Integer id,Integer auth){
        if (id == null || auth == null) return HttpWebResult.getMonoError("参数不能为空");
        cityUserMapper.update(null, new UpdateWrapper<CityUser>().set("auth", auth).eq("id", id));
        return HttpWebResult.getMonoSucStr();
    }
}
