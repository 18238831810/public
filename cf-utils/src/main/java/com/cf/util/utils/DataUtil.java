package com.cf.util.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author frank
 * 2019/8/20
 **/
@Slf4j
public class DataUtil {

    /**
     * 可用
     * @param data
     * @return
     */
    public static boolean checkIsUsable(Long data){
        return data != null && data > 0;
    }

    /**
     * 可用
     * @param data
     * @return
     */
    public static boolean checkIsUsable(Integer data){
        return data != null && data > 0;
    }

    /**
     * list不为空
     * @param list
     * @return
     */
    public static boolean listNotEmpty(Collection list){
        return list != null && !list.isEmpty();
    }

    /**
     * map不为空
     * @param map
     * @return
     */
    public static boolean mapNotEmpty(Map map){
        return map != null && !map.isEmpty();
    }

    /**
     * json不为空
     * @param json
     * @return
     */
    public static boolean jsonNotEmpty(JSONObject json){
        return json != null && !json.isEmpty();
    }



    /**
     * 增删改通用工具类
     * @param function
     * @return
     */
    public static ResultJson<String> crud(Function<Object,Object> function) {
        try {
            function.apply(1);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            if (DuplicateKeyException.class == e.getClass()) return HttpWebResult.getMonoError("请勿重复添加");
            return HttpWebResult.getMonoError(e.getMessage());
        }
        return HttpWebResult.getMonoSucStr();
    }

    /**
     * 增删改通用工具类
     * @param function
     * @return
     */
    public static ResultJson<String> crud(Function<Object,Object> function, Consumer<Object> postHandle) {
        try {
            function.apply(1);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            if (DuplicateKeyException.class == e.getClass()) return HttpWebResult.getMonoError("请勿重复添加");
            return HttpWebResult.getMonoError(e.getMessage());
        }
        postHandle.accept(1);
        return HttpWebResult.getMonoSucStr();
    }


}
