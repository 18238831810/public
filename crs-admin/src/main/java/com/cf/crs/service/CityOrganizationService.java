package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CityOrganization;
import com.cf.crs.mapper.CityOrganizationMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityOrganizationService {

    @Autowired
    CityOrganizationMapper cityOrganizationMapper;

    /**
     * 获取所有部门
     * @return
     */
    public ResultJson<List<CityOrganization>> getOrganizationList(){
        return HttpWebResult.getMonoSucResult(cityOrganizationMapper.selectList(new QueryWrapper<CityOrganization>()));
    }

    /**
     * 设置角色
     * @param id
     * @param auth
     * @return
     */
    public ResultJson<String> setRole(Integer id, String auth){
        return HttpWebResult.getMonoSucResult(cityOrganizationMapper.update(null, new UpdateWrapper<CityOrganization>().eq("id", id).set("auth", auth)));
    }
}
