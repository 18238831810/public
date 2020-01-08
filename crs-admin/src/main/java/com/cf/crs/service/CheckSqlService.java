package com.cf.crs.service;

import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


/**
 * @author frank
 * 2019/11/17
 **/
@Slf4j
@Service
public class CheckSqlService {

    @Autowired
    RestTemplate restTemplate;

    @Value("${check.sql.url}")
    private String url;

    @Value("${check.sql.apikey}")
    private String apikey;

    @Value("${check.sql.sqlType}")
    private String sqlType;

    @Value("${check.sql.middlewareType}")
    private String middlewareType;

    @Autowired
    CheckServerService checkServerService;

    /**
     * 获取设备列表
     * @param type 1:数据库 2:中间件
     * @return
     */
    public ResultJson<List<JSONObject>> getCheckList(Integer type){
        if (type == null) return HttpWebResult.getMonoError("请选择查询设备类型");
        if (type == 3) return checkServerService.serverList();
        List<JSONObject> list = Lists.newArrayList();
        String html = getCheckSqlList(type);
        if (StringUtils.isEmpty(html)) return HttpWebResult.getMonoError("接口返回为null");
        log.info("getCheckSqlResult:{}",html);
        Document doc = Jsoup.parse(html);
        Elements result = doc.select("response");
        if (result == null) return HttpWebResult.getMonoError("response为null");
        if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code"))) HttpWebResult.getMonoError("请求失败");
        //请求数据成功
        Elements rowList = result.select("Monitor");
        if (rowList == null || rowList.isEmpty()) return HttpWebResult.getMonoSucResult(list);
        rowList.forEach(row->{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name",row.attr("RESOURCEID"));
            jsonObject.put("displayName",row.attr("DISPLAYNAME"));
            list.add(jsonObject);
        });
        return HttpWebResult.getMonoSucResult(list);
    }

    /**
     * 获取设备列表
     * @param type 设备类型
     * @return
     */
    public String getCheckSqlList(String type){
        String listUrl = url+"/AppManager/xml/ListMonitor?apikey="+apikey+"&type="+type;
        log.info("getCheckSqlList:{}",listUrl);
        return restTemplate.getForObject(listUrl, String.class);
    }

    /**
     * 获取设备列表
     * @param type 设备类型
     * @return
     */
    public String getCheckSqlList(Integer type){
        return getCheckSqlList(type==1?sqlType:middlewareType);
    }

    /**
     * 获取设备列表
     * @param resourceid 设备id
     * @return
     */
    public String getMonitorData(String resourceid){
        String listUrl = url+"/AppManager/xml/GetMonitorData?apikey="+apikey+"&resourceid="+resourceid;
        log.info("getCheckSqlList:{}",listUrl);
        return restTemplate.getForObject(listUrl, String.class);
    }


}
