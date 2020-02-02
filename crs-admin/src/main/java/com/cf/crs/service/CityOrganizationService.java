package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CityOrganization;
import com.cf.crs.mapper.CityOrganizationMapper;
import com.cf.util.http.HttpWebResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityOrganizationService {

    @Autowired
    CityOrganizationMapper cityOrganizationMapper;

    /**
     * 设置角色
     * @param id
     * @param auth
     * @return
     */
    public Object setRole(Integer id,String auth){
        return HttpWebResult.getMonoSucResult(cityOrganizationMapper.update(null, new UpdateWrapper<CityOrganization>().eq("id", id).set("auth", auth)));
    }
}
