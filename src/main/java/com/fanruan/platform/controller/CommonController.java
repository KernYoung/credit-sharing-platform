package com.fanruan.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.NationCodeDao;
import com.fanruan.platform.etl.RiskPushTimer;
import com.fanruan.platform.service.CommonService;
import com.fanruan.platform.service.CompanyService;
import com.fanruan.platform.service.InputPointsService;
import com.fanruan.platform.service.UserService;
import com.fanruan.platform.util.CommonUtil;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.MD5Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.sinosure.exchange.edi.po.ArrayOfEdiFeedback;
import com.sinosure.exchange.edi.po.ArrayOfEntrustInput;
import com.sinosure.exchange.edi.po.EdiFeedback;
import com.sinosure.exchange.edi.po.EntrustInput;
import com.sinosure.exchange.edi.service.EdiException_Exception;
import com.sinosure.exchange.edi.service.SolEdiProxyWebService;
import com.sinosure.exchange.edi.service.SolEdiProxyWebServicePortType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class CommonController {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Value(value = "${local.schema}")
    private String schema;
    @Value("${local.host}")
    private String host;
    @Value("${local.port}")
    private String port;
    @Value(value = "${interfaceIssued.schema}")
    private String interfaceIssuedSchema;
    @Value("${interfaceIssued.ip}")
    private String interfaceIssuedIp;
    @Value("${interfaceIssued.port}")
    private String interfaceIssuedPort;
    @Autowired
    private UserService userService;

    @Autowired
    private NationCodeDao nationCodeDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "/common/getArea", method = RequestMethod.POST)
    @ResponseBody
    public String getAreaInfo(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
//        String areaCode = (String) param.get("areaCode");
        List<Area> areaList = RiskPushTimer.areaListCache;
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("areaList",areaList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getUserCompany", method = RequestMethod.POST)
    @ResponseBody
    public String getUserCompany(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String operator = (String) param.get("operator");
        List<UserCompany> userCompanyList = commonService.getUserCompany(operator);
        hs.put("userCompanyList",userCompanyList);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getNationCode", method = RequestMethod.POST)
    @ResponseBody
    public String getNationCode(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<NationCode> nationCode = commonService.getAreaInfo();
        hs.put("nationCode",nationCode);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getIndustry", method = RequestMethod.POST)
    @ResponseBody
    public String getIndustry(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
//        String areaCode = (String) param.get("areaCode");
        Map<String,List<String>> areaList = RiskPushTimer.industryListCache;
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("areaList",areaList);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getBlackList", method = RequestMethod.POST)
    @ResponseBody
    public String getBlackList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
//        String userCode = (String) param.get("userCode");
        String sortCriteria = (String) param.get("sortCriteria");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        List<BlackListDetailResultList> blackList = commonService.getBlackList(pageIndex,pageSize,sortCriteria);
        Integer total = commonService.getBlackListTotalCount();
        hs.put("blackList",blackList);
        hs.put("total",total);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getGreyList", method = RequestMethod.POST)
    @ResponseBody
    public String getGreyList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
//        String userCode = (String) param.get("userCode");
        String sortCriteria = (String) param.get("sortCriteria");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        List<BlackListDetailResultList> greyList = commonService.getGreyList(pageIndex,pageSize,sortCriteria);
        Integer total = commonService.getGreyListTotalCount();
        hs.put("greyList",greyList);
        hs.put("total",total);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPermissionRoles", method = RequestMethod.POST)
    @ResponseBody
    public String getPermissionRoles(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<PermissionPoint> permissionRoles = commonService.getPermissionRoles();
        hs.put("permissionRoles",permissionRoles);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPermissionPointList", method = RequestMethod.POST)
    @ResponseBody
    public String getPermissionPointList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<PermissionPoint> permissionPointList = commonService.getPermissionPointList();
        hs.put("permissionPointList",permissionPointList);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPermissionPoint", method = RequestMethod.POST)
    @ResponseBody
    public String getPermissionPoint(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        PermissionPoint permissionPoint = commonService.getPermissionPoint(param);
        hs.put("permissionPoint",permissionPoint);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPermissionRolePoint", method = RequestMethod.POST)
    @ResponseBody
    public String getPermissionRolePoint(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<PermissionPoint> permissionPoint = commonService.getPermissionRolePoint(param);
        hs.put("permissionPoint",permissionPoint);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getIsBlack", method = RequestMethod.POST)
    @ResponseBody
    public String getIsBlack(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Long currentCompanyId = param.get("id") == null ? -1 :  CommonUtils.getLongValue(param.get("id"));
        boolean isBlack = false;
        List<BlackListDetailResultList> blackList = commonService.getBlackList(null,null,null);
        for (BlackListDetailResultList blackInfo : blackList ){
            Long blackListCompanyId = blackInfo.getTycCompanyId() == null ? 0 : blackInfo.getTycCompanyId();
            isBlack = blackListCompanyId.equals(currentCompanyId);
            if(isBlack){
                break;
            }
        }
        hs.put("isBlack",isBlack);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getIsGrey", method = RequestMethod.POST)
    @ResponseBody
    public String getIsGrey(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Long currentCompanyId = param.get("id") == null ? -1 :  CommonUtils.getLongValue(param.get("id"));
        boolean isGrey = false;
        List<BlackListDetailResultList> greyList = commonService.getGreyList(null,null,null);
        for (BlackListDetailResultList greyInfo : greyList ){
            Long greyListCode = greyInfo.getTycCompanyId() == null ? 0 : greyInfo.getTycCompanyId();
            isGrey = greyListCode.equals(currentCompanyId);
            if(isGrey){
                break;
            }
        }
        hs.put("isGrey",isGrey);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/savePermissionPoint", method = RequestMethod.POST)
    @ResponseBody
    public String savePermissionPoint(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer id = CommonUtils.getIntegerValue((String)param.get("id"));
        PermissionPoint permissionPoint = commonService.savePermissionPoint(id,param);
        hs.put("permissionPoint",permissionPoint);
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 工商信息
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1001", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1001(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "1001";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/cb/ic/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",dataOutput);
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

//    /**
//     * 工商信息（无需转译）
//     * @param param
//     * @param request
//     * @return
//     * @throws JsonProcessingException
//     */
//    @RequestMapping(value = "/common/getJson/1001_2", method = RequestMethod.POST)
//    @ResponseBody
//    public String getJson1001_2(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
//        HashMap<String,Object> hs=new HashMap<>();
//        ObjectMapper objectMapper=new ObjectMapper();
//        String name = (String)param.get("name");
//        String tokenId = request.getParameter("tokenId");
//        String jsonFlag = "1001";
//
//        Map<String, String> paramMap = Maps.newHashMap();
//        paramMap.put("keyword",name);
//
//        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
//        if(companyName == null || companyName.isEmpty()){
//            hs.put("result","");
//            hs.put("code",1);
//            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
//            return objectMapper.writeValueAsString(hs);
//        }
//
//        Integer isOverUsage = commonService.getIsOverUsage(jsonFlag, companyName);
//
//        if(isOverUsage == 1){
//            hs.put("result","");
//            hs.put("code",1);
//            hs.put("msg","失败，调用次数已超当日上限！jsonFlag: " + jsonFlag + ",companyName: " + companyName);
//            return objectMapper.writeValueAsString(hs);
//        }else if(isOverUsage == -1){
//            hs.put("result","");
//            hs.put("code",1);
//            hs.put("msg","失败，未能获取到接口限制数量！jsonFlag: " + jsonFlag + ",companyName: " + companyName);
//            return objectMapper.writeValueAsString(hs);
//        }else if(isOverUsage == -2){
//            hs.put("result","");
//            hs.put("code",1);
//            hs.put("msg","失败，未能获取到接口已使用数量！jsonFlag: " + jsonFlag + ",companyName: " + companyName);
//            return objectMapper.writeValueAsString(hs);
//        }
//
//        String requestUrl = "/services/open/cb/ic/2.0?";
//        //need adjust
//        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap);
//
//        hs.put("result",JSONObject.parse(dataOutput));
//        hs.put("code",0);
//        hs.put("msg","");
//        return objectMapper.writeValueAsString(hs);
//    }
    /**
     * 工商信息（无需转译）
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1001_2", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1001_2(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String tokenId = request.getParameter("tokenId");
        String jsonFlag = "1001";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/cb/ic/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 企业基本信息
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/818", method = RequestMethod.POST)
    @ResponseBody
    public String getJson818(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "818";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/ic/baseinfoV2/2.0?";

        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",dataOutput);
            hs.put("code",0);
            hs.put("msg","");
        }
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 企业基本信息（无需转译）
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/818_2", method = RequestMethod.POST)
    @ResponseBody
    public String getJson818_2(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "818";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/ic/baseinfoV2/2.0?";

        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }


    /**
     * 司法风险
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1002", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1002(@RequestBody Map<String,Object> param,HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String tokenId = request.getParameter("tokenId");
        String jsonFlag = "1002";
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/cb/judicial/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",dataOutput);
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 司法风险（无需转译）
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1002_2", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1002_2(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "1002";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/cb/judicial/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 联系信息
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/966", method = RequestMethod.POST)
    @ResponseBody
    public String getJson966(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "966";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/stock/corpContactInfo/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",dataOutput);
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 联系信息（无需转译）
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/966_2", method = RequestMethod.POST)
    @ResponseBody
    public String getJson966_2(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "966";

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/stock/corpContactInfo/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 法律诉讼
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1114", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1114(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "1114";
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);
        if(pageSize != null ) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null ) paramMap.put("pageNum",pageNum.toString());

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/jr/lawSuit/3.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 历史法律诉讼
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1115", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1115(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "1115";
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);
        if(pageSize != null) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null) paramMap.put("pageNum",pageNum.toString());

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/hi/lawSuit/3.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 欠税公告
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/851", method = RequestMethod.POST)
    @ResponseBody
    public String getJson851(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "851";
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);
        if(pageSize != null) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null) paramMap.put("pageNum",pageNum.toString());

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/mr/ownTax/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 税务评级
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/884", method = RequestMethod.POST)
    @ResponseBody
    public String getJson884(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String name = (String)param.get("name");
        String jsonFlag = "884";
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",name);
        if(pageSize != null) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null) paramMap.put("pageNum",pageNum.toString());

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/m/taxCredit/2.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 行政处罚
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1124", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1124(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String keyword = (String)param.get("name");
        String jsonFlag = "1124";
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("keyword",keyword);
        if(pageSize != null) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null) paramMap.put("pageNum",pageNum.toString());


        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }

        String requestUrl = "/services/open/mr/punishmentInfo/3.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 专利信息
     * @param param
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/getJson/1137", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1137(@RequestBody Map<String,Object> param, HttpServletRequest request) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String tokenId = request.getParameter("tokenId");
        String pubDateBegin = (String)param.get("pubDateBegin");
        String appDateBegin = (String)param.get("appDateBegin");
        String pubDateEnd = (String)param.get("pubDateEnd");
        String appDateEnd = (String)param.get("appDateEnd");
        String keyword = (String)param.get("keyword");
        Integer patentType = CommonUtils.getIntegerValue(param.get("patentType"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        Integer pageNum = CommonUtils.getIntegerValue(param.get("pageNum"));
        String jsonFlag = "1137";

        Map<String, String> paramMap = Maps.newHashMap();
        if(pubDateBegin != null) paramMap.put("pubDateBegin",pubDateBegin);
        if(appDateBegin != null) paramMap.put("appDateBegin",appDateBegin);
        if(pubDateEnd != null) paramMap.put("pubDateEnd",pubDateEnd);
        if(appDateEnd != null) paramMap.put("appDateEnd",appDateEnd);
        paramMap.put("keyword",keyword);
        if(patentType != null) paramMap.put("patentType",patentType.toString());
        if(pageSize != null) paramMap.put("pageSize",pageSize.toString());
        if(pageNum != null) paramMap.put("pageNum",pageNum.toString());

        String companyName = commonService.getCompanyNamebyTokenId(tokenId);
        if(companyName == null || companyName.isEmpty()){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg","失败，未能通过tokenId关联到成员公司，请咨询门户管理员。");
            return objectMapper.writeValueAsString(hs);
        }


        String requestUrl = "/services/open/ipr/patents/3.0?";
        //need adjust
        String dataOutput = commonService.getDataOutputAndInput2DB(requestUrl, jsonFlag, tokenId, paramMap, companyName);

        if(dataOutput.startsWith("失败")){
            hs.put("result","");
            hs.put("code",1);
            hs.put("msg",dataOutput);
        }else{
            hs.put("result",JSONObject.parse(dataOutput));
            hs.put("code",0);
            hs.put("msg","");
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/logCreditOper", method = RequestMethod.POST)
    @ResponseBody
    public String logCreditOper(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        commonService.saveLog(param);
        hs.put("code",0);
        hs.put("msg","日志记录成功");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/searchApplyList",method = RequestMethod.POST)
    @ResponseBody
    public String searchUserListNew(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        Integer pageIndex = CommonUtils.getIntegerValue(para.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(para.get("pageSize"));
        String operator = (String)para.get("operator");
        Integer isSubAdmin = CommonUtils.getIntegerValue(para.get("isSubAdmin"));
        String zxbCode = (String)para.get("zxbCode");
        String zxbCompanyName = (String)para.get("zxbCompanyName");
        String zxbApprove = (String)para.get("zxbApprove");
        String approveCode = "";
       if(null != zxbApprove){
           if(zxbApprove.equals("通过")){
               approveCode = "1";
           }else if(zxbApprove.equals("不通过")){
               approveCode = "999";
           }else if(zxbApprove.equals("异常")){
               approveCode = "0";
           }else if(zxbApprove.equals("待审核")){
               approveCode = "2";
           }
       }
        String zxbInformant = (String)para.get("zxbInformant");
        String zxbApprover = (String)para.get("zxbApprover");
        HashMap<String,Object> hs=new HashMap<>();
        List<ZhongXinBaoLog> applyList = null;
        hs =  commonService.searchApplyList(hs,pageIndex,pageSize,zxbCode,zxbCompanyName,approveCode,zxbInformant,zxbApprover,isSubAdmin,operator);
        applyList = (List<ZhongXinBaoLog> )hs.get("applyList");
        hs.put("applyList",applyList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getJson/getDetailUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getDetailUrl(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        String userName = (String)param.get("userName");
        String companyName = (String)param.get("companyName");
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String dataStr = "";
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(companyName)){
            hs.put("code", "500");
            hs.put("msg", "Username or company name is empty");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        User userByUsername = userService.getUserByUsername(userName);
        Company company =  companyService.getCompanyByName(companyName);
        if(null == userByUsername || null == userByUsername.getUsername() || null == userByUsername.getUserId()){
            hs.put("code", "404");
            hs.put("msg", "User does not exist");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        if(null == company){
            company = companyService.creditCompany(companyName, userByUsername.getUserId());
        }



        if(null == company || null == company.getId()){
            hs.put("code", "404");
            hs.put("msg", "The company information is not found");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        String tycid = company.getId().toString();
        String username = "zjb_"+userByUsername.getUsername();
        String sign = MD5Util.MD5(username+"44bce5ef-873e-4689-b515-a1ef9775aa82");
        String url = "https://pro.tianyancha.com/cloud-std-security/aut/login.json?username="+username+"&authId=lf2b4yqy4lsfgp1x&sign="+sign+"&redirectUrl=/company/"+tycid+"/background";
        /*String sign1 = MD5Util.MD5(userByUsername.getUsername()+"44bce5ef-873e-4689-b515-a1ef9775aa82");
        String MHurl = "http://localhost:9001/xinTuoCompanyBasicInfo?id="+company.getId()+"&companyName="+company.getCompanyName()+"&companyId="+company.getCompanyId()+"&creditCode="+company.getCreditCode()+"&index=0&userName="+userByUsername.getUsername()+"&sign="+sign1;*/
        hs.put("code", "0");
        hs.put("msg", "success");
        hs.put("id", company.getCompanyId());
        hs.put("url", url);
        hs.put("companyName", company.getCompanyName());
        return objectMapper.writeValueAsString(hs);
    }

//    @RequestMapping(value = "/common/getJson/getDetailUrl", method = RequestMethod.POST)
//    @ResponseBody
//    public String getDetailUrl(@RequestBody Map<String,Object> param) throws JsonProcessingException {
//        String userName = (String)param.get("userName");
//        String companyName = (String)param.get("companyName");
//        HashMap<String,Object> hs=new HashMap<>();
//        ObjectMapper objectMapper=new ObjectMapper();
//        String dataStr = "";
//        if(StringUtils.isBlank(userName) || StringUtils.isBlank(companyName)){
//            hs.put("code", "500");
//            hs.put("msg", "Username or company name is empty");
//            hs.put("id", "");
//            hs.put("url", "");
//            hs.put("companyName", "");
//            return objectMapper.writeValueAsString(hs);
//        }
//        User userByUsername = userService.getUserByUsername(userName);
//        Company company =  companyService.getCompanyByName(companyName);
//        if(null == userByUsername || null == userByUsername.getUsername() || null == userByUsername.getUserId()){
//            hs.put("code", "404");
//            hs.put("msg", "User does not exist");
//            hs.put("id", "");
//            hs.put("url", "");
//            hs.put("companyName", "");
//            return objectMapper.writeValueAsString(hs);
//        }
//        if(null == company){
//            Map<String,Object> resultMap = new HashMap<>();
//            resultMap = companyService.creditCompanyNew(companyName, userByUsername.getUserId());
//            for(String str : resultMap.keySet()){
//                if(str.equals("company")){
//                    company = (Company) resultMap.get(str);
//                }
//                if(str.equals("resultStr")){
//                    dataStr = (String) resultMap.get(str);
//                }
//            }
//
//            String jsonFlag = "1001";
//            User user =  userService.getUserByUsername(userName);
//            String companyCode = user == null ? "" : user.getCompanyCode();
//            String tokenId = companyService.getTokenIdByCompanyCode(companyCode,"getBaseInfo");
//            if(StringUtils.isBlank(tokenId)) log.info("获取tokenId失败，userName: " + userName + ", jsonFlag: 1001");
//
//            if(StringUtils.isBlank(dataStr)){
//                commonService.SaveLocalJsonWithoutDataStr(companyName,jsonFlag,tokenId);
//            }else{
//                commonService.SaveLocalJson1(dataStr,jsonFlag,tokenId);
//            }
//        }
//
//
//
//        if(null == company || null == company.getId()){
//            hs.put("code", "404");
//            hs.put("msg", "The company information is not found");
//            hs.put("id", "");
//            hs.put("url", "");
//            hs.put("companyName", "");
//            return objectMapper.writeValueAsString(hs);
//        }
//        String tycid = company.getId().toString();
//        String username = "zjb_"+userByUsername.getUsername();
//        String sign = MD5Util.MD5(username+"44bce5ef-873e-4689-b515-a1ef9775aa82");
//        String url = "https://pro.tianyancha.com/cloud-std-security/aut/login.json?username="+username+"&authId=lf2b4yqy4lsfgp1x&sign="+sign+"&redirectUrl=/company/"+tycid+"/background";
//        /*String sign1 = MD5Util.MD5(userByUsername.getUsername()+"44bce5ef-873e-4689-b515-a1ef9775aa82");
//        String MHurl = "http://localhost:9001/xinTuoCompanyBasicInfo?id="+company.getId()+"&companyName="+company.getCompanyName()+"&companyId="+company.getCompanyId()+"&creditCode="+company.getCreditCode()+"&index=0&userName="+userByUsername.getUsername()+"&sign="+sign1;*/
//        hs.put("code", "0");
//        hs.put("msg", "success");
//        hs.put("id", company.getCompanyId());
//        hs.put("url", url);
//        hs.put("companyName", company.getCompanyName());
//        return objectMapper.writeValueAsString(hs);
//    }


    @RequestMapping(value = "/common/getJson/getDetailUrlMH", method = RequestMethod.POST)
    @ResponseBody
    public String getDetailUrlMH(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        String userName = (String)param.get("userName");
        String companyName = (String)param.get("companyName");
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String dataStr = "";
        if(StringUtils.isBlank(userName) || StringUtils.isBlank(companyName)){
            hs.put("code", "500");
            hs.put("msg", "userName or companyName is empty");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        User userByUsername = userService.getUserByUsername(userName);
        Company company =  companyService.getCompanyByName(companyName);
        if(null == userByUsername || null == userByUsername.getUsername() || null == userByUsername.getUserId()){
            hs.put("code", "404");
            hs.put("msg", "User does not exist");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        if(null == company){
            company = companyService.creditCompany(companyName, userByUsername.getUserId());
        }
        if(null == company || null == company.getId()){
            hs.put("code", "404");
            hs.put("msg", "The company information is not found");
            hs.put("id", "");
            hs.put("url", "");
            hs.put("companyName", "");
            return objectMapper.writeValueAsString(hs);
        }
        String tycid = company.getId().toString();
        /*String username = "zjb_"+userByUsername.getUsername();*/
       /* String sign = MD5Util.MD5(username+"44bce5ef-873e-4689-b515-a1ef9775aa82");*/
        /*String url = "https://pro.tianyancha.com/cloud-std-security/aut/login.json?username="+username+"&authId=lf2b4yqy4lsfgp1x&sign="+sign+"&redirectUrl=/company/"+tycid+"/background";*/
        String sign = MD5Util.MD5(userByUsername.getUsername()+"44bce5ef-873e-4689-b515-a1ef9775aa82");

        String url = schema+"://"+host+":"+port+"/xinTuoCompanyBasicInfo?id="+company.getId()+"&companyName="+company.getCompanyName()+"&companyId="+company.getCompanyId()+"&creditCode="+company.getCreditCode()+"&index=0&userName="+userByUsername.getUsername()+"&sign="+sign;
        hs.put("code", "0");
        hs.put("msg", "success");
        hs.put("id", company.getCompanyId());
        hs.put("url", url);
        hs.put("companyName", company.getCompanyName());
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/ZXB/getZXBReportSummary", method = RequestMethod.POST)
    @ResponseBody
    public String getZXBReportSummary(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String reportcorpchnname = (String)param.get("reportcorpchnname");
        String reportcorpengname = (String)param.get("reportcorpengname");
        String reportbuyerno = (String)param.get("reportbuyerno");

        if(reportbuyerno == null || reportbuyerno.isEmpty()){
            hs.put("code", "500");
            hs.put("msg", "reportbuyerno为必填项！");
            return objectMapper.writeValueAsString(hs);
        }

        String url = schema+"://"+host+":"+port+"/zxbReportSummary?reportcorpchnname=" + reportcorpchnname +
                "&reportcorpengname=" + reportcorpengname + "&reportbuyerno=" + reportbuyerno;

        hs.put("code", "0");
        hs.put("msg", "success");
        hs.put("url", url);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/saveInitialScreeningOfMerchants", method = RequestMethod.POST)
    @ResponseBody
    public String saveInitialScreeningOfMerchants(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String serialid = (String) param.get("serialNumber");
        String updateBy = (String) param.get("userName");
        List list = (List) param.get("formInfo");
        if(StringUtils.isBlank(serialid)){
            hs.put("msg", "流水号不能为空");
            hs.put("code", "1");
            return objectMapper.writeValueAsString(hs);
        }
        if(StringUtils.isBlank(updateBy)){
            hs.put("msg", "填报人不能为空");
            hs.put("code", "2");
            return objectMapper.writeValueAsString(hs);
        }
        if(null == list || list.size() == 0){
            hs.put("msg", "填报数据为空");
            hs.put("code", "3");
            return objectMapper.writeValueAsString(hs);
        }
        commonService.saveInitialScreeningOfMerchants(serialid,list,updateBy);
        hs.put("msg", "填报信息录入成功");
        hs.put("code", "0");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getSearchFillInInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getSearchFillInInfo(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        if(StringUtils.isBlank(userName)){
            hs.put("msg", "填报人为空");
            hs.put("code", "1");
            hs.put("allUpdataBy", "");
            hs.put("allSerialid", "");
            return objectMapper.writeValueAsString(hs);
        }
        Map<String, List<List<String>>> searchFillInInfo = commonService.getSearchFillInInfo(userName);
        if(null == searchFillInInfo || searchFillInInfo.size() == 0){
            hs.put("msg", "未查询到填报人和流水号");
            hs.put("code", "2");
            hs.put("allUpdataBy", "");
            hs.put("allSerialid", "");
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("msg", "");
        hs.put("code", "0");
        hs.put("allUpdataBy", searchFillInInfo.get("allUpdataBy"));
        hs.put("allSerialid", searchFillInInfo.get("allSerialid"));;
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getMerchantsViewResults", method = RequestMethod.POST)
    @ResponseBody
    public String getMerchantsViewResults(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String updataBy = (String)param.get("updataBy");
        String serialid = (String)param.get("serialid");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        if(StringUtils.isBlank(updataBy) || StringUtils.isBlank(serialid)){
            hs.put("totalRecords","");
            hs.put("totalPages","");
            hs.put("merchantsViewResults","");
            hs.put("code",1);
            hs.put("msg", "填报人或流水号为空");
            return objectMapper.writeValueAsString(hs);
        }
        hs = commonService.getMerchantsViewResults(hs,updataBy,serialid,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getAllBlackListResultList", method = RequestMethod.POST)
    @ResponseBody
    public String getAllBlackListResultList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<Integer> status = (ArrayList<Integer>)param.get("status");
        String publishBy = (String) param.get("publishBy");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        if(null == status || StringUtils.isBlank(publishBy)){
            hs.put("totalRecords","");
            hs.put("totalPages","");
            hs.put("blacklistResultList","");
            hs.put("code",0);
            hs.put("msg", "审核状态和申请人不能为空！");
            return objectMapper.writeValueAsString(hs);
        }
        hs  = commonService.getAllBlackListResultList(hs, status, publishBy, pageIndex, pageSize);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getBlacklistApprovalList", method = RequestMethod.POST)
    @ResponseBody
    public String getBlacklistApprovalList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String publishBy = (String) param.get("publishBy");
        List<Integer> status = (ArrayList<Integer>)param.get("status");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs  = commonService.getBlacklistApprovalList(hs, status, pageIndex, pageSize,publishBy);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getBlackListDetailList", method = RequestMethod.POST)
    @ResponseBody
    public String getBlackListDetailList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String companyName = (String) param.get("companyName");
        String dataSource = (String) param.get("dataSource");
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        String userCode = (String) param.get("userCode");
//        String publishBy = (String) param.get("publishBy");
//        List<Integer> status = (ArrayList<Integer>)param.get("status");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs  = commonService.getBlackListDetailList(hs, pageIndex, pageSize, companyName, dataSource, startDate, endDate,userCode);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/downloadDocument", method = RequestMethod.POST)
    @ResponseBody
    public String downloadDocument(HttpServletResponse response, @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String fileName = (String)param.get("fileName");
//        String filePath = "/home/ftpuser/";
//        String fileName = noticeSerialno;
        File file = new File(fileName);
        try {
            //加载文件
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            IOUtils.copy(is,outputStream,1024);
            hs.put("code","0");
        } catch (Exception e) {
            e.printStackTrace();
            hs.put("code","1");
            hs.put("msg","文件下载出错");
        }
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public String uploadFile(HttpServletRequest request) throws JsonProcessingException{
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        for (MultipartFile file:files) {
            if (file.isEmpty()) {
                hs.put("code", "1");
                hs.put("msg","上传文件失败");
                return objectMapper.writeValueAsString(hs);
            }
            String fileName = file.getOriginalFilename();
//            String filePath = "/Users/chengbin/Downloads/";
            String filePath = "/home/ftpuser/upload/";
            File dest = new File(filePath + fileName);
            try {
                file.transferTo(dest);
                hs.put("code", "0");
                hs.put("msg","上传文件成功");
            } catch (IOException e) {
                hs.put("code", "2");
                hs.put("msg",e.getMessage());
                return objectMapper.writeValueAsString(hs);
            }
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/deleteFile", method = RequestMethod.POST)
    @ResponseBody
    public String deleteFile(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String filePathName = (String) param.get("filePathName");
        File file = new File(filePathName);
        if(file.exists()){
            if (file.delete()) {
                hs.put("msg", "文件删除成功！" + filePathName);
                hs.put("code","0");
            }else{
                hs.put("msg", "文件删除失败！" + filePathName);
                hs.put("code","0");
            }
        }else{
            hs.put("msg", "文件不存在！" + filePathName);
            hs.put("code","0");
        }
        return objectMapper.writeValueAsString(hs);
    }



    @RequestMapping(value = "/common/saveOrEdit", method = RequestMethod.POST)
    @ResponseBody
    public String saveOrEdit(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
            hs = commonService.saveOrEdit(hs,param);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/concern", method = RequestMethod.POST)
    @ResponseBody
    public String concern(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Map<String,Integer> relations = Maps.newHashMap();
        String dataStr = "";

        String userCode = (String)param.get("userCode");
        String companyName = (String)param.get("companyName");
        Integer tianyanchaFlag = CommonUtils.getIntegerValue(param.get("tianyanchaflag"));
        Integer zhongchengxinFlag = CommonUtils.getIntegerValue(param.get("zhongchengxinflag"));

        String enttype = (String) param.get("enttype");
        param.put("entType",enttype);
        param.put("entName",companyName);
        String countyName = (String) param.get("countyname");
        param.put("countyName",countyName);
        String countyCode = (String) param.get("countycode");
        param.put("countyCode", countyCode);
        String cityCode = (String) param.get("citycode");
        param.put("cityCode",cityCode);
        String cityName = (String) param.get("cityname");
        param.put("cityName",cityName);
        String areaLevel = (String) param.get("arealevel");
        param.put("areaLevel",areaLevel);
        String provinceCode = (String) param.get("provincecode");
        param.put("provinceCode",provinceCode);
        String provinceName = (String) param.get("provincename");
        param.put("provinceName",provinceName);

        if(StringUtils.isBlank(userCode) || StringUtils.isBlank(companyName)){
            hs.put("code","1");
            hs.put("msg","工号或者公司名称为空");
            hs.put("isConcern",false);
            return objectMapper.writeValueAsString(hs);
        }

         if((null == tianyanchaFlag || ( 1 != tianyanchaFlag && 0 != tianyanchaFlag))  &&  (null == zhongchengxinFlag || (1 != zhongchengxinFlag && 0 != zhongchengxinFlag))){
            hs.put("code","2");
            hs.put("msg","tianyanchaFlag 或者 zhongchengxinFlag  不正确！！！");
            hs.put("isConcern",false);
            return objectMapper.writeValueAsString(hs);
         }

        if(null != tianyanchaFlag && null != zhongchengxinFlag){
            hs.put("code","2");
            hs.put("msg","天眼查关注和中诚信关注/取消关注只能任选其一!");
            hs.put("isConcern",false);
            return objectMapper.writeValueAsString(hs);
        }


        User user = userService.getUserByUsername(userCode);
        Company company = companyService.getCompanyByName(companyName);

        if (null == user){
            hs.put("code","3");
            hs.put("msg","未查询到用户信息,请核对工号！");
            hs.put("isConcern",false);
            return objectMapper.writeValueAsString(hs);
        }

        if(null == company){
            company = companyService.creditCompany(companyName, user.getUserId());
        }

        if(null == company){
            hs.put("code","4");
            hs.put("msg","未检查到公司信息,请检查companyName的信息是否准确!");
            hs.put("isConcern",false);
            return objectMapper.writeValueAsString(hs);
        }


        if(StringUtils.isNotBlank(enttype) && "1".equals(enttype)){
            boolean isTrue = false;
            if(StringUtils.isBlank(areaLevel)){
                hs.put("code","5");
                hs.put("msg","中诚信传入的参数有误,行政级别不能为空!");
                hs.put("isConcern",false);
                return objectMapper.writeValueAsString(hs);
            }
            switch (areaLevel){
                case "省级":
                    isTrue = StringUtils.isBlank(provinceName) || StringUtils.isBlank(provinceCode) ? true : false;
                    hs.put("msg","中诚信传入的参数有误,省级名称或对应的code不能为空!");
                    break;
                case "地市级":
                    isTrue = StringUtils.isBlank(provinceName) || StringUtils.isBlank(provinceCode) ||  StringUtils.isBlank(cityName) || StringUtils.isBlank(cityCode)  ? true : false;
                    hs.put("msg","中诚信传入的参数有误,省级和城市或对应的code不能为空!");
                    break;
                case "区县级":
                    isTrue = StringUtils.isBlank(provinceName) || StringUtils.isBlank(provinceCode) ||  StringUtils.isBlank(cityName) || StringUtils.isBlank(cityCode) ||  StringUtils.isBlank(countyName) || StringUtils.isBlank(countyCode) ? true : false;
                    hs.put("msg","中诚信传入的参数有误,省级和城市和县级名称或对应的code不能为空!");
                    break;
            }
            if(isTrue){
                hs.put("code","5");
                hs.put("isConcern",false);
                return objectMapper.writeValueAsString(hs);
            }
        }

        if (null != tianyanchaFlag){
            relations.put("tianyancha",tianyanchaFlag);
        }

        if(null != zhongchengxinFlag){
            relations.put("zhongchengxin",zhongchengxinFlag);
        }

        boolean isConcern = companyService.updateConcernInfo(param, tianyanchaFlag, zhongchengxinFlag, company, user);
        userService.saveRelation(user, company,relations);

        hs.put("isConcern",isConcern);

        if((null != tianyanchaFlag && 0 == tianyanchaFlag) || (null != zhongchengxinFlag && 0 == zhongchengxinFlag)){
            if(isConcern){
                hs.put("code","0");
                hs.put("msg","取消关注成功！");
            }else{
                hs.put("code", "4");
                hs.put("msg","取消关注失败,公司的信报代码为空 或者 第三方接口调用失败，请检查参数是否正确");
            }
            return objectMapper.writeValueAsString(hs);
        }

        if(isConcern){
            hs.put("code","0");
            hs.put("msg","关注成功！");
        }else{
            hs.put("code", "4");
            hs.put("msg","关注失败,公司的信保代码为空 或者 第三方接口调用失败，请检查参数是否正确");
        }
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/getEventTypeList", method = RequestMethod.POST)
    @ResponseBody
    public String getEventTypeList(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        List<String> riskleve = ( ArrayList<String>) param.get("riskleve");
        List<String> companyName = ( ArrayList<String>) param.get("companyName");
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        List<String> eventTypeList = commonService.getEventTypeList(userName,riskleve,companyName,startDate,endDate);
        hs.put("code", "0");
        hs.put("msg", "");
        hs.put("eventTypeList", eventTypeList);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/getRealTimeWarning", method = RequestMethod.POST)
    @ResponseBody
    public String getRealTimeWarning(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        List<String> riskleve = ( ArrayList<String>) param.get("riskleve");
        List<String> companyName = ( ArrayList<String>) param.get("companyName");
        List<String> eventType = ( ArrayList<String>) param.get("eventType");
        riskleve.remove("全选");
        companyName.remove("全选");
        eventType.remove("全选");
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getRealTimeWarning(hs,userName,riskleve,companyName,eventType,startDate,endDate,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPlatformNews", method = RequestMethod.POST)
    @ResponseBody
    public String getPlatformNews(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getPlatformNews(hs,userName,startDate,endDate,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/getRiskMorningPost", method = RequestMethod.POST)
    @ResponseBody
    public String getRiskMorningPost(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        List<String> riskleve = ( ArrayList<String>) param.get("riskleve");
        List<String> companyName = ( ArrayList<String>) param.get("companyName");
        riskleve.remove("全选");
        companyName.remove("全选");
        System.out.println("companyName: " + companyName);
        List<String> riskleveInt = new ArrayList<>();
        for(String level :riskleve){
            if ("警示".equals(level)){
                 riskleveInt.add("2");
            }
            if ("重大".equals(level)){
                riskleveInt.add("1");
            }
            if ("一般".equals(level)){
                riskleveInt.add("0");
            }
        }
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getRiskMorningPost(hs,userName,riskleveInt,companyName,startDate,endDate,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getMorningNews", method = RequestMethod.POST)
    @ResponseBody
    public String getMorningNews(@RequestBody Map<String,Object> param) throws JsonProcessingException{
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String userName = (String) param.get("userName");
        List<String> riskleve = ( ArrayList<String>) param.get("riskleve");
        List<String> companyName = ( ArrayList<String>) param.get("companyName");
        List<String> newsEmotion = ( ArrayList<String>) param.get("newsEmotion");
        newsEmotion.remove("全选");
        companyName.remove("全选");
        riskleve.remove("全选");
        List<String> riskleveInt = new ArrayList<>();
        for(String level :riskleve){
            if ("警示".equals(level)){
                riskleveInt.add("2");
            }
            if ("重大".equals(level)){
                riskleveInt.add("1");
            }
            if ("一般".equals(level)){
                riskleveInt.add("0");
            }
        }
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getMorningNews(hs,userName,riskleveInt,companyName,newsEmotion,startDate,endDate,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }


    /**
     * 信保报告申请接口入口
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/common/ZXB/reportApply", method = RequestMethod.POST)
    @ResponseBody
    public String reportApply(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String clientNo = "";
        String reportCorpCountryCode = "";
        Integer userId = null;

        String userName = (String)param.get("userName");
        String reportBuyerNo = (String)param.get("reportBuyerNo");
        String countryCode = (String)param.get("reportCorpCountryCode");
        String reportCorpChnName = (String)param.get("reportCorpChnName");
        String reportCorpEngName = (String)param.get("reportCorpEngName");
        String reportCorpaddress = (String)param.get("reportCorpaddress");
        String creditNo = (String)param.get("creditNo");
        String reportCorptel = (String)param.get("reportCorptel");
        String reportCorpemail = (String)param.get("reportCorpemail");
        String speed = (String)param.get("speed");
        Integer isTranslation = null;

        try{
            isTranslation = CommonUtils.getIntegerValue(param.get("isTranslation"));

            userId = commonService.getUserId(userName);

            if(userId != null){
                ZhongXinBaoLog log = companyService.getCodeInfo(userId);
                clientNo = log.getClientNo();
            }

            List<NationCode> nationCodes = getNationCode();
            for(int i = 0; i < nationCodes.size(); i++){
                if(countryCode.equalsIgnoreCase(nationCodes.get(i).getNationCode())){
                    reportCorpCountryCode = countryCode;
                    break;
                }
            }
        }catch(Exception e){

        }

        if(clientNo == null || clientNo.isEmpty()){
            hs.put("code",500);
            hs.put("msg","人员所在的二级公司没有开通信保通业务，如需使用该功能请咨询公司管理员。");
            return objectMapper.writeValueAsString(hs);
        }else if((reportCorpChnName == null || reportCorpChnName.isEmpty()) && (reportCorpEngName == null || reportCorpEngName.isEmpty())){
            hs.put("code",500);
            hs.put("msg","操作失败: reportCorpChnName、reportCorpEngName 需选其一必填");
            return objectMapper.writeValueAsString(hs);
        }else if(isTranslation != 1 && isTranslation != 0) {
            hs.put("code", 500);
            hs.put("msg", "操作失败: isTranslation 字段必填，且值只能为1或0");
            return objectMapper.writeValueAsString(hs);
        }

        //无信保代码
        if(reportBuyerNo == null || reportBuyerNo.isEmpty()) {
            if(reportCorpCountryCode == null || reportCorpCountryCode.isEmpty()){
                hs.put("code", 500);
                hs.put("msg", "操作失败: reportCorpCountryCode 请输入有效国家(无信保代码时)");
                return objectMapper.writeValueAsString(hs);
            }else if(reportCorpaddress == null || reportCorpaddress.isEmpty()){
                hs.put("code", 500);
                hs.put("msg", "操作失败: reportCorpaddress 字段必填(无信保代码时)");
                return objectMapper.writeValueAsString(hs);
            }else if((reportCorpChnName != null && !reportCorpChnName.isEmpty()) && (creditNo == null || creditNo.isEmpty())){
                hs.put("code", 500);
                hs.put("msg", "操作失败: creditNo 字段必填(无信保代码时),英文名称企业可不填");
                return objectMapper.writeValueAsString(hs);
            }
        }

        hs.put("userId",userId);
        hs.put("clientNo",clientNo);
        hs.put("reportBuyerNo",reportBuyerNo);
        hs.put("reportCorpCountryCode",reportCorpCountryCode);
        hs.put("reportCorpChnName",reportCorpChnName);
        hs.put("reportCorpEngName",reportCorpEngName);
        hs.put("reportCorpaddress",reportCorpaddress);
        hs.put("creditNo",creditNo);
        hs.put("isTranslation",isTranslation);
        hs.put("reportCorptel",reportCorptel);
        hs.put("reportCorpemail",reportCorpemail);
        hs.put("speed",speed);
        return getZhongXinbaoApply(hs);
    }

    public List<NationCode> getNationCode() throws JsonProcessingException {
        List<NationCode> nationCode = commonService.getAreaInfo();
        return nationCode;
    }

    public String getZhongXinbaoApply( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        User user = userService.getUserById(userId);
        //jina
        List<String> reportApplyUserNameList = commonService.getReportApplyUserNameList(user.getUsername());
        for(String userName:reportApplyUserNameList){
            commonService.insertReportApply(userName);
        }

        ArrayOfEntrustInput arrayOfEntrustInput = new ArrayOfEntrustInput();
        EntrustInput entrustInput = new EntrustInput();
        String corpSerialNo = buildEntrustInput(arrayOfEntrustInput, entrustInput,param);
        companyService.saveZhongXinBaoLog(user,entrustInput);




        return getZhongXinbaoApprove(1,1,corpSerialNo);

//        hs.put("msg","申请成功，请耐心等待审核");
//        hs.put("code","0");
//        ObjectMapper objectMapper=new ObjectMapper();
//        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 信保报告申请接口入口，汇集参数
     * @param arrayOfEntrustInput
     * @param entrustInput
     * @param param
     * @return
     */
    private String buildEntrustInput(ArrayOfEntrustInput arrayOfEntrustInput, EntrustInput entrustInput, Map<String, Object> param) {

        String reportbuyerNo = (String) param.get("reportBuyerNo");
        String clientNo = (String) param.get("clientNo");
        String  corpSerialNo = CommonUtils.getRandomCode();
        String creditno = (String) param.get("creditNo");
        entrustInput.setCorpSerialNo(getJAXBElement("corpSerialNo",corpSerialNo));
        entrustInput.setClientNo(getJAXBElement("clientNo",clientNo));
        entrustInput.setCreditno(getJAXBElement("creditno",creditno));
        String reportCorpChnName = (String) param.get("reportCorpChnName");
        String reportCorpEngName = (String) param.get("reportCorpEngName");
        String reportCorpemail = (String) param.get("reportCorpemail");
        String reportCorptel = (String) param.get("reportCorptel");
        String speed = (String) param.get("speed");
        if(StringUtils.isBlank(reportbuyerNo)){
            String reportCorpCountryCode = (String) param.get("reportCorpCountryCode");
            String reportCorpaddress = (String) param.get("reportCorpaddress");
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(param.get("isTranslation"))) ;
            entrustInput.setReportCorpCountryCode(getJAXBElement("reportCorpCountryCode",reportCorpCountryCode));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
            entrustInput.setReportCorpaddress(getJAXBElement("reportCorpaddress",reportCorpaddress));
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setClientNo(getJAXBElement("clientno",clientNo));
            entrustInput.setCreditno(getJAXBElement("creditno",creditno));
            entrustInput.setReportCorptel(getJAXBElement("reportCorptel",reportCorptel));
            entrustInput.setReportCorpemail(getJAXBElement("reportCorpemail",reportCorpemail));
            entrustInput.setSpeed(getJAXBElement("speed",speed));
        }else{
            entrustInput.setReportbuyerNo(getJAXBElement("reportbuyerNo",reportbuyerNo));
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(param.get("isTranslation"))) ;
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setCreditno(getJAXBElement("creditno",creditno));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
            entrustInput.setSpeed(getJAXBElement("speed",speed));
        }
        arrayOfEntrustInput.getEntrustInput().add(entrustInput);

        return corpSerialNo;
    }

    private JAXBElement<String> getJAXBElement(String filedName, String fieldValue) {
        QName corpSerialNo_NAME = new QName("http://po.edi.exchange.sinosure.com", filedName);
        return new JAXBElement<String>(corpSerialNo_NAME, String.class, fieldValue);
    }

    /**
     * 信保报告审核接口入口
     * @param approve
     * @param approveCode
     * @param corpSerialNo
     * @return
     * @throws JsonProcessingException
     */
    public String getZhongXinbaoApprove(Integer approve,Integer approveCode,String  corpSerialNo) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
//        Integer approve = CommonUtils.getIntegerValue(param.get("approve")) ;
        User user = userService.getUserById(approve);
//        Integer approveCode = CommonUtils.getIntegerValue(param.get("approveCode")) ;
//        if(approveCode == 999){
////            String  corpSerialNo = (String) param.get("corpSerialNo");
//            ZhongXinBaoLog log = companyService.findByCorpSerialNo(corpSerialNo);
//            log.setApproveCode(String.valueOf(approveCode));
//            log.setApproveby(user.getUsername());
//            log.setApproveDate(new Timestamp(System.currentTimeMillis()));
//            companyService.saveZhongXinBaoLog(log);
//            String updateBy = log.getUpdateBy();
//            String approveBy = log.getApproveby();
//            companyService.insertOAMsg(updateBy, user.getName());
//            hs.put("code",0);
//            hs.put("msg","操作成功");
//            ObjectMapper objectMapper=new ObjectMapper();
//            return objectMapper.writeValueAsString(hs);
//        }
        ArrayOfEntrustInput arrayOfEntrustInput = new ArrayOfEntrustInput();
        EntrustInput entrustInput = new EntrustInput();
        buildEntrustApprove(arrayOfEntrustInput, entrustInput,corpSerialNo);

//        try{
//            companyService.saveReportPushInfo(user,entrustInput);
//        }catch(Exception e){
//            e.printStackTrace();
//        }

        URL wsdlURL = SolEdiProxyWebService.WSDL_LOCATION;
        QName SERVICE_NAME = new QName("http://service.edi.exchange.sinosure.com", "SolEdiProxyWebService");
        SolEdiProxyWebService ss = new SolEdiProxyWebService(wsdlURL,SERVICE_NAME);
        SolEdiProxyWebServicePortType port = ss.getSolEdiProxyWebServiceHttpPort();
        try {
            ArrayOfEdiFeedback arrayOfEdiFeedback = port.doEdiCreditReportInput(arrayOfEntrustInput);
            List<EdiFeedback> ediFeedback = arrayOfEdiFeedback.getEdiFeedback();
            for(EdiFeedback e:ediFeedback){
                String returnMsg = e.getReturnMsg().getValue();
                String returnCode = e.getReturnCode().getValue();
                //统一成功标识 20210616

                if(returnCode.contains("1")){
                    returnCode = returnCode.replace('1','0');
                }

                hs.put("msg",returnMsg);
                hs.put("code",returnCode);
                companyService.approveZhongXinBaoLog(user,e,entrustInput);
                companyService.saveReportPushInfo(user,entrustInput);
            }
        } catch (EdiException_Exception e) {
            e.printStackTrace();
            log.info(e.toString());
            hs.put("code",-1);
            hs.put("msg","调用中信保接口失败，请确认参数");
        }

        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);

    }

    /**
     * 信保报告审核接口入口，汇集参数
     * @param arrayOfEntrustInput
     * @param entrustInput
     * @param corpSerialNo
     */
    private void buildEntrustApprove(ArrayOfEntrustInput arrayOfEntrustInput, EntrustInput entrustInput, String  corpSerialNo) {

//        String  corpSerialNo = (String) param.get("corpSerialNo");
        ZhongXinBaoLog log = companyService.findByCorpSerialNo(corpSerialNo);
        entrustInput.setCorpSerialNo(getJAXBElement("corpSerialNo",corpSerialNo));
        entrustInput.setClientNo(getJAXBElement("clientNo",log.getClientNo()));
        String reportbuyerNo = log.getReportbuyerNo();
        String clientNo = log.getClientNo();
        String speed = log.getSpeed();
        if(StringUtils.isBlank(reportbuyerNo)){
            String reportCorpCountryCode = log.getReportCorpCountryCode();
            String reportCorpChnName = log.getReportCorpChnName();
            String reportCorpEngName = log.getReportCorpEngName();
            String reportCorpaddress = log.getReportCorpaddress();
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(log.getIstranslation())) ;
            String reportCorptel = log.getReportCorptel();
            String reportCorpemail = log.getReportCorpemail();

            entrustInput.setReportCorpCountryCode(getJAXBElement("reportCorpCountryCode",reportCorpCountryCode));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
            entrustInput.setReportCorpaddress(getJAXBElement("reportCorpaddress",reportCorpaddress));
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
//            entrustInput.setClientNo(getJAXBElement("clientno",clientNo));
            entrustInput.setReportCorptel(getJAXBElement("reportCorptel",reportCorptel));
            entrustInput.setReportCorpemail(getJAXBElement("reportCorpemail",reportCorpemail));
            entrustInput.setSpeed(getJAXBElement("speed",speed));
        }else{
            entrustInput.setReportbuyerNo(getJAXBElement("reportbuyerNo",reportbuyerNo));
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(log.getIstranslation())) ;
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setSpeed(getJAXBElement("speed",speed));
        }
        arrayOfEntrustInput.getEntrustInput().add(entrustInput);
    }

    @RequestMapping(value = "/common/ZXB/getPDFUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getJsongetPDFUrl(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String reportBuyerNo = (String)param.get("reportBuyerNo");
        String fileName = commonService.getXBPDFFileName(reportBuyerNo);
        String downloadUrl = interfaceIssuedSchema + "://"+ interfaceIssuedIp + ":" + interfaceIssuedPort + "/common/ZXB/downloadPDF/" + fileName;
        String updateTime = commonService.getXBPDFFileUpdateTime(reportBuyerNo);
        if(fileName != null && !fileName.isEmpty())
        {
            hs.put("result",downloadUrl);
            hs.put("UPDATETIME",updateTime);
            hs.put("code",0);
            hs.put("msg","");
        }else
        {
            hs.put("code",500);
            hs.put("msg","reportBuyerNo: " + reportBuyerNo + ", There is no PDF.");
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/ZXB/downloadPDF/{pdfName}", method = RequestMethod.GET)
    @ResponseBody
    public String getJsondownloadPDF(@PathVariable("pdfName") String pdfName, HttpServletResponse response) throws IOException {

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String fileNameUat = "/home/ftpuser/" + pdfName;
        File file = new File(fileNameUat);
        try {
            //加载文件
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileNameUat.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            IOUtils.copy(is,outputStream,1024);
            hs.put("code","0");
        } catch (Exception e) {
            e.printStackTrace();
            hs.put("code","1");
            hs.put("msg","文件下载出错");
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/ZCX/downloadPDF/{pdfName}", method = RequestMethod.GET)
    @ResponseBody
    public String getJsondownloadZcxPDF(@PathVariable("pdfName") String pdfName, HttpServletResponse response) throws IOException {

        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String fileNameUat = "/home/ftpuser/zcxpdf" + pdfName;
        File file = new File(fileNameUat);
        try {
            //加载文件
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileNameUat.getBytes("UTF-8"), "ISO-8859-1"));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            IOUtils.copy(is,outputStream,1024);
            hs.put("code","0");
        } catch (Exception e) {
            e.printStackTrace();
            hs.put("code","1");
            hs.put("msg","文件下载出错");
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/ZXB/getEdiMessages", method = RequestMethod.POST)
    @ResponseBody
    public String getEdiMessages( @RequestBody Map<String,Object> param) throws JsonProcessingException, ServiceException {
        HashMap<String,Object> hs=new HashMap<>();
        JSONObject json = new JSONObject(param);
        String paramStr = json.toJSONString();
        String url= SolEdiProxyWebService.Edi3Server;

        String insureResponsePost = insureResponsePost(url,paramStr);

        if(insureResponsePost.startsWith("Error")){
            hs.put("code",-1);
            hs.put("msg",insureResponsePost);
        }else{
            JSONObject jsonResult = (JSONObject)JSONObject.parse(insureResponsePost);
            System.out.println(insureResponsePost);

            hs.put("datas",JSONObject.parse(jsonResult.get("datas").toString()));
            hs.put("imethod",jsonResult.get("imethod"));
            hs.put("errormsg",jsonResult.get("errormsg"));
        }

        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/ZXB/doEdiMessages", method = RequestMethod.POST)
    @ResponseBody
    public String doEdiMessages( @RequestBody Map<String,Object> param) throws JsonProcessingException, ServiceException {
        HashMap<String,Object> hs=new HashMap<>();
        JSONObject json = new JSONObject(param);
        String paramStr = json.toJSONString();
        String url= SolEdiProxyWebService.Edi3Server;

        String insureResponsePost = insureResponsePost(url,paramStr);

        if(insureResponsePost.startsWith("Error")){
            hs.put("code",-1);
            hs.put("msg",insureResponsePost);
        }else{
            JSONObject jsonResult = (JSONObject)JSONObject.parse(insureResponsePost);
            System.out.println(insureResponsePost);

            hs.put("datas",JSONObject.parse(jsonResult.get("datas").toString()));
            hs.put("imethod",jsonResult.get("imethod"));
            hs.put("errormsg",jsonResult.get("errormsg"));
        }

        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    public static String insureResponsePost(String url, String param) {
        PrintWriter out = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = "";
        HttpURLConnection conn = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod( "POST");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(300000);
            conn.setRequestProperty("Charset", "utf-8");
            conn.setRequestProperty( "Content-Type", "application/json");
            conn.setRequestProperty( "Content-Encoding", "utf-8");
            conn.setDoOutput( true);
            conn.setDoInput( true);
            conn.setUseCaches( false);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            is = conn.getInputStream();
            BufferedReader bufferedReader = null;
            if (is != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }

            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return ("Error! httpClient访问异常：" + e.getMessage());
        }

        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (br != null) {
                    br.close();
                }
                if (conn!= null) {
                    conn.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return ("Error! httpClient关闭异常：" + ex.getMessage());
            }
        }
        return result;
    }

    @RequestMapping(value = "/common/savePAFCUser", method = RequestMethod.POST)
    @ResponseBody
    public String savePAFCUser(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Integer companyCode = CommonUtils.getIntegerValue(param.get("companyCode"));
        String companyName = (String)param.get("companyName");
        String userName = (String)param.get("userName");
        String email = (String)param.get("email");

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/addPAFCUser", method = RequestMethod.POST)
    @ResponseBody
    public String addPAFCUser(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        String companyCode = (String)param.get("companyCode");
        String userName = (String)param.get("userName");
        String email = (String)param.get("email");

        PAFCUser pafcUser = new PAFCUser();
        pafcUser.setUserName(userName);
        pafcUser.setEmail(email);
        pafcUser.setCompanyCode(companyCode);

        Integer flag = commonService.addPAFCUserInfo(pafcUser);

        if(flag ==1){
            hs.put("code",0);
            hs.put("msg","新增收件人成功");
        }else {
            hs.put("code",1);
            hs.put("msg","新增收件人失败");
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/updatePAFCUser", method = RequestMethod.POST)
    @ResponseBody
    public String updatePAFCUser(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Integer id = CommonUtils.getIntegerValue(param.get("id"));
        String userName = (String)param.get("userName");
        String email = (String)param.get("email");

        PAFCUser pafcUser = new PAFCUser();
        pafcUser.setUserName(userName);
        pafcUser.setEmail(email);
        pafcUser.setId(id);

        Integer flag = commonService.updatePAFCUserInfoById(pafcUser);

        if(flag ==1){
            hs.put("code",0);
            hs.put("msg","设置收件人成功");
        }else {
            hs.put("code",1);
            hs.put("msg","设置收件人失败");
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/deletePAFCUser", method = RequestMethod.POST)
    @ResponseBody
    public String deletePAFCUser(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        Integer id = CommonUtils.getIntegerValue(param.get("id"));

        Integer flag = commonService.deletePAFCUserInfoById(id);

        if(flag ==1){
            hs.put("code",0);
            hs.put("msg","删除收件人成功");
        }else {
            hs.put("code",1);
            hs.put("msg","删除收件人失败");
        }

        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/savePAFCVersion", method = RequestMethod.POST)
    @ResponseBody
    public String savePAFCVersion(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        Integer code = 0;
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        String supplierName = (String)param.get("supplierName");
        String versionNo = (String)param.get("versionNo");
        Integer interfaceTotalPoints = CommonUtils.getIntegerValue(param.get("interfaceTotalPoints"));
        Integer attentionTotalPoints = CommonUtils.getIntegerValue(param.get("attentionTotalPoints"));
        String startDate = (String)param.get("startDate");
        String endDate = (String)param.get("endDate");

        PAFCVersion  pafcVersion = commonService.getPAFCVersion(versionNo);
        pafcVersion.setStartDate(DateUtil.transform2day(pafcVersion.getStartDate()));
        pafcVersion.setEndDate(DateUtil.transform2day(pafcVersion.getEndDate()));

        if(interfaceTotalPoints == null){
            pafcVersion.setInterfaceTotalPoints(0);
        }else{
            pafcVersion.setInterfaceTotalPoints(interfaceTotalPoints);
        }

        if(attentionTotalPoints == null){
            pafcVersion.setAttentionTotalPoints(0);
        }else{
            pafcVersion.setAttentionTotalPoints(attentionTotalPoints);
        }

        if(StringUtils.isNoneBlank(startDate)){
            pafcVersion.setStartDate(startDate);
        }

        //若更新结束时间，自动更新版本信息
        if(pafcVersion.getEndDate() != null && pafcVersion.getEndDate().equals(endDate)){
            code = commonService.updatePAFCVersion(pafcVersion);
        }else{
            String versionNoNew = "";
            if(StringUtils.isNoneBlank(endDate)){
                pafcVersion.setEndDate(endDate);
            }
            if(supplierName.equals("天眼查")){
                versionNoNew = "tyc" + pafcVersion.getEndDate().replaceAll("-","");
            }else{
                versionNoNew = "zcx" + pafcVersion.getEndDate().replaceAll("-","");
            }
            pafcVersion.setVersionNo(versionNoNew);
            code = commonService.insertPAFCVersion(pafcVersion);
            List<PAFCCompany> pafcCompanyList = new ArrayList<>();
            pafcCompanyList = commonService.getRecipientInfoList();
            PAFCPoints pafcPoints = new PAFCPoints();
            for(PAFCCompany pafcCompany : pafcCompanyList){
                pafcPoints = new PAFCPoints();
                pafcPoints.setCompanyCode(pafcCompany.getCompanyCode());
                pafcPoints.setVersionNo(pafcVersion.getVersionNo());
                commonService.insertPAFCPoints(pafcPoints);
            }
        }

        if(code > 0){
            hs.put("code",0);
            hs.put("msg","版本信息更新成功");
        }else {
            hs.put("code",1);
            hs.put("msg","版本信息更新失败");
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/savePAFCPoints", method = RequestMethod.POST)
    @ResponseBody
    public String savePAFCPoints(@RequestBody Map<String,Object> param) throws JsonProcessingException {

        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Integer interfaceDistributePoints = CommonUtils.getIntegerValue(param.get("interfaceDistributePoints"));
        Integer attentionDistributePoints = CommonUtils.getIntegerValue(param.get("attentionDistributePoints"));
        String versionNo = (String)param.get("versionNo");
        String companyCode = (String)param.get("companyCode");
        PAFCPoints pafcPoints = new PAFCPoints();
        pafcPoints.setInterfaceDistributePoints(interfaceDistributePoints == null ? 0 : interfaceDistributePoints);
        pafcPoints.setAttentionDistributePoints(attentionDistributePoints == null ? 0 : attentionDistributePoints);
        pafcPoints.setVersionNo(versionNo);
        pafcPoints.setCompanyCode(companyCode);
        Integer code = commonService.updatePAFCPoints(pafcPoints);

        if(code > 0){
            hs.put("code",0);
            hs.put("msg","点数填报信息更新成功");
        }else {
            hs.put("code",1);
            hs.put("msg","点数填报信息更新失败");
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPAFCVersionList",method = RequestMethod.POST)
    @ResponseBody
    public String getPAFCVersionList() throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<PAFCVersion>  pafcVersionList = new ArrayList<>();
        List<PAFCVersion>  versionPointsList = new ArrayList<>();
        PAFCVersion pafcVersionPROD;
        PAFCVersion pafcVersionDEV;
        pafcVersionList = commonService.getPAFCVersionList();

        for(PAFCVersion pafcVersion : pafcVersionList){
            pafcVersionPROD = new PAFCVersion();
            pafcVersionDEV = new PAFCVersion();
            pafcVersion.setStartDate(pafcVersion.getStartDate() == null ? "" : DateUtil.transform2day(pafcVersion.getStartDate()));
            pafcVersion.setEndDate(pafcVersion.getEndDate() == null ? "" : DateUtil.transform2day(pafcVersion.getEndDate()));
            if(pafcVersion.getSupplierName().equals("天眼查")){
                pafcVersionPROD = commonService.getVersionPointsTYC(pafcVersion);
                pafcVersionDEV = InputPointsService.getVersionPointsTYC(pafcVersion);

            }else if(pafcVersion.getSupplierName().equals("中诚信")){
                pafcVersionPROD = commonService.getVersionPointsZCX(pafcVersion);
                pafcVersionDEV = InputPointsService.getVersionPointsZCX(pafcVersion);
            }

            if(pafcVersionPROD.getInterfaceUsedTotalPoints() == null) pafcVersionPROD.setInterfaceUsedTotalPoints(0);
            if(pafcVersionPROD.getAttentionUsedTotalPoints() == null) pafcVersionPROD.setAttentionUsedTotalPoints(0);

            //正式+测试的数据总和
            int interfaceTotal = pafcVersionPROD.getInterfaceUsedTotalPoints() + pafcVersionDEV.getInterfaceUsedTotalPoints();
            int AttentionTotal = pafcVersionPROD.getAttentionUsedTotalPoints() + pafcVersionDEV.getAttentionUsedTotalPoints();
            //按供应商维度统计接口、关注使用总数
            pafcVersion.setInterfaceUsedTotalPoints(interfaceTotal);
            pafcVersion.setAttentionUsedTotalPoints(AttentionTotal);
//            pafcVersion.setInterfaceUsedTotalPoints(pafcVersionPROD.getInterfaceUsedTotalPoints());
//            pafcVersion.setAttentionUsedTotalPoints(pafcVersionPROD.getAttentionUsedTotalPoints());
        }

        hs.put("code",0);
        hs.put("pafcVersionList",pafcVersionList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getPAFCPointsList",method = RequestMethod.POST)
    @ResponseBody
    public String getPAFCPointsList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String versionNo = "";
        versionNo = (String)param.get("versionNo");

        List<PAFCPoints>  pafcPointsList = new ArrayList<>();
        List<PAFCCompany>  pafcCompanyList = new ArrayList<>();
        PAFCVersion  pafcVersion = new PAFCVersion();
        List<PAFCPoints>  companyPointsPRODList = new ArrayList<>();
        List<PAFCPoints>  companyPointsDEVList = new ArrayList<>();

        if(versionNo.isEmpty()){
            hs.put("code",0);
            hs.put("pafcPointsList",pafcPointsList);
            return objectMapper.writeValueAsString(hs);
        }

        pafcVersion = commonService.getPAFCVersion(versionNo);
        pafcVersion.setStartDate(pafcVersion.getStartDate() == null ? "" : DateUtil.transform2day(pafcVersion.getStartDate()));
        pafcVersion.setEndDate(pafcVersion.getEndDate() == null ? "" : DateUtil.transform2day(pafcVersion.getEndDate()));
        pafcCompanyList = commonService.getRecipientInfoList();
        pafcPointsList = commonService.getPAFCPointsList(versionNo);
        companyPointsPRODList = commonService.getCompanyPoints(pafcVersion);
        companyPointsDEVList = InputPointsService.getCompanyPoints(pafcVersion);

        for(PAFCPoints pafcPoints : pafcPointsList){
            for(PAFCCompany pafcCompany : pafcCompanyList){
                if(pafcPoints.getCompanyCode().equals(pafcCompany.getCompanyCode())){
                    pafcPoints.setUserName(pafcCompany.getUserNameStr());
                    pafcPoints.setEmail(pafcCompany.getEmailStr());
                    pafcPoints.setCompanyName(pafcCompany.getCompanyName());
                }
            }
        }
        Integer attentionTemp = 0;
        Integer interfaceTemp = 0;
        //按二级公司维度统计接口、关注使用数 --正式数据
        for(PAFCPoints pafcPoints : pafcPointsList){
            for(PAFCPoints companyPoints : companyPointsPRODList){
                if(pafcPoints.getCompanyName().equals(companyPoints.getCompanyName())){
                    pafcPoints.setInterfaceUsedPoints(companyPoints.getInterfaceUsedPoints());
                    pafcPoints.setAttentionUsedPoints(companyPoints.getAttentionUsedPoints());
                }
            }
            //加上测试环境数据
            for(PAFCPoints companyPoints : companyPointsDEVList){
                if(pafcPoints.getCompanyName().equals(companyPoints.getCompanyName())){
                    pafcPoints.setInterfaceUsedPoints(pafcPoints.getInterfaceUsedPoints() + companyPoints.getInterfaceUsedPoints());
                    pafcPoints.setAttentionUsedPoints(pafcPoints.getAttentionUsedPoints() + companyPoints.getAttentionUsedPoints());
                }

                if(companyPoints.getCompanyName().equals("浙江中大技术进出口集团有限公司")){
                    interfaceTemp = companyPoints.getInterfaceUsedPoints();
                    attentionTemp = companyPoints.getAttentionUsedPoints();
                }
            }
        }

        //正式、测试组织架构不同，特殊处理
        for(PAFCPoints pafcPoints : pafcPointsList){
            if(pafcPoints.getCompanyName().equals("浙江省国贸集团资产经营有限公司")){
                pafcPoints.setAttentionUsedPoints(pafcPoints.getAttentionUsedPoints() + attentionTemp);
                pafcPoints.setInterfaceUsedPoints(pafcPoints.getInterfaceUsedPoints() + interfaceTemp);
            }
        }

        hs.put("code",0);
        hs.put("pafcPointsList",pafcPointsList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getRecipientInfoList",method = RequestMethod.POST)
    @ResponseBody
    public String getRecipientInfoList() throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<PAFCCompany>  pafcCompanyList = new ArrayList<>();
        pafcCompanyList = commonService.getRecipientInfoList();

        hs.put("code",0);
        hs.put("pafcCompanyList",pafcCompanyList);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/getPAFCUserInfoList",method = RequestMethod.POST)
    @ResponseBody
    public String getPAFCUserInfoList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String companyCode = "";
        companyCode = param.get("userCompanyCode").toString();

        List<PAFCUser> pafcUserList = commonService.getPAFCUserListByCompanyCode(companyCode);

        hs.put("code",0);
        hs.put("pafcUserList",pafcUserList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/syncODSCompany2PAFCCompany",method = RequestMethod.POST)
    @ResponseBody
    public void syncODSCompany2PAFCCompany(){
        List<CompanyLevel> companyLevelList = new ArrayList<>();
        List<PAFCCompany> pafcCompanyList = new ArrayList<>();
        List<PAFCCompany> updateList = new ArrayList<>();
        List<PAFCCompany> deleteList = new ArrayList<>();
        List<PAFCCompany> addList = new ArrayList<>();

        companyLevelList = commonService.getLV2Company();//ODS 数据
        pafcCompanyList = commonService.getPAFCCompanyList();//填报数据
        boolean needAdd;
        boolean needDelete;
        PAFCCompany pafcCompanyTemp;
        for(CompanyLevel companyLevel : companyLevelList){
            needAdd = true;
            for(PAFCCompany pafcCompany : pafcCompanyList){
                if(companyLevel.getCode().equals(pafcCompany.getCompanyCode())){
                    needAdd = false;
                    if(!companyLevel.getName().equals(pafcCompany.getCompanyName())){
                        pafcCompanyTemp = new PAFCCompany();
                        pafcCompanyTemp.setCompanyCode(companyLevel.getCode());
                        pafcCompanyTemp.setCompanyName(companyLevel.getName());
                        updateList.add(pafcCompanyTemp);
                        break;
                    }
                }
            }

            if(needAdd){
                pafcCompanyTemp = new PAFCCompany();
                pafcCompanyTemp.setCompanyCode(companyLevel.getCode());
                pafcCompanyTemp.setCompanyName(companyLevel.getName());
                addList.add(pafcCompanyTemp);
            }
        }

        for(PAFCCompany pafcCompany : pafcCompanyList){
            needDelete = true;
            for(CompanyLevel companyLevel : companyLevelList){
                if(companyLevel.getCode().equals(pafcCompany.getCompanyCode())){
                    needDelete = false;
                    break;
                }
            }
            if (needDelete) deleteList.add(pafcCompany);
        }

        for(PAFCCompany pafcCompany : updateList){
            commonService.updatePAFCCompany(pafcCompany);
        }

        for(PAFCCompany pafcCompany : deleteList){
            commonService.deletePAFCCompany(pafcCompany);
        }

        if(addList.size() > 0){
            List<PAFCVersion> pafcVersionList = new ArrayList<>();
            List<PAFCPoints> pafcPointsList;
            pafcVersionList = commonService.getPAFCVersionList();

            for(PAFCCompany pafcCompany : addList){
                //二级公司新增需同步到指定版本中
                commonService.insertPAFCCompany(pafcCompany);
                for(PAFCVersion pafcVersion : pafcVersionList){
                    boolean needAdd2PAFCPoints = true;
                    pafcPointsList = new ArrayList<>();
                    pafcPointsList = commonService.getPAFCPointsList(pafcVersion.getVersionNo());

                    for(PAFCPoints pafcPoints : pafcPointsList){
                        //已有则不加
                        if(pafcCompany.getCompanyCode().equals(pafcPoints.getCompanyCode())){
                            needAdd2PAFCPoints = false;
                            break;
                        }
                    }

                    if(needAdd2PAFCPoints){
                        PAFCPoints pafcPoints = new PAFCPoints();
                        pafcPoints.setCompanyCode(pafcCompany.getCompanyCode());
                        pafcPoints.setVersionNo(pafcVersion.getVersionNo());
                        commonService.insertPAFCPoints(pafcPoints);
                    }

                }
            }
        }
    }

    @RequestMapping(value = "/common/getTokenList",method = RequestMethod.POST)
    @ResponseBody
    public String getTokenList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String companyName = (String)param.get("companyName");
        String uri = (String)param.get("uri");
        String interfaceName = (String)param.get("interfaceName");
        Integer status = CommonUtils.getIntegerValue(param.get("status"));
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));

        List<OpenAPI> openAPIList = commonService.getTokenList(companyName, uri, interfaceName, status, pageIndex,pageSize);
        Integer totalRecords = commonService.getTokenListTotalCount(companyName, uri, interfaceName, status);

        hs.put("code",0);
        hs.put("openAPIList",openAPIList);
        hs.put("totalRecords",totalRecords);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/tokenValidate",method = RequestMethod.POST)
    @ResponseBody
    public String tokenValidate(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String tokenId = (String)param.get("tokenId");
        String companyName = (String)param.get("companyName");
        String uri = (String)param.get("uri");
        Integer openId = CommonUtils.getIntegerValue(param.get("openId"));

        Integer code = commonService.tokenValidate(tokenId, companyName, uri, openId);

        hs.put("code",code);

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/tokenPrefixValidate",method = RequestMethod.POST)
    @ResponseBody
    public String tokenPrefixValidate(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String tokenIdPrefix = (String)param.get("tokenIdPrefix");
        Integer id = CommonUtils.getIntegerValue(param.get("id"));

        Integer code = commonService.tokenPrefixValidate(tokenIdPrefix,id);


        hs.put("code",code);

        return objectMapper.writeValueAsString(hs);
    }


//    @RequestMapping(value = "/common/getCompanyWithOpenAPI",method = RequestMethod.POST)
//    @ResponseBody
//    public String getCompanyWithOpenAPI() throws JsonProcessingException {
//        HashMap<String,Object> hs = new HashMap<>();
//        ObjectMapper objectMapper=new ObjectMapper();
//
//        List<OpenAPIRelation> openAPIRelationList = commonService.getOpenAPIRelationList();
//
//        hs.put("code",0);
//        hs.put("openAPIRelationList",openAPIRelationList);
//        return objectMapper.writeValueAsString(hs);
//    }

    @RequestMapping(value = "/common/updateOpenAPIStatus",method = RequestMethod.POST)
    @ResponseBody
    public String updateOpenAPIStatus(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        Integer openId = CommonUtils.getIntegerValue(param.get("openId"));
        Integer status = CommonUtils.getIntegerValue(param.get("status"));
        String tokenId = (String)param.get("tokenId");

        if(status == 1){
            if(tokenId.startsWith("0X")){
                tokenId = tokenId.substring(2);
            }
        }else if(status == 0){
            if(!tokenId.startsWith("0X")){
                tokenId = "0X" + tokenId;
            }
        }

        Integer isUpdate = commonService.updateOpenAPIStatus(tokenId, openId);

        hs.put("code",0);
        hs.put("isUpdate",isUpdate);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getTokenRelation",method = RequestMethod.POST)
    @ResponseBody
    public String getTokenRelation() throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        List<OpenAPIRelation> openAPIRelationList = commonService.getOpenAPIRelationList();

        hs.put("code",0);
        hs.put("openAPIRelationList",openAPIRelationList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/saveTokenRelation",method = RequestMethod.POST)
    @ResponseBody
    public String saveTokenRelation(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String companyName = param.get("companyName") == null ? "" : (String)param.get("companyName");
        String companyNameShort = param.get("companyNameShort") == null ? "" : (String)param.get("companyNameShort");
        String tokenIdPrefix = param.get("tokenIdPrefix") == null ? "" : (String)param.get("tokenIdPrefix");
        String type = param.get("type") == null ? "" : (String)param.get("type");
        Integer id = CommonUtils.getIntegerValue(param.get("id"));
        Integer isSaved  = commonService.saveTokenRelation(companyName, companyNameShort, tokenIdPrefix, type, id);

        if(isSaved == 1){
            hs.put("code",0);
            hs.put("msg","数据保存成功！");
        }else{
            hs.put("code",1);
        }

        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/deleteTokenRelation",method = RequestMethod.POST)
    @ResponseBody
    public String deleteTokenRelation(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        Integer id = CommonUtils.getIntegerValue(param.get("id"));
        String tokenId = (String)param.get("tokenIdPrefix");

        Integer isDelete = commonService.deleteTokenRelation(id);

        commonService.deleteCreditOpenApi(tokenId);


        if(isDelete == 1){
            hs.put("code",0);
            hs.put("msg","数据删除成功！");
        }else{
            hs.put("code",1);
        }
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/saveToken",method = RequestMethod.POST)
    @ResponseBody
    public String saveToken(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        Integer openId = CommonUtils.getIntegerValue(param.get("openId"));
        String tokenId = param.get("tokenId") == null ? "" : (String)param.get("tokenId");
        String uri = param.get("uri") == null ? "" : (String)param.get("uri");
        String interfaceName = param.get("interfaceName") == null ? "" : (String)param.get("interfaceName");
        String type = param.get("type") == null ? "" : (String)param.get("type");
        String remark = param.get("remark") == null ? "" : (String)param.get("remark");

        Integer isSaved  = commonService.saveToken(openId, tokenId, uri, interfaceName, type, remark);

        if(isSaved == 1){
            hs.put("code",0);
            hs.put("msg","数据保存成功！");
        }else{
            hs.put("code",1);
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getInterfaceLimitList",method = RequestMethod.POST)
    @ResponseBody
    public String getInterfaceLimitList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String companyName = (String)param.get("companyName");
        String interfaceName = (String)param.get("interfaceName");
        String jsonFlag = (String)param.get("jsonFlag");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));

        List<InterfaceUsedLimit> interfaceUsedLimit = commonService.getInterfaceLimitList(companyName,interfaceName, jsonFlag, pageIndex,pageSize);
        Integer totalRecords = commonService.getInterfaceLimitListTotalCount(companyName,interfaceName, jsonFlag);

        hs.put("code",0);
        hs.put("interfaceUsedLimit",interfaceUsedLimit);
        hs.put("totalRecords",totalRecords);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/saveInterfaceLimit",method = RequestMethod.POST)
    @ResponseBody
    public String saveInterfaceLimit(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        Integer limitNumber = param.get("limitNumber") == null ? 0 : CommonUtils.getIntegerValue(param.get("limitNumber"));
        Integer id = CommonUtils.getIntegerValue(param.get("id"));
        String jsonFlag = param.get("jsonFlag") == null ? "" : (String)param.get("jsonFlag");
        String companyName = param.get("companyName") == null ? "" : (String)param.get("companyName");
        String interfaceName = param.get("interfaceName") == null ? "" : (String)param.get("interfaceName");
        String editType = param.get("editType") == null ? "" : (String)param.get("editType");

        Integer isSaved  = commonService.saveInterfaceLimit(editType, jsonFlag, companyName, limitNumber, interfaceName, id);

        if(isSaved == 1){
            hs.put("code",0);
            hs.put("msg","数据保存成功！");
        }else{
            hs.put("code",1);
        }
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/deleteInterfaceLimit",method = RequestMethod.POST)
    @ResponseBody
    public String deleteInterfaceLimit(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        Integer id = CommonUtils.getIntegerValue(param.get("id"));

        Integer isSaved  = commonService.deleteInterfaceLimit(id);

        if(isSaved == 1){
            hs.put("code",0);
            hs.put("msg","数据删除成功！");
        }else{
            hs.put("code",1);
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getClientNoMaintainList",method = RequestMethod.POST)
    @ResponseBody
    public String getClientNoMaintainList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();

        String companyName = (String)param.get("companyName");
        String dataSource = (String)param.get("dataSource");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));

        List<ClientNoMaintain> clientNoMaintainList = commonService.getClientNoMaintainList(companyName,dataSource, pageIndex,pageSize);
        Integer totalRecords = commonService.getClientNoMaintainListTotalCount(companyName,dataSource);

        hs.put("code",0);
        hs.put("interfaceUsedLimit",clientNoMaintainList);
        hs.put("totalRecords",totalRecords);
        return objectMapper.writeValueAsString(hs);
    }

}
