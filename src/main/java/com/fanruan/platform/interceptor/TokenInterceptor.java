package com.fanruan.platform.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fanruan.platform.bean.OpenAPIToken;
import com.fanruan.platform.bean.RecordInterfaceCall;
import com.fanruan.platform.dao.OpenAPITokenDao;
import com.fanruan.platform.dao.RecordInterfaceCallDao;
import com.fanruan.platform.util.PasswordEncoder;
import com.fanruan.platform.util.TokenUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private OpenAPITokenDao openAPITokenDao;
    private static Set<String> blackSet = Sets.newHashSet();

    @Autowired
    private RecordInterfaceCallDao recordInterfaceCallDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception{
        if(request.getMethod().equals("OPTIONS")){
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        RecordInterfaceCall call = new RecordInterfaceCall();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        call.setRequestTime(sdf.format(new Date()));
        response.setCharacterEncoding("utf-8");
        String token = request.getHeader("token");
        String authToken = request.getHeader("X-AURORA-TOKEN");
        String requestURI = request.getRequestURI();
        String tokenId = request.getParameter("tokenId");
        System.out.println(requestURI);
        if(requestURI.startsWith("//common/ZXB/downloadPDF/") || requestURI.startsWith("/common/ZXB/downloadPDF/")|| requestURI.startsWith("//company/ZCX/getPDF/") || requestURI.startsWith("/company/ZCX/getPDF/") ||requestURI.contains("getAllBusinessInfoNoToken") ||
                requestURI.contains("getApplyProgressListNoToken")){
            return true;
        }

        if(StringUtils.isNotBlank(tokenId)){
            Optional<OpenAPIToken> tokenOptional = openAPITokenDao.findByTokenId(tokenId);
            if(tokenOptional.isPresent()){
                OpenAPIToken openAPIToken = tokenOptional.get();
                String tokenKey = openAPIToken.getTokenKey();
                String uri = openAPIToken.getUri();
                boolean passwordValid = PasswordEncoder.isPasswordValid(authToken, tokenKey);
//               if(passwordValid&&StringUtils.contains(requestURI,uri)){
                if(passwordValid&&StringUtils.startsWith(requestURI,uri)){
                   call.setTokenId(openAPIToken.getTokenId());
                   call.setUri(requestURI);
                   call.setRequestMessage(getParams(request));
                   recordInterfaceCallDao.saveAndFlush(call);
                   System.out.println("加密对外api，跳过登录认证");
                   return true;
               }
            }
        }
        if(token != null){
            boolean result = TokenUtil.verify(token);
            if(blackSet.contains(token)){
                JSONObject json = new JSONObject();
                json.put("msg"," tocken loginOUt ");
                json.put("code","49998");
                response.getWriter().append(json.toJSONString());
                return false;
            }
            if(request.getRequestURI().contains("/user/loginOut")){
                blackSet.add(token);
            }
            if(result){
                System.out.println("通过拦截器");
                call.setUri(requestURI);
                call.setRequestMessage(getParams(request));
                recordInterfaceCallDao.saveAndFlush(call);
                return true;
            }
        }else {
            JSONObject json = new JSONObject();
            json.put("msg"," no token to verify ");
            json.put("code","49999");
            response.getWriter().append(json.toJSONString());
            return false;
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try{
            JSONObject json = new JSONObject();
            json.put("msg","token verify fail");
            json.put("code","50000");
            response.getWriter().append(json.toJSONString());
            System.out.println("认证失败，未通过拦截器");
        }catch (Exception e){
            e.printStackTrace();
            response.sendError(500);
            return false;
        }
        return false;
    }

    private String getParams(HttpServletRequest request){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}