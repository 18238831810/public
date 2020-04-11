package com.cf.crs.service;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.WaringParam;
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
import java.util.stream.Collectors;


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

     @Value("${check.sql.seq}")
    private String seq;

     @Value("${check.sql.monitor}")
    private String monitor;



    @Autowired
    CheckServerService checkServerService;

    @Autowired
    WarningService warningService;

    /**
     * 获取设备列表
     * @param type 1:数据库 2:中间件 3:服务器 4 :物联网设备 5:工单 6:业务监测 7:页面可用性
     * @return
     */
    public ResultJson<List<JSONObject>> getCheckList(Integer type,Integer waringType){
        if (type == null) return HttpWebResult.getMonoError("请选择查询设备类型");
        if (type == 3) return checkServerService.serverList(waringType);
        if (type == 5) return HttpWebResult.getMonoSucResult(warningService.checkOrderList(waringType));
        return sqlList(type, waringType);
    }

    /**
     * 获取对应告警的的数据库和中间件数据
     * @param type
     * @param waringType
     * @return
     */
    private ResultJson<List<JSONObject>> sqlList(Integer type, Integer waringType) {
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
            jsonObject.put("status",row.attr("HEALTHSTATUS"));
            jsonObject.put("type",row.attr("TYPESHORTNAME"));
            list.add(jsonObject);
        });
        if (waringType == null)  return HttpWebResult.getMonoSucResult(list);
        List<JSONObject> resultList = list.stream().filter(jsonObject -> {
            String status = jsonObject.getString("status");
            if (waringType == 1) return "critical".equalsIgnoreCase(status);
            else if (waringType == 2) return "warning".equalsIgnoreCase(status);
            else return "clear".equalsIgnoreCase(status);
        }).collect(Collectors.toList());
        return HttpWebResult.getMonoSucResult(resultList);
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
        String deviceType = "";
        if (type == 1){
            deviceType = sqlType;
        }else if(type == 2){
            deviceType = middlewareType;
        }else if(type == 6){
            deviceType = seq;
        }else if(type == 7){
            deviceType = monitor;
        }
        return getCheckSqlList(deviceType);
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
