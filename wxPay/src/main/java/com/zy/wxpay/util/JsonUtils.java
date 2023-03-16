package com.zy.wxpay.util;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;

public class JsonUtils {

    private JsonUtils() {
    }

    private static Gson gson = new Gson();

    /**
     * json转换为对象方法
     *
     * @param json  json字符串
     * @param clazz 对象class
     * @return 转换的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }


    /**
     * 对象转换为json字符串
     *
     * @param obj 需要转换的对象
     * @return json字符串
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
    * json中的uft8字符串转中文
    */
    public static JSONArray jsonParse(String jsonStr){
        JSONArray response=null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             response = objectMapper.readValue(jsonStr, JSONArray.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
