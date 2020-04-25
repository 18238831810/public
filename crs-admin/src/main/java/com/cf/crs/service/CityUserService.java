package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CityUser;
import com.cf.crs.entity.SysUser;
import com.cf.crs.mapper.CityUserMapper;
import com.cf.crs.mapper.SysUserMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author frank
 * 2019/12/1
 **/
@Slf4j
@Service
public class CityUserService {

    @Autowired
    CityUserMapper cityUserMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    /**
     * 获取所有的city用户
     * @return
     */
    public ResultJson<List<CityUser>> selectList(String username,String user){
        List<CityUser> list = cityUserMapper.selectList(new QueryWrapper<CityUser>().eq("isDisabled", 0).
                like(StringUtils.isNotEmpty(username),"username",username).like(StringUtils.isNotEmpty(user),"user",user));
        List<SysUser> sysUsers = sysUserMapper.selectList(new QueryWrapper<SysUser>().
                like(StringUtils.isNotEmpty(username),"username",username).like(StringUtils.isNotEmpty(user),"user",user));
        List<CityUser> collect = sysUsers.stream().map(sysUser -> {
            CityUser cityUser = new CityUser();
            cityUser.setId(sysUser.getId());
            cityUser.setUsername(sysUser.getUsername());
            cityUser.setUser(sysUser.getUser());
            cityUser.setAuth(sysUser.getAuth());
            cityUser.setType(1);
            return cityUser;
        }).collect(Collectors.toList());
        list.addAll(collect);
        return HttpWebResult.getMonoSucResult(list);
    }

    /**
     * 设置用户权限
     * @param id
     * @param auth
     * @return
     */
    public ResultJson<String> setRole(Integer id,String auth){
        if (id == null || auth == null) return HttpWebResult.getMonoError("参数不能为空");
        cityUserMapper.update(null, new UpdateWrapper<CityUser>().set("auth", auth).eq("id", id));
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 更改用户信息
     * @param sysUser
     * @return
     */
    public ResultJson<String> updateUser(SysUser sysUser){
        sysUser.setPassword(DigestUtils.md5DigestAsHex(sysUser.getUsername().getBytes()));
        sysUserMapper.updateById(sysUser);
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 新增用户信息
     * @param sysUser
     * @return
     */
    public ResultJson<String> addUser(SysUser sysUser){
        sysUser.setPassword(DigestUtils.md5DigestAsHex(sysUser.getUsername().getBytes()));
        sysUserMapper.insert(sysUser);
        return HttpWebResult.getMonoSucStr();
    }
    /**
     * 删除用户信息
     * @param id
     * @return
     */
    public ResultJson<String> deleteUser(Long id){
        if (!DataUtil.checkIsUsable(id)) return HttpWebResult.getMonoError("您删除的用户不存在");
        sysUserMapper.deleteById(id);
        return HttpWebResult.getMonoSucStr();
    }


    /**
     * 更改当前用户密码
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public ResultJson<String> updatePassword(Long id,String oldPassword,String newPassword){
        SysUser sysUser = sysUserMapper.selectById(id);
        String password = sysUser.getPassword();
        if (!password.equalsIgnoreCase(DigestUtils.md5DigestAsHex(oldPassword.getBytes()))) return HttpWebResult.getMonoError("请输入正确的初始密码");
        sysUserMapper.update(null,new UpdateWrapper<SysUser>().eq("id",id).set("password",DigestUtils.md5DigestAsHex(newPassword.getBytes())));
        return HttpWebResult.getMonoSucStr();
    }

}
