package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.CheckReport;
import com.cf.crs.mapper.CheckReportMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author frank
 * 2019/10/17
 **/
@Slf4j
@Service
public class CheckReportService {

    @Autowired
    CheckReportMapper checkReportMapper;

    public ResultJson<List<CheckReport>> getReportList(){
        List<CheckReport> list = checkReportMapper.selectList(new QueryWrapper<CheckReport>());
        return HttpWebResult.getMonoSucResult(list);
    }


    public ResultJson<String> addReportList(CheckReport checkReport){
        return HttpWebResult.getMonoSucResult(checkReportMapper.insert(checkReport));
    }


    public ResultJson<String> updateReportList(CheckReport checkReport){
        return HttpWebResult.getMonoSucResult(checkReportMapper.updateById(checkReport));
    }

        public ResultJson<String> deleteReportList(Integer id){
        return HttpWebResult.getMonoSucResult(checkReportMapper.deleteById(id));
    }






}
