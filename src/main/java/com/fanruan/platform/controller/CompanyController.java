package com.fanruan.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.UserDao;
import com.fanruan.platform.dao.ZhongXinBaoLogDao;
import com.fanruan.platform.htmlToPdf.HtmlToPdfUtils;
import com.fanruan.platform.mapper.PdfMapper;
import com.fanruan.platform.service.CommonService;
import com.fanruan.platform.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.fanruan.platform.service.UserService;
import com.fanruan.platform.service.CompanyService;
import com.sinosure.exchange.edi.po.ArrayOfEdiFeedback;
import com.sinosure.exchange.edi.po.ArrayOfEntrustInput;
import com.sinosure.exchange.edi.po.EdiFeedback;
import com.sinosure.exchange.edi.po.EntrustInput;
import com.sinosure.exchange.edi.service.EdiException_Exception;
import com.sinosure.exchange.edi.service.SolEdiProxyWebService;
import com.sinosure.exchange.edi.service.SolEdiProxyWebServicePortType;
import io.swagger.models.auth.In;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

@RestController
public class CompanyController {

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private Environment environment;

    @Autowired
    private PdfMapper pdfMapper;

    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/company/getRiskInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getRiskInfo(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        String riskSource = (String) param.get("riskSource");
        List<RiskInfo> riskInfoList =  companyService.findLatestRisk(riskSource);
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("riskInfoList",riskInfoList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/searchList", method = RequestMethod.POST)
    @ResponseBody
    public String searchCompany(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        String keyword = StringUtils.trim((String) param.get("keyword"));
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        Integer page = CommonUtils.getIntegerValue(param.get("page"));
        List<Company> companyList =  companyService.searchList(userId,hs,keyword,page);
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("searchList",companyList);
        return objectMapper.writeValueAsString(hs);
    }
    @RequestMapping(value = "/company/direct/searchList", method = RequestMethod.POST)
    @ResponseBody
    public String directSearchCompany(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        String keyword = StringUtils.trim((String) param.get("keyword"));
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        Integer page = CommonUtils.getIntegerValue(param.get("page"));
        List<Company> companyList = Lists.newArrayList();
        if(userId!=null){
            companyList =  companyService.directSearchList(userId,keyword,page);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("searchList",companyList);
        hs.put("sourceType","模糊查询接口");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/directSearch", method = RequestMethod.POST)
    @ResponseBody
    public String directSearch(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String companyName = StringUtils.trim((String) param.get("companyName"));
        if(StringUtils.isBlank(companyName)){
            hs.put("msg", "请填写正确的companyName");
            hs.put("data","");
            hs.put("sourceType","模糊查询接口");
            return objectMapper.writeValueAsString(hs);
        }
        JSONObject dataStr  =  companyService.getDirectSearch(companyName);
        hs.put("msg", "");
        hs.put("data",dataStr);
        hs.put("sourceType","模糊查询接口");
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/latestWords", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestWords(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer limit = CommonUtils.getIntegerValue(param.get("limit"));
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        if(limit==null){
            limit = 5;
        }
        List<SearchWords> SearchWordsList =  companyService.getLatestWords(userId,limit);
        ObjectMapper objectMapper = new ObjectMapper();
        hs.put("latestWords",SearchWordsList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/detail", method = RequestMethod.POST)
    @ResponseBody
    public String getDetail( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        List<CompanyExtendInfo> companyList =  companyService.searchDetailList(param);
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("searchList",companyList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/getBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getBaseInfo( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId"));
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        String companyName = (String)param.get("companyName");
        ObjectMapper objectMapper=new ObjectMapper();
        if(companyId==null && StringUtils.isBlank(companyName)){
            hs.put("code","1");
            hs.put("msg","查询公司基本信息失败，companyId为空");
        }else {
            TianYanChaInfo tianYanChaInfo =  companyService.getTianYanChaInfo(companyId,userId,companyName);
            if(tianYanChaInfo==null){
                hs.put("code","2");
                hs.put("msg","查询公司基本信息失败");
            }else {
                hs.put("baseInfo",tianYanChaInfo);
                hs.put("code","0");
                hs.put("msg","");
            }
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/getCompanyInfoByName", method = RequestMethod.POST)
    @ResponseBody
    public String getCompanyInfoByName( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        String companyName = (String)param.get("companyName");
        ObjectMapper objectMapper=new ObjectMapper();
        if(companyName==null){
            hs.put("code","1");
            hs.put("msg","查询公司基本信息失败，companyName为空");
        }else {
            Company company =  companyService.getCompanyByName(companyName);
            if(company==null){
                hs.put("code","2");
                hs.put("msg","查询公司基本信息失败");
            }else {
                hs.put("company",company);
                hs.put("code","0");
                hs.put("msg","");
            }
        }
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongxinbao", method = RequestMethod.POST)
    @ResponseBody
    public String getZhongXinbao( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        User user = userService.getUserById(userId);
        ArrayOfEntrustInput arrayOfEntrustInput = new ArrayOfEntrustInput();
        EntrustInput entrustInput = new EntrustInput();
        buildEntrustInput(arrayOfEntrustInput, entrustInput,param);
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
                hs.put("returnMsg",returnMsg);
                hs.put("returnCode",returnCode);
                companyService.saveZhongXinBaoLog(user,e,entrustInput);
                companyService.saveReportPushInfo(user,entrustInput);
            }
        } catch (EdiException_Exception e) {
            e.printStackTrace();
            hs.put("returnCode",-1);
            hs.put("returnMsg","调用中信保接口失败，请确认参数");
        }

        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongxinbaoApply", method = RequestMethod.POST)
    @ResponseBody
    public String getZhongXinbaoApply( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        User user = userService.getUserById(userId);
        //jina
        List<String> reportApplyUserNameList = commonService.getReportApplyUserNameList(user.getUsername());
        for(String userName:reportApplyUserNameList){
            commonService.insertReportApply(userName);
        }

       //
        ArrayOfEntrustInput arrayOfEntrustInput = new ArrayOfEntrustInput();
        EntrustInput entrustInput = new EntrustInput();
        buildEntrustInput(arrayOfEntrustInput, entrustInput,param);
        companyService.saveZhongXinBaoLog(user,entrustInput);
        hs.put("returnMsg","申请成功，请耐心等待审核");
        hs.put("returnCode","0");
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongxinbaoApprove", method = RequestMethod.POST)
    @ResponseBody
    public String getZhongXinbaoApprove( @RequestBody Map<String,Object> param) throws JsonProcessingException {
       HashMap<String,Object> hs=new HashMap<>();
        Integer approve = CommonUtils.getIntegerValue(param.get("approve")) ;
        User user = userService.getUserById(approve);
        Integer approveCode = CommonUtils.getIntegerValue(param.get("approveCode")) ;
        if(approveCode == 999){
            String  corpSerialNo = (String) param.get("corpSerialNo");
            ZhongXinBaoLog log = companyService.findByCorpSerialNo(corpSerialNo);
            log.setApproveCode(String.valueOf(approveCode));
            log.setApproveby(user.getUsername());
            log.setApproveDate(new Timestamp(System.currentTimeMillis()));
            companyService.saveZhongXinBaoLog(log);
            String updateBy = log.getUpdateBy();
            String approveBy = log.getApproveby();
            companyService.insertOAMsg(updateBy, user.getName());
            hs.put("returnCode",0);
            hs.put("returnMsg","操作成功");
            ObjectMapper objectMapper=new ObjectMapper();
            return objectMapper.writeValueAsString(hs);
        }
        ArrayOfEntrustInput arrayOfEntrustInput = new ArrayOfEntrustInput();
        EntrustInput entrustInput = new EntrustInput();
        buildEntrustApprove(arrayOfEntrustInput, entrustInput,param);
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
                hs.put("returnMsg",returnMsg);
                hs.put("returnCode",returnCode);
                companyService.approveZhongXinBaoLog(user,e,entrustInput);
                companyService.saveReportPushInfo(user,entrustInput);
            }
        } catch (EdiException_Exception e) {
            e.printStackTrace();
            hs.put("returnCode",-1);
            hs.put("returnMsg","调用中信保接口失败，请确认参数");
        }

        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.writeValueAsString(hs);

    }
    @RequestMapping(value = "/company/getAllCompanyLevel", method = RequestMethod.POST)
    @ResponseBody
    public String getAllCompanyLevel(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        if(null == userId){
            hs.put("code",1);
            hs.put("msg","无法获取登陆人信息");
            return objectMapper.writeValueAsString(hs);
        }
        User user = userService.getUserById(userId);
        if(null == user){
            hs.put("code",1);
            hs.put("msg","无法获取登陆人信息");
            return objectMapper.writeValueAsString(hs);
        }
        List<CompanyLevel> treeData = new ArrayList<>();
        companyService.getTreeData(user.getCompanyCode(),treeData);
        if(null == treeData){
            hs.put("code",2);
            hs.put("msg","未查询到公司架构树信息");
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code",0);
        hs.put("msg","");
        hs.put("treeData",treeData);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/getCodeInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getCodeInfo( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        ZhongXinBaoLog log = null;
        if(userId!=null&&companyId!=null){
            log  = companyService.getCodeInfo(userId,companyId);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("code",0);
        hs.put("codeInfo",log);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/getCodeInfoByUserId", method = RequestMethod.POST)
    @ResponseBody
    public String getCodeInfoByUserId( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        Integer userId = CommonUtils.getIntegerValue(param.get("userId")) ;
        ZhongXinBaoLog log = null;
        if(userId!=null){
            log  = companyService.getCodeInfo(userId);
        }
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("code",0);
        hs.put("codeInfo",log);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongxinbao/getShareInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getShareInfo( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        List<ZhongXinBaoShare> shareInfoList = companyService.getShareInfo(companyId);
        if(shareInfoList==null){
            hs.put("code",1);
            hs.put("shareInfo",null);
            hs.put("msg","查询不到股票信息或者参数有误");
        }
        hs.put("code",0);
        hs.put("shareInfo",shareInfoList);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongxinbao/getBusinessInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getBusinessInfo( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        ZhongXinBaoInfo zhongXinBaoInfo  = companyService.getBusinessInfo(companyId);
        if(zhongXinBaoInfo==null){
            hs.put("code",1);
            hs.put("businessInfo",zhongXinBaoInfo);
            hs.put("msg","查询不到基本信息或者参数有误");
        }else {
            hs.put("code",0);
            hs.put("shareInfo",zhongXinBaoInfo);
            hs.put("msg","");
        }
        return objectMapper.writeValueAsString(hs);
    }
    @RequestMapping(value = "/company/zhongxinbao/getAllBusinessInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getAllBusinessInfo( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String reportcorpchnname = (String)param.get("reportcorpchnname");
        String reportcorpengname = (String)param.get("reportcorpengname");
        String reportbuyerno = (String)param.get("reportbuyerno");
        if(StringUtils.isNotBlank(reportcorpchnname) || StringUtils.isNotBlank(reportcorpengname)){
            hs = companyService.getBusinessInfo(reportcorpchnname,reportcorpengname,reportbuyerno,hs);
        }
        if(hs.isEmpty()){
            hs.put("code",1);
            hs.put("businessInfo",null);
            hs.put("shareList",null);
            hs.put("pdfList",null);
            hs.put("msg","查询不到基本信息或者参数有误");
        }
        return objectMapper.writeValueAsString(hs);
    }

    private void buildEntrustApprove(ArrayOfEntrustInput arrayOfEntrustInput, EntrustInput entrustInput, Map<String, Object> param) {

        String  corpSerialNo = (String) param.get("corpSerialNo");
        ZhongXinBaoLog log = companyService.findByCorpSerialNo(corpSerialNo);
        entrustInput.setCorpSerialNo(getJAXBElement("corpSerialNo",corpSerialNo));
        entrustInput.setClientNo(getJAXBElement("clientNo",log.getClientNo()));
        String reportbuyerNo = log.getReportbuyerNo();
        String clientNo = log.getClientNo();
        if(StringUtils.isBlank(reportbuyerNo)){
            String reportCorpCountryCode = log.getReportCorpCountryCode();
            String reportCorpChnName = log.getReportCorpChnName();
            String reportCorpEngName = log.getReportCorpEngName();
            String reportCorpaddress = log.getReportCorpaddress();
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(log.getIstranslation())) ;

            entrustInput.setReportCorpCountryCode(getJAXBElement("reportCorpCountryCode",reportCorpCountryCode));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
            entrustInput.setReportCorpaddress(getJAXBElement("reportCorpaddress",reportCorpaddress));
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setClientNo(getJAXBElement("clientno",clientNo));
        }else{
            entrustInput.setReportbuyerNo(getJAXBElement("reportbuyerNo",reportbuyerNo));
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(log.getIstranslation())) ;
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
        }
        arrayOfEntrustInput.getEntrustInput().add(entrustInput);
    }
    private void buildEntrustInput(ArrayOfEntrustInput arrayOfEntrustInput, EntrustInput entrustInput, Map<String, Object> param) {

        String reportbuyerNo = (String) param.get("reportbuyerNo");
        String clientNo = (String) param.get("clientNo");
//        String corpSerialNo = (String) param.get("corpSerialNo");
        String  corpSerialNo = CommonUtils.getRandomCode();
        String creditno = (String) param.get("creditno");
        entrustInput.setCorpSerialNo(getJAXBElement("corpSerialNo",corpSerialNo));
        entrustInput.setClientNo(getJAXBElement("clientNo",clientNo));
        entrustInput.setCreditno(getJAXBElement("creditno",creditno));
        String reportCorpChnName = (String) param.get("reportCorpChnName");
        String reportCorpEngName = (String) param.get("reportCorpEngName");
        if(StringUtils.isBlank(reportbuyerNo)){
            String reportCorpCountryCode = (String) param.get("reportCorpCountryCode");
            String reportCorpaddress = (String) param.get("reportCorpaddress");
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(param.get("istranslation"))) ;
            entrustInput.setReportCorpCountryCode(getJAXBElement("reportCorpCountryCode",reportCorpCountryCode));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
            entrustInput.setReportCorpaddress(getJAXBElement("reportCorpaddress",reportCorpaddress));
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setClientNo(getJAXBElement("clientno",clientNo));
            entrustInput.setCreditno(getJAXBElement("creditno",creditno));
        }else{
            entrustInput.setReportbuyerNo(getJAXBElement("reportbuyerNo",reportbuyerNo));
            String istranslation = String.valueOf(CommonUtils.getIntegerValue(param.get("istranslation"))) ;
            entrustInput.setIstranslation(getJAXBElement("istranslation",istranslation));
            entrustInput.setCreditno(getJAXBElement("creditno",creditno));
            entrustInput.setReportCorpChnName(getJAXBElement("reportCorpChnName",reportCorpChnName));
            entrustInput.setReportCorpEngName(getJAXBElement("reportCorpEngName",reportCorpEngName));
        }
        arrayOfEntrustInput.getEntrustInput().add(entrustInput);
    }



    private JAXBElement<String> getJAXBElement(String filedName, String fieldValue) {
        QName corpSerialNo_NAME = new QName("http://po.edi.exchange.sinosure.com", filedName);
        return new JAXBElement<String>(corpSerialNo_NAME, String.class, fieldValue);
    }

    @RequestMapping(value = "/company/getPDFList", method = RequestMethod.POST)
    @ResponseBody
    public String getPDFList( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        Company company = companyService.getCompanyById(companyId);
        if(company==null||StringUtils.isBlank(company.getCompanyName())){
            hs.put("code","2");
            hs.put("msg","未查询到公司或者公司名为空");
            return objectMapper.writeValueAsString(hs);
        }
        List<ZhongXinBaoPDF> zhongXinBaoPDFS = pdfMapper.selectZhongXinBaoPDF(company.getCompanyName(),"","");
        if(CollectionUtils.isEmpty(zhongXinBaoPDFS)){
            hs.put("code","3");
            hs.put("msg","服务器上未查询到该公司pdf文件");
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code","0");
        hs.put("msg","");
        hs.put("pdfList",zhongXinBaoPDFS);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 获取所有信保报告
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/getPDFListAll", method = RequestMethod.POST)
    @ResponseBody
    public String getPDFListAll( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String companyName = param.get("companyName")!=null?param.get("companyName").toString():"";
        String xcode = param.get("xcode")!=null?param.get("xcode").toString():"";
        Integer page = CommonUtils.getIntegerValue(param.get("pageIndex")) ;
        Integer pageSize = CommonUtils.getIntegerValue(param.get("pageSize")) ;
        Integer total = pdfMapper.selectZhongXinBaoPDFListCount(xcode,companyName);
        List<ZhongXinBaoPDFList> zhongXinBaoPDFS = pdfMapper.selectZhongXinBaoPDFList(xcode,companyName,pageSize,page);
//        if(CollectionUtils.isEmpty(zhongXinBaoPDFS)){
//            hs.put("code","3");
//            hs.put("msg","服务器上未查询到pdf文件");  //服务器上未查询到pdf文件   修改报错提示
//            return objectMapper.writeValueAsString(hs);
//        }
        hs.put("code","0");
        hs.put("msg","");
        hs.put("pdfList",zhongXinBaoPDFS);
        hs.put("totalRecords",total);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/getPDF", method = RequestMethod.POST)
    @ResponseBody
    public String getPDF(HttpServletResponse response, @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String noticeSerialno = (String)param.get("noticeSerialno");
        String filePath = "/home/ftpuser/";
        String fileName = noticeSerialno;
        File file = new File(filePath+fileName);
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

    @RequestMapping(value = "/company/getNewCompany", method = RequestMethod.POST)
    @ResponseBody
    public String getNewCompany( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        List<NewCompany> newCompanys = companyService.getNewCompany();
        ObjectMapper objectMapper=new ObjectMapper();
        hs.put("code",0);
        hs.put("newCompany",newCompanys);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }




    @RequestMapping(value = "/company/getCompanyIDVerification", method = RequestMethod.POST)
    @ResponseBody
    public String getCompanyIDVerification( @RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String code = (String)param.get("code");
        if(StringUtils.isBlank(code)){
            hs.put("code",1);
            hs.put("CompanyIDVerification","");
            hs.put("msg","工号不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        CompanyIDVerification companyIDVerification = companyService.getCompanyIDVerification(code);
        if(null == companyIDVerification){
            hs.put("CompanyIDVerification","");
            hs.put("code",2);
            hs.put("msg","未查询到工号校验规则");
            return objectMapper.writeValueAsString(hs);
        }
        hs.put("code",0);
        hs.put("CompanyIDVerification",companyIDVerification);
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/getCompayNameAndCreditCode", method = RequestMethod.POST)
    @ResponseBody
    public String getCompayNameAndCreditCode(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String, Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        List<CompayNameCode> compayNameAndCreditCodes = companyService.getCompayNameAndCreditCode();
        hs.put("code",0);
        hs.put("msg","");
        hs.put("compayNameAndCreditCodes",compayNameAndCreditCodes);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/getCompanyStatus", method = RequestMethod.POST)
    @ResponseBody
    public String getCompanyStatus(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String, Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String compayName = (String) param.get("companyName");
        String creditCode = (String) param.get("creditCode");
        String updateBy = (String) param.get("updateBy");
        String status = "";
        Optional<BlackInfo> blackInfo = companyService.getCompanyStatus(compayName,creditCode,updateBy);
        if(blackInfo.isPresent()){
            BlackInfo bInfo = blackInfo.get();
            status = bInfo.getStatus();
        }
        hs.put("code",0);
        hs.put("msg","");
        hs.put("status",status);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/getCompayNameList", method = RequestMethod.POST)
    @ResponseBody
    public String getCompayNameList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String, Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String userName = (String) param.get("userName");
        if(StringUtils.isBlank(userName)){
            hs.put("code",1);
            hs.put("msg","企业名称列表获取失败,用户名不能为空！");
            hs.put("companyNameList","");
            return objectMapper.writeValueAsString(hs);
        }
        List<String> companyNameList = companyService.getCompanyNameList(userName);
        hs.put("code",0);
        hs.put("msg","");
        hs.put("companyNameList",companyNameList);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/getZCXCompayNameList", method = RequestMethod.POST)
    @ResponseBody
    public String getZCXCompayNameList(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String, Object> hs = new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String userName = (String) param.get("userName");
        if(StringUtils.isBlank(userName)){
            hs.put("code",1);
            hs.put("msg","企业名称列表获取失败,用户名不能为空！");
            hs.put("companyNameList","");
            return objectMapper.writeValueAsString(hs);
        }
        List<String> companyNameList = companyService.getZCXCompayNameList(userName);
        hs.put("code",0);
        hs.put("msg","");
        hs.put("companyNameList",companyNameList);
        return objectMapper.writeValueAsString(hs);
    }
}
