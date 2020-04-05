package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CityMenu;
import com.cf.crs.entity.CityRole;
import com.cf.crs.mapper.CityMenuMapper;
import com.cf.crs.mapper.CityRoleMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author frank
 * 2019/12/1
 **/
@Slf4j
@Service
public class CityRoleService {

    @Autowired
    CityRoleMapper cityRoleMapper;

    @Autowired
    CityMenuService cityMenuService;

    /**
     * 查询所有有角色
     * @return
     */
    public ResultJson<List<CityRole>> getRoleList(){
        return HttpWebResult.getMonoSucResult(cityRoleMapper.selectList(new QueryWrapper<CityRole>()));
    }

    /**
     * 根据id查询权限(多个id逗号隔开)
     * @return
     */
    public List<CityRole> getRoleList(String ids){
        if ("1".equalsIgnoreCase(ids) || "2".equalsIgnoreCase(ids)) return cityRoleMapper.selectList(new QueryWrapper<CityRole>());
        if(StringUtils.isEmpty(ids)) return Lists.newArrayList();
        List<Integer> collect = Arrays.stream(ids.split(",")).filter(id -> NumberUtils.isNumber(id)).map(id -> Integer.parseInt(id)).collect(Collectors.toList());
        return cityRoleMapper.selectList(new QueryWrapper<CityRole>().in("id",collect));
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
