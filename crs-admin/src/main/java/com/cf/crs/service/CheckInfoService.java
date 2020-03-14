package com.cf.crs.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckInfo;
import com.cf.crs.entity.CheckMenu;
import com.cf.crs.mapper.CheckInfoMapper;
import com.cf.crs.mapper.CheckMenuMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考评菜单
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckInfoService {

    @Autowired
    CheckInfoMapper checkInfoMapper;

    /**
     * 获取对象信息
     * @return
     */
    public ResultJson<List<CheckInfo>> getCheckInfo(){
        List<CheckInfo> list = checkInfoMapper.selectList(new QueryWrapper<CheckInfo>());
        List<CheckInfo> result = Lists.newArrayList();
        for (CheckInfo checkInfo : list) {
            //先获取考评对象
            if (checkInfo.getParentId() == 0 && checkInfo.getType() == 0){
                Long id = checkInfo.getId();
                //获取考评对象所属设备
                Map<Integer, List<CheckInfo>> map = list.stream().filter(checkDevice -> (checkDevice.getParentId() != 0 || checkDevice.getType() != 0) && (long) checkDevice.getParentId() == id).collect(Collectors.groupingBy(CheckInfo::getType));
                checkInfo.setDeviceList(map);
                result.add(checkInfo);
            }
        }
        return HttpWebResult.getMonoSucResult(result);
    }

    /**
     * 新增考评对象信息
     * @return
     */
    public ResultJson<String> addCheckInfo(CheckInfo checkInfo){
        checkInfo.setParentId(0);
        checkInfo.setType(0);
        return HttpWebResult.getMonoSucResult(checkInfoMapper.insert(checkInfo));
    }

    /**
     * 修改考评对象信息
     * @return
     */
    public ResultJson<String> updateCheckInfo(CheckInfo checkInfo){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.update(null, new UpdateWrapper<CheckInfo>().eq("id", checkInfo.getId()).
                set("name", checkInfo.getName()).set("displayName", checkInfo.getDisplayName()).set("checkItems", checkInfo.getCheckItems()).set("email", checkInfo.getEmail()).set("automatic", checkInfo.getAutomatic())));
    }
    /**
     * 删除考评对象信息或设备
     * @return
     */
    public ResultJson<String> deleteCheckInfo(Integer id){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.delete(new QueryWrapper<CheckInfo>().eq("id",id).or().eq("parentId",id)));
    }

    /**
     * 编辑考评对象信息安全
     * @return
     */
    public ResultJson<String> updateCheckInfoSecurity(CheckInfo checkInfo){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.update(null, new UpdateWrapper<CheckInfo>().eq("id", checkInfo.getId()).
        set("informationSecurity",checkInfo.getInformationSecurity())));
    }

    /**
     * 新增考评对象设备
     * @return
     */
    public ResultJson<String> addCheckDevice(CheckInfo checkInfo){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.insert(checkInfo));
    }

    /**
     * 修改考评对象信息
     * @return
     */
    public ResultJson<String> updateCheckDevice(CheckInfo checkInfo){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.updateById(checkInfo));
    }

    /**
     * 获取考评计划
     * @return
     */
    public ResultJson<List<CheckInfo>> getCheckPlan(){
        return HttpWebResult.getMonoSucResult(checkInfoMapper.selectList(new QueryWrapper<CheckInfo>().eq("type",0).eq("parentId",0)));
    }

    /**
     * 编辑考评计划
     * @return
     */
    public ResultJson<String> updateCheckPlan(CheckInfo checkInfo){
        Long id = checkInfo.getId();
        return HttpWebResult.getMonoSucResult(checkInfoMapper.update(null,new UpdateWrapper<CheckInfo>().eq(id != null && id > 0,"id",checkInfo.getId()).eq("type",0).eq("parentId",0).set("checkPlan",checkInfo.getCheckPlan()).
                set("checkStartTime",checkInfo.getCheckStartTime()).set("checkEndTime",checkInfo.getCheckEndTime())));
    }


}
