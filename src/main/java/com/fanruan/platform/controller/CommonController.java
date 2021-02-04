package com.fanruan.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.CompanyDao;
import com.fanruan.platform.dao.NationCodeDao;
import com.fanruan.platform.etl.RiskPushTimer;
import com.fanruan.platform.htmlToPdf.HtmlToPdfUtils;
import com.fanruan.platform.service.CommonService;
import com.fanruan.platform.service.CompanyService;
import com.fanruan.platform.service.UserService;
import com.fanruan.platform.util.CommonUtil;
import com.fanruan.platform.util.MD5Util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import oracle.sql.ARRAY;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class CommonController {

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
        String userCode = (String) param.get("userCode");
        List<BlackList> blackList = commonService.getBlackList(userCode);
        hs.put("blackList",blackList);
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
        String userCode = (String) param.get("userCode");
        String code = (String) param.get("code");
        boolean isBlack = false;
        if(StringUtils.isBlank(userCode) || StringUtils.isBlank(code)){
            hs.put("isBlack",isBlack);
            hs.put("code",0);
            hs.put("msg","userCode or creditCode  不能为空！");
            return objectMapper.writeValueAsString(hs);
        }
        List<BlackList> blackList = commonService.getBlackList(userCode);
        for (BlackList blackInfo : blackList ){
            isBlack = blackInfo.getCode().equals(code);
            if(isBlack){
                break;
            }
        }
        hs.put("isBlack",isBlack);
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

    @RequestMapping(value = "/common/getJson/1001", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1001(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String id = (String)param.get("id");
        String jsonFlag = "1001";
//        String type = (String)param.get("type");
        TianYanChaJson tianYanChaJson = null;
//        if(!StringUtils.equals(type,"direct")){
            tianYanChaJson =  commonService.getLocalJson(id,name,jsonFlag);
//        }
        if(tianYanChaJson==null){
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("name",name);
            String dataStr = requestTianYanChaAPI(paramMap,"/services/open/cb/ic/2.0?");
            tianYanChaJson = commonService.SaveLocalJson(id,name,dataStr,jsonFlag);
        }
        hs.put("result",tianYanChaJson.getJson());
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getJson/1002", method = RequestMethod.POST)
    @ResponseBody
    public String getJson1002(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String jsonFlag = "1002";
//        String type = (String)param.get("type");
        TianYanChaJson tianYanChaJson = null;
//        if(!StringUtils.equals(type,"direct")){
            tianYanChaJson =  commonService.getLocalJson(null,name,jsonFlag);

//        }
        if(tianYanChaJson==null){
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("name",name);
            String dataStr = requestTianYanChaAPI(paramMap,"/services/open/cb/judicial/2.0?");
            tianYanChaJson = commonService.SaveLocalJson(null,name,dataStr,jsonFlag);
        }
        hs.put("result",tianYanChaJson.getJson());
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/common/getJson/818", method = RequestMethod.POST)
    @ResponseBody
    public String getJson818(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String id = (String)param.get("id");
        String jsonFlag = "818";
        TianYanChaJson tianYanChaJson = commonService.getLocalJson(id,name,jsonFlag);
        if(tianYanChaJson==null){
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("name",name);
            String dataStr = requestTianYanChaAPI(paramMap,"/services/open/ic/baseinfoV2/2.0?");
            tianYanChaJson = commonService.SaveLocalJson(id,name,dataStr,jsonFlag);
        }
        hs.put("result",tianYanChaJson.getJson());
        hs.put("code",0);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/common/getJson/966", method = RequestMethod.POST)
    @ResponseBody
    public String getJson966(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String name = (String)param.get("name");
        String id = (String)param.get("id");
        String jsonFlag = "966";
        TianYanChaJson tianYanChaJson = commonService.getLocalJson(id,name,jsonFlag);
        if(tianYanChaJson==null){
            Map<String, String> paramMap = Maps.newHashMap();
            paramMap.put("name",name);
            String dataStr = requestTianYanChaAPI(paramMap,"/services/open/stock/corpContactInfo/2.0?");
            tianYanChaJson = commonService.SaveLocalJson(id,name,dataStr,jsonFlag);
        }
        hs.put("result",tianYanChaJson.getJson());
        hs.put("code",0);
        hs.put("msg","");
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



    private String requestTianYanChaAPI(Map<String,String> paramMap,String uri) {
        String md5Hex = DigestUtils.md5Hex(CommonUtil.TIAN_YAN_CHA_USERNAME+CommonUtil.TIAN_YAN_CHA_KEY);
        StringBuffer sb = new StringBuffer(CommonUtil.TIAN_YAN_CHA+uri);
        sb.append("username="+CommonUtil.TIAN_YAN_CHA_USERNAME)
                .append("&authId="+CommonUtil.TIAN_YAN_CHA_AUTHID)
                .append("&sign="+md5Hex);
        for(Map.Entry<String,String> kv:paramMap.entrySet()){
            String key = kv.getKey();
            String value = kv.getValue();
            sb.append("&").append(key).append("=").append(value);
        }
//        .append("&name="+company.getCompanyName())
        HttpGet get = new HttpGet(sb.toString());
        get.setHeader("Authorization", CommonUtil.TIAN_YAN_CHA_AUTH);
        HttpClient client = new DefaultHttpClient();
        HttpResponse rese = null;
        String dataStr = null;
        try {
            rese = client.execute(get);
            dataStr = EntityUtils.toString(rese.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataStr;
    }




//    @RequestMapping(value = "/common/transformToPdf", method = RequestMethod.POST)
//    @ResponseBody
//    public String transformToPdf(@RequestBody Map<String,Object> param) throws JsonProcessingException {
//        HashMap<String,Object> hs=new HashMap<>();
//        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
//        Optional<Company> companyOptional = companyService.findById(companyId);
//        Company company = CommonUtils.getCompanyValue(companyOptional);
//        Map<String,Object> inputMap = Maps.newHashMap();
//        JSONObject model = companyService.getLiteRating(company, param);
//        hs.put("host", "E:\\test\\");
//        hs.put("code",0);
//        hs.put("model",model.getJSONObject("data"));
//        inputMap.put("reportNo",model.getString("requestId"));
//        inputMap.put("endTime",new Date());
//        hs.put("base",inputMap);
////        File file = new File("E:\\other\\帆软资料\\帆软项目\\test.html");
//        String pdfName =  "E:\\test\\test.pdf";
//        String templatePath =  "E:\\test\\pdf_demo\\template\\liteRating_new\\";
//        String exePath = "E:\\test\\pdf_demo\\wkhtmltopdf\\wkhtmltopdf.exe";
//        HtmlToPdfUtils.createHtmlByTemp(templatePath, "E:\\test\\test.html", hs);
//
//        boolean flag = HtmlToPdfUtils.convert("E:\\test\\test.html", "E:\\test\\", exePath, "test.pdf", "","E:\\test\\test.html",4);
//        ObjectMapper objectMapper=new ObjectMapper();
//
//        return objectMapper.writeValueAsString(hs);
//    }

    @RequestMapping(value = "/common/getJson/getDetailUrl", method = RequestMethod.POST)
    @ResponseBody
    public String getDetailUrl(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        String userName = (String)param.get("userName");
        String companyName = (String)param.get("companyName");
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
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
        hs.put("code", "0");
        hs.put("msg", "success");
        hs.put("id", company.getCompanyId());
        hs.put("url", url);
        hs.put("companyName", company.getCompanyName());
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
            company = companyService.creditCompany(companyName,user.getUserId());
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
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getRiskMorningPost(hs,userName,riskleve,companyName,startDate,endDate,pageIndex,pageSize);
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
        String startDate = (String) param.get("startDate");
        String endDate = (String) param.get("endDate");
        Integer pageIndex = CommonUtils.getIntegerValue(param.get("pageIndex"));
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize"));
        hs = commonService.getMorningNews(hs,userName,riskleve,companyName,newsEmotion,startDate,endDate,pageIndex,pageSize);
        return objectMapper.writeValueAsString(hs);
    }
}
