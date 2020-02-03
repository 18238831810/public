package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cf.crs.entity.CheckObject;
import com.cf.crs.mapper.CheckObjectMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 考评对象
 * @author frank
 * 2019/10/22
 **/
@Slf4j
@Service
public class CheckObjectService {

    @Autowired
    CheckObjectMapper checkObjectMapper;

    /**
     * 获取考评对象
     * @return
     */
    public ResultJson<JSONArray> getCheckObject(){
        CheckObject checkObject = getObject();
        return HttpWebResult.getMonoSucResult(checkObject != null?JSON.parseArray(checkObject.getObject()): Lists.newArrayList());
    }
    public ResultJson<JSONArray> getCheckSafe(){
        CheckObject checkObject = getObject();
        return HttpWebResult.getMonoSucResult(checkObject != null?JSON.parseArray(checkObject.getSafe()): Lists.newArrayList());
    }

    public CheckObject getObject() {
        return checkObjectMapper.selectById(1);
    }


    /**
     * 设置考评对象
     * @param list
     * @return
     */
    public ResultJson<String> updateCheckObject(String list){
        try {
            if (StringUtils.isEmpty(list)) return HttpWebResult.getMonoError("考评对象不能为空");
            //json校验
            JSON.parseArray(list);
            checkObjectMapper.update(null,new UpdateWrapper<CheckObject>().eq("id",1).set("object",list));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }
    /**
     * 设置安全信息
     * @param list
     * @return
     */
    public ResultJson<String> updateCheckSafe(String list){
        try {
            if (StringUtils.isEmpty(list)) return HttpWebResult.getMonoError("考评安全信息不能为空");
            //json校验
            JSON.parseArray(list);
            checkObjectMapper.update(null,new UpdateWrapper<CheckObject>().eq("id",1).set("safe",list));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }
}
