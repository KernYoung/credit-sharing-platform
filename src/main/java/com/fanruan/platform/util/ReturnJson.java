package com.fanruan.platform.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class ReturnJson {

    public static String getJson(String code,String msg,Object obj) throws Exception{
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code",code);
        hs.put("msg",msg);
        hs.put("data",obj);
        return objectMapper.writeValueAsString(hs);
    }
}
