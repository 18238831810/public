package com.cf.util.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: JsonUtils
 * @Description:Jackson操作常用工具类
 * @author: spark
 * @date: Aug 20, 2019 3:51:28 PM
 */
@Slf4j
public class JsonUtils {
    private static ObjectMapper objectMapper;
    private static final String TIME_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private String timeFormat;

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
        objectMapper.setDateFormat(new SimpleDateFormat(timeFormat));
    }

    public JsonUtils() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_DATE_FORMAT));
    }

    public JsonUtils(String timeFormat) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat(timeFormat));
    }

    /**
     * 对象转json字符串
     *
     * @param object
     * @return
     */
    public String toJSon(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("对象转json字符串", e);
        }
        return "";
    }

    /**
     * 字符串转化为对象
     *
     * @param v
     * @param json
     * @param <T>
     * @return
     */
    public <T> T getObjectFromStr(Class<T> v, String json) {
        try {
            return objectMapper.readValue(json.getBytes(), objectMapper.constructType(v));
        } catch (IOException e) {
            log.error("字符串转化为对象异常", e);
        }
        return null;
    }

    /**
     * HashMap对象转对象
     *
     * @param v
     * @param map
     * @param <T>
     * @return
     */
    public <T> T getObjectFromMap(Class<T> v, HashMap<String, Object> map) {
        return objectMapper.convertValue(map, objectMapper.getTypeFactory().constructType(v));
    }

    /**
     * 字符串转化为ArrayList对象
     *
     * @param v
     * @param json
     * @param <T>
     * @return
     */
    @SuppressWarnings("deprecation")
    public <T> List<T> getArrayListObjectFromStr(Class<T> v, String json) {
        try {
            JavaType javaType = getCollectionType(ArrayList.class, v);
            List<T> lst = (List<T>) objectMapper.readValue(json, javaType);
            return lst;
        } catch (IOException e) {
            log.error("字符串转化为ArrayList对象异常", e);
        }
        return null;
    }

    /**
     * 字符串转化为ArrayList的HashMap对象
     *
     * @param json
     * @param <T>
     * @return
     */
    public <T> List<T> getArrayListMapFromStr(String json) {
        try {
            return objectMapper.readValue(json.getBytes(), objectMapper.getTypeFactory().constructParametricType(ArrayList.class, HashMap.class));
        } catch (IOException e) {
            log.error("字符串转化为ArrayList的HashMap对象异常", e);
        }
        return null;
    }

    /**
     * javaBean、列表数组转换为json字符串,忽略空值
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String obj2jsonIgnoreNull(Object obj) throws Exception {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * json字符串转换为map
     *
     * @param jsonString
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, Object> json2map(String jsonString) throws Exception {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper.readValue(jsonString, Map.class);
    }

    /**
     * json字符串转换为map
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Map<String, T> json2map(String jsonString, Class<T> clazz) throws Exception {
        Map<String, Map<String, Object>> map = objectMapper.readValue(jsonString, new TypeReference<Map<String, T>>() {
        });
        Map<String, T> result = new HashMap<String, T>();
        for (Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            result.put(entry.getKey(), map2pojo(entry.getValue(), clazz));
        }
        return result;
    }


    /**
     * 获取泛型的Collection Type
     *
     * @param collectionClass 泛型的Collection
     * @param elementClasses  元素类
     * @return JavaType Java类型
     * @since 1.0
     */
    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }


    /**
     * map转JavaBean
     *
     * @param map
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T map2pojo(Map map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }

    /**
     * map转json
     *
     * @param map
     * @return
     */
    public static String mapToJson(Map map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        JsonUtils json = new JsonUtils();
    }

}
