package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CityMenu;
import com.cf.crs.entity.CityRole;
import com.cf.crs.mapper.CityMenuMapper;
import com.cf.crs.mapper.CityRoleMapper;
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
public class CityRoleService {

    @Autowired
    CityRoleMapper cityRoleMapper;


    public ResultJson<List<CityRole>> getRoleList(){
        return HttpWebResult.getMonoSucResult(cityRoleMapper.selectList(new QueryWrapper<CityRole>()));
    }

    /**
     * 增加角色
     * @return
     */
    public ResultJson<String> addRole(CityRole cityRole){
        return HttpWebResult.getMonoSucResult(cityRoleMapper.insert(cityRole));
    }

    /**
     * 修改角色
     * @return
     */
    public ResultJson<String> updateRole(CityRole cityRole){
        return HttpWebResult.getMonoSucResult(cityRoleMapper.updateById(cityRole));
    }
    /**
     * 删除角色
     * @return
     */
    public ResultJson<String> deleteRole(Integer id){
        return HttpWebResult.getMonoSucResult(cityRoleMapper.deleteById(id));
    }
}
