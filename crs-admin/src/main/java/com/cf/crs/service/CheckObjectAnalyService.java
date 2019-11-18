package com.cf.crs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.CheckObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author frank
 * 2019/11/18
 **/
@Slf4j
@Service
public class CheckObjectAnalyService {

    @Autowired
    CheckObjectService checkObjectService;

    @Autowired
    CheckServerService checkServerService;

    @Autowired
    CheckSqlService checkSqlService;


    public Map<String,JSONObject> getServersMap(){
        HashMap<String,JSONObject> map = Maps.newHashMap();
        try {
            List<JSONObject> servers = checkServerService.getServerList();
            if (servers == null || servers.isEmpty()) return map;
            log.info("serverResult:{}",JSON.toJSONString(servers));
            for (Object obj:servers) {
                JSONObject server = JSON.parseObject(JSON.toJSONString(obj));
                if (server == null || server.isEmpty()) continue;
                String name = server.getString("name");
                map.put(name,server);
            }
        } catch (Exception e) {
            log.info(e.getMessage(),e);
        }
        return map;
    }

    public Map<String,Element> getSqlMap(Integer type){
        Map<String, Element> map = Maps.newHashMap();
        try {
            String html = checkSqlService.getCheckSqlList(type);
            if (StringUtils.isEmpty(html)) return map;
            log.info("getCheckSqlResult:{}", html);
            Document doc = Jsoup.parse(html);
            Elements result = doc.select("response");
            if (result == null) return map;
            if (!result.hasAttr("response-code") || !"4000".equalsIgnoreCase(result.attr("response-code")))
                HttpWebResult.getMonoError("请求失败");
            //请求数据成功
            Elements rowList = result.select("Monitor");
            for (Element element : rowList) {
                String name = element.attr("RESOURCEID");
                map.put(name, element);
            }
        }catch (Exception e){
            log.info(e.getMessage(),e);
        }
        return map;
    }

    public List<Object> getCheckObjectAnalyResult(){
        Map<String, JSONObject> serversMap = getServersMap();
        Map<String, Element> sqlMap = getSqlMap(1);
        Map<String, Element> middlewareMap = getSqlMap(2);
        CheckObject object = checkObjectService.getObject();
        if (object == null) return null;
        String result = object.getObject();
        JSONArray array = JSON.parseArray(result);
        if (array == null || array.isEmpty()) return null;
        ArrayList<Object> list = Lists.newArrayList();
        for(Object obj:array){
            JSONObject symbolJson = new JSONObject();
            //遍历一级菜单
            JSONObject checkObj = JSON.parseObject(JSON.toJSONString(obj));
            String symbol = checkObj.getString("name");
            JSONArray information = checkObj.getJSONArray("information");
            if (information == null ||information.isEmpty()) continue;
            HashMap<Object, Object> deviceMap = Maps.newHashMap();
            for (Object typeObj:information){
                //遍历二级菜单
                JSONObject TypeObj = JSON.parseObject(JSON.toJSONString(typeObj));
                if (TypeObj == null || TypeObj.isEmpty()) continue;
                String name = TypeObj.getString("name");
                int total = 0;
                int waring = 0;
                //获取考评设备列表
                JSONArray deviceList = TypeObj.getJSONArray("information");
                if (deviceList == null || deviceList.isEmpty()) continue;
                if ("server".equalsIgnoreCase(name)){
                    //服务器
                    for (Object deviceObj:deviceList){
                        JSONObject device = JSON.parseObject(JSON.toJSONString(deviceObj));
                        String deviceName = device.getString("name");
                        JSONObject jsonObject = serversMap.get(deviceName);
                        if (jsonObject == null || jsonObject.isEmpty()) continue;
                        total += 1;
                        Integer severity = jsonObject.getInteger("severity");
                        if (severity != 5) waring += 1;
                    }
                }else if ("sql".equalsIgnoreCase(name)){
                    //数据库
                    for (Object deviceObj:deviceList){
                        JSONObject device = JSON.parseObject(JSON.toJSONString(deviceObj));
                        String deviceName = device.getString("name");
                        Element element = sqlMap.get(deviceName);
                        if (element == null) continue;
                        total += 1;
                        String healthstatus = element.attr("HEALTHSTATUS");
                        if (!"clear".equalsIgnoreCase(healthstatus)) waring += 1;
                    }
                }else if("middleware".equalsIgnoreCase(name)){
                    //中间件
                    for (Object deviceObj:deviceList){
                        JSONObject device = JSON.parseObject(JSON.toJSONString(deviceObj));
                        String deviceName = device.getString("name");
                        Element element = middlewareMap.get(deviceName);
                        if (element == null) continue;
                        total += 1;
                        String healthstatus = element.attr("HEALTHSTATUS");
                        if (!"clear".equalsIgnoreCase(healthstatus)) waring += 1;
                    }
                }else if("物联网设备".equalsIgnoreCase(name)){
                    //物联网设备
                }
                deviceMap.put(name,waring+"/"+total);
            }
            symbolJson.put("name",symbol);
            symbolJson.put("information",deviceMap);
            list.add(symbolJson);
        }
        return list;
    }

    public ResultJson<List> getAnalyResult(){
        return HttpWebResult.getMonoSucResult(getCheckObjectAnalyResult());
    }
}
