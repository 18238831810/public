package com.cf.sql.service;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.cf.sql.mapper.SqlDefinedMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Set;

/**
 * sql自定义查询
 * @author frank
 * 2019/8/7
 **/
@Service
public class SqlDefinedService {

    @Autowired
    private SqlDefinedMapper sqlDefinedMapper;

    @Autowired
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @DS("#header.dataBase")
    public Object definedSelect(String sql){
        return sqlDefinedMapper.definedSelect(sql);
    }

    public ResultJson<Set<String>> getDataSourceName(){
        Map<String, DataSource> currentDataSources = dynamicRoutingDataSource.getCurrentDataSources();
        if (!DataUtil.mapNotEmpty(currentDataSources)) return HttpWebResult.getMonoError("未配置动态数据");
        return HttpWebResult.getMonoSucResult(currentDataSources.keySet());
    }
}
