package com.fanruan.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.htmlToPdf.HtmlToPdfUtils;
import com.fanruan.platform.service.CompanyService;
import com.fanruan.platform.service.UserService;
import com.fanruan.platform.util.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sinosure.exchange.edi.po.ArrayOfEdiFeedback;
import com.sinosure.exchange.edi.po.ArrayOfEntrustInput;
import com.sinosure.exchange.edi.po.EdiFeedback;
import com.sinosure.exchange.edi.po.EntrustInput;
import com.sinosure.exchange.edi.service.EdiException_Exception;
import com.sinosure.exchange.edi.service.SolEdiProxyWebService;
import com.sinosure.exchange.edi.service.SolEdiProxyWebServicePortType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ReportController {
    /**
     * 日志对象
     */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(HtmlToPdfUtils.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private Environment environment;

    @RequestMapping(value = "/company/zhongChengXin/getFinancialDeminingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getFinancialDeminingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        requestNewFinancialDeminingHtml(response, param, hs, company);
        return objectMapper.writeValueAsString(hs);
    }


    @RequestMapping(value = "/company/zhongChengXin/getLiteRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLiteRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        requestNewLiteRatingHtml(response, param, hs, company);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongChengXin/getRiskScreenHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getRiskScreenHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        requestNewRiskScreenHtml(response, param, hs, company);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongChengXin/getCityInvRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getCityInvRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        requestNewCityInvRatingHtml(response, param, hs, company);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongChengXin/getRegionInfo", method = RequestMethod.POST)
    @ResponseBody
    public String getRegionInfo( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        JSONObject queryAreaDetails = companyService.getQueryAreaDetails(param);
        hs.put("code","0");
        hs.put("regionInfo",queryAreaDetails);
        return objectMapper.writeValueAsString(hs);
    }

    @RequestMapping(value = "/company/zhongChengXin/getRegionRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getRegionRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        requestNewRegionRatingHtml(response, param, hs, company);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 产业企业信用评价
     * @param response
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/zhongChengXin/getLatestLiteRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestLiteRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        File temp = null;
        try {
            temp = File.createTempFile("liteRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String creditCode = (String)param.get("creditCode") ;
        if(StringUtils.isBlank(creditCode)){
            hs.put("code","1");
            hs.put("msg","参数creditCode不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        String reportType = "产业企业信用评价";
        Company company = companyService.findCompanyByCode(creditCode);
        List<Report> reportList = companyService.getReportList(company,reportType);
        if(CollectionUtils.isEmpty(reportList)){
            requestNewLiteRatingHtml(response, param, hs, company);
        }else {
            getDataBaseReportHtml(response, hs, temp, reportList);
        }
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 城投企业信用评价
     * @param response
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/zhongChengXin/getLatestCityInvRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestCityInvRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        File temp = null;
        try {
            temp = File.createTempFile("cityInvRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String creditCode = (String)param.get("creditCode") ;
        if(StringUtils.isBlank(creditCode)){
            hs.put("code","1");
            hs.put("msg","参数creditCode不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        String reportType = "城投企业信用评价";
        Company company = companyService.findCompanyByCode(creditCode);
        List<Report> reportList = companyService.getReportList(company,reportType);
        if(CollectionUtils.isEmpty(reportList)){
            requestNewCityInvRatingHtml(response, param, hs, company);
        }else {
            getDataBaseReportHtml(response, hs, temp, reportList);
        }
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 区域信用评价
     * @param response
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/zhongChengXin/getLatestRegionRatingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestRegionRatingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        File temp = null;
        try {
            temp = File.createTempFile("regionRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String creditCode = (String)param.get("creditCode") ;
        if(StringUtils.isBlank(creditCode)){
            hs.put("code","1");
            hs.put("msg","参数creditCode不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        String reportType = "区域信用评价";
        Company company = companyService.findCompanyByCode(creditCode);
        List<Report> reportList = companyService.getReportList(company,reportType);
        if(CollectionUtils.isEmpty(reportList)){
            requestNewRegionRatingHtml(response, param, hs, company);
        }else {
            getDataBaseReportHtml(response, hs, temp, reportList);
        }
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 风险初筛
     * @param response
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/zhongChengXin/getLatestRiskScreenHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestRiskScreenHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        File temp = null;
        try {
            temp = File.createTempFile("riskScreenHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String creditCode = (String)param.get("creditCode") ;
        if(StringUtils.isBlank(creditCode)){
            hs.put("code","1");
            hs.put("msg","参数creditCode不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        String reportType = "风险初筛";
        Company company = companyService.findCompanyByCode(creditCode);
        List<Report> reportList = companyService.getReportList(company,reportType);
        if(CollectionUtils.isEmpty(reportList)){
            requestNewRiskScreenHtml(response, param, hs, company);
        }else {
            getDataBaseReportHtml(response, hs, temp, reportList);
        }
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 财务排雷
     * @param response
     * @param param
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/company/zhongChengXin/getLatestFinancialDeminingHtml", method = RequestMethod.POST)
    @ResponseBody
    public String getLatestFinancialDeminingHtml( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        File temp = null;
        try {
            temp = File.createTempFile("financialDemining_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String creditCode = (String)param.get("creditCode") ;
        if(StringUtils.isBlank(creditCode)){
            hs.put("code","1");
            hs.put("msg","参数creditCode不能为空");
            return objectMapper.writeValueAsString(hs);
        }
        String reportType = "财务排雷";
        Company company = companyService.findCompanyByCode(creditCode);
//        List<Report> reportList = companyService.getReportList(company,reportType);
//        if(CollectionUtils.isEmpty(reportList)){
        requestNewFinancialDeminingHtml(response, param, hs, company);
//        }else {
//            getDataBaseReportHtml(response, hs, temp, reportList);
//        }
        return objectMapper.writeValueAsString(hs);
    }





    private void requestNewFinancialDeminingHtml(HttpServletResponse response, Map<String, Object> param, HashMap<String, Object> hs, Company company) {
        InputStream is = companyService.getFinancialDeminingHtml(company,param);
        if(is==null){
            hs.put("code","4");
            hs.put("mag","非常抱歉，第三方接口内无财报数据，无法生成对应报告！");
            return;
        }
        File temp = null;
        try {
            temp = File.createTempFile("financialDeminingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String reportType = "财务排雷";
        dealWithNewHtml(response, param, hs, company, is, temp, reportType);
    }

    private void requestNewRegionRatingHtml(HttpServletResponse response,  Map<String, Object> param, HashMap<String, Object> hs, Company company) {
        InputStream is = companyService.getRegionRatingHtml(company,param);
        File temp = null;
        try {
            temp = File.createTempFile("regionRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String reportType = "区域信用评价";
        dealWithNewHtml(response, param, hs, company, is, temp, reportType);
    }

    private void requestNewCityInvRatingHtml(HttpServletResponse response,  Map<String, Object> param, HashMap<String, Object> hs, Company company) {
        InputStream is = companyService.getCityInvRatingHtml(company,param);
        if(is==null){
            hs.put("code","4");
            hs.put("mag","非常抱歉，第三方接口内无财报数据，无法生成对应报告！");
            return;
        }
        File temp = null;
        try {
            temp = File.createTempFile("cityInvRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String reportType = "城投企业信用评价";
        dealWithNewHtml(response, param, hs, company, is, temp, reportType);
    }

    private void requestNewLiteRatingHtml(HttpServletResponse response,  Map<String, Object> param, HashMap<String, Object> hs, Company company) {
        InputStream is = companyService.getLiteRatingHtml(company,param);
        if(is==null){
            hs.put("code","4");
            hs.put("mag","非常抱歉，第三方接口内无财报数据，无法生成对应报告！");
            return;
        }
        File temp = null;
        try {
            temp = File.createTempFile("liteRatingHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String reportType = "产业企业信用评价";
        dealWithNewHtml(response, param, hs, company, is, temp, reportType);
    }

    private void requestNewRiskScreenHtml(HttpServletResponse response,  Map<String, Object> param, HashMap<String, Object> hs, Company company) {
        InputStream is = companyService.getRiskScreenHtml(company,param);
        File temp = null;
        try {
            temp = File.createTempFile("riskScreenHtml_", ".html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String reportType = "风险初筛";
        dealWithNewHtml(response, param, hs, company, is, temp, reportType);
    }

    private void dealWithNewHtml(HttpServletResponse response, Map<String, Object> param, HashMap<String, Object> hs, Company company, InputStream is, File temp, String reportType) {
        if(is!=null){
            try {
                IOUtils.copy(is,new FileOutputStream(temp));
                long length = temp.length();
                if(length>1024){
                    FileInputStream fileIs = new FileInputStream(temp);
                    Report report = companyService.saveReport(company,param, fileIs,reportType);
                    FileInputStream fileInput = new FileInputStream(temp);
                    response.addHeader("Content-Disposition", "attachment;filename=" + report.getFileName());
                    ServletOutputStream outputStream = response.getOutputStream();
                    response.setContentType("application/octet-stream");
                    IOUtils.copy(fileInput,outputStream,1024);
                    hs.put("code","0");
                }else {
                    hs.put("code","3");
                    hs.put("msg","非常抱歉，第三方接口相关数据无法获取，报告无法生成！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                hs.put("code","1");
                hs.put("msg","文件下载出错");
            }finally {
                if(temp!=null){
                    temp.delete();
                }
            }
        }else {
            hs.put("code","2");
            hs.put("msg","请求参数有误！");
        }
    }




    private void getDataBaseReportHtml(HttpServletResponse response, HashMap<String, Object> hs, File temp, List<Report> reportList) {
        Report report = reportList.get(0);
        byte[] reportHtml = report.getReportHtml();
        try {

            FileOutputStream fileOutputStream = new FileOutputStream(temp);
            fileOutputStream.write(reportHtml);
            fileOutputStream.flush();
            fileOutputStream.close();
            FileInputStream fileInput = new FileInputStream(temp);
            response.addHeader("Content-Disposition", "attachment;filename=" + report.getFileName());
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            IOUtils.copy(fileInput,outputStream,1024);
            hs.put("code","0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/company/zhongChengXin/getReportList", method = RequestMethod.POST)
    @ResponseBody
    private String getReportList(HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
//        String reportType = "产业企业信用评价";
//        String reportType = "风险初筛";
//        String reportType = "城投企业信用评价";
//        String reportType = "区域信用评价";
        String reportType = (String)param.get("reportType");
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return objectMapper.writeValueAsString(hs);
        }
        Optional<Company> companyOptional = companyService.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        List<Report> reportList = companyService.getReportList(company, reportType);
        hs.put("reportList",reportList);
        hs.put("code","0");
        hs.put("msg","");
        return objectMapper.writeValueAsString(hs);
    }



    @RequestMapping(value = "/company/zhongChengXin/getLiteRatingPDF", method = RequestMethod.POST)
    @ResponseBody
    public String getLiteRatingPDF( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String fileName = (String)param.get("fileName") ;
        if(StringUtils.isBlank(fileName)){
            return null;
        }
        Report report = companyService.getReport(fileName);
        try {
            String[] split = fileName.split("\\.");
            File tempHtml = File.createTempFile(split[0],"."+split[1]);
            String absolutePath = tempHtml.getParentFile().getAbsolutePath()+"/";

            if(report!=null){
                byte[] reportHtml = report.getReportHtml();
                FileOutputStream fileOutputStream = new FileOutputStream(tempHtml);
                fileOutputStream.write(reportHtml);
                fileOutputStream.flush();
                String name = split[0]+".pdf";
                String exePath = environment.getProperty("wkhtmltopdf_path");
                boolean flag = HtmlToPdfUtils.convert(tempHtml.getAbsolutePath(), absolutePath, exePath, name, "",tempHtml.getAbsolutePath(),4);
                File file = new File(absolutePath + "/" + name);
                if(!file.exists()){
                    return null;
                }
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO-8859-1"));
                ServletOutputStream outputStream = response.getOutputStream();
                response.setContentType("application/octet-stream");
                IOUtils.copy(is,outputStream,1024);
                hs.put("code","0");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hs.put("code","1");
            hs.put("msg","文件下载出错");
        }
        return objectMapper.writeValueAsString(hs);
    }


//    @RequestMapping(value = "/company/zhongChengXin/getReportFromFTP", method = RequestMethod.POST)
//    @ResponseBody
//    public String getReportFromFTP( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
//        HashMap<String,Object> hs=new HashMap<>();
//        ObjectMapper objectMapper=new ObjectMapper();
//        String fileName = (String)param.get("fileName") ;
//        log.info("fileName: " + fileName);
//        if(StringUtils.isBlank(fileName)){
//            return null;
//        }
//        Report report = companyService.getReport(fileName);
//        try {
//            String[] split = fileName.split("\\.");
//            if(report!=null){
//                String name = split[0]+".pdf";
//                File file = new File("/Users/yangwenqiang/Temp/zcxPDF/" + name);
////                File file = new File(CommonUtil.ZCX_PDF_FTP_PATH + name);
//                if(!file.exists()){
//                    File sourceFile = new File("/Users/yangwenqiang/Temp/" + name);
//                    FileUtils.copyFile(sourceFile, file);
////                    return null;
//                }
//
//                if(!file.exists()) return null;
//                InputStream is = new BufferedInputStream(new FileInputStream(file));
//                response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO-8859-1"));
//                ServletOutputStream outputStream = response.getOutputStream();
//                response.setContentType("application/octet-stream");
//                IOUtils.copy(is,outputStream,1024);
//                hs.put("code","0");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hs.put("code","1");
//            hs.put("msg","文件下载出错");
//        }
//        return objectMapper.writeValueAsString(hs);
//    }

    @RequestMapping(value = "/company/zhongChengXin/getReportPDF", method = RequestMethod.POST)
    @ResponseBody
    public String getReportPDF( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String fileName = (String)param.get("fileName") ;
        log.info("fileName: " + fileName);
        if(StringUtils.isBlank(fileName)) return null;

        Report report = companyService.getReport(fileName);
        if(report == null) return null;

        try {
            String[] split = fileName.split("\\.");
            String namePDF = split[0]+".pdf";

            File outPDF = new File(CommonUtil.ZCX_PDF_FTP_PATH + namePDF);
            log.info("zcxPathPDF: " + CommonUtil.ZCX_PDF_FTP_PATH);
            if(!outPDF.exists()){
                File tempHtml = File.createTempFile(split[0],"."+split[1], new File(CommonUtil.ZCX_PDF_FTP_PATH));
                String absolutePath = tempHtml.getParentFile().getAbsolutePath()+"/";
                log.info("absolutePath: " + absolutePath);
                byte[] reportHtml = report.getReportHtml();
                FileOutputStream fileOutputStream = new FileOutputStream(tempHtml);
                fileOutputStream.write(reportHtml);
                fileOutputStream.flush();
                log.info("pdfName: " + namePDF);
                String exePath = environment.getProperty("wkhtmltopdf_path");
                log.info("exePath: " + exePath);
                log.info("outPut: " + tempHtml.getAbsolutePath());
                HtmlToPdfUtils.convert(tempHtml.getAbsolutePath(), absolutePath, exePath, namePDF, "",tempHtml.getAbsolutePath(),4);
                outPDF = new File(CommonUtil.ZCX_PDF_FTP_PATH + namePDF);

                boolean isDelete = tempHtml.delete();
                if(isDelete) log.info("html is deleted: " + tempHtml.getName());
            }

            if(!outPDF.exists()) return null;

            InputStream is = new BufferedInputStream(new FileInputStream(outPDF));
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(namePDF.getBytes("UTF-8"), "ISO-8859-1"));
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

//    @RequestMapping(value = "/company/zhongChengXin/getReportPDF", method = RequestMethod.POST)
//    @ResponseBody
//    public String getReportPDF( HttpServletResponse response,@RequestBody Map<String,Object> param) throws JsonProcessingException {
//        HashMap<String,Object> hs=new HashMap<>();
//        ObjectMapper objectMapper=new ObjectMapper();
//        String fileName = (String)param.get("fileName") ;
//        log.info("fileName: " + fileName);
//        if(StringUtils.isBlank(fileName)){
//            return null;
//        }
//        Report report = companyService.getReport(fileName);
//        try {
//            String[] split = fileName.split("\\.");
//            File tempHtml = File.createTempFile(split[0],"."+split[1]);
//            String absolutePath = tempHtml.getParentFile().getAbsolutePath()+"/";
//            log.info("absolutePath: " + absolutePath);
//            if(report!=null){
//                byte[] reportHtml = report.getReportHtml();
//                FileOutputStream fileOutputStream = new FileOutputStream(tempHtml);
//                fileOutputStream.write(reportHtml);
//                fileOutputStream.flush();
//                String name = split[0]+".pdf";
//                log.info("pdfName: " + name);
//                String exePath = environment.getProperty("wkhtmltopdf_path");
//                log.info("exePath: " + exePath);
//                log.info("outPut: " + tempHtml.getAbsolutePath());
//                boolean flag = HtmlToPdfUtils.convert(tempHtml.getAbsolutePath(), absolutePath, exePath, name, "",tempHtml.getAbsolutePath(),4);
//                File file = new File(absolutePath + "/" + name);
//                if(!file.exists()){
//                    return null;
//                }
//                InputStream is = new BufferedInputStream(new FileInputStream(file));
//                response.addHeader("Content-Disposition", "attachment;filename=" + new String(name.getBytes("UTF-8"), "ISO-8859-1"));
//                ServletOutputStream outputStream = response.getOutputStream();
//                response.setContentType("application/octet-stream");
//                IOUtils.copy(is,outputStream,1024);
//                hs.put("code","0");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            hs.put("code","1");
//            hs.put("msg","文件下载出错");
//        }
//        return objectMapper.writeValueAsString(hs);
//    }

    @RequestMapping(value = "/report/zhongChengXin/reportExist", method = RequestMethod.POST)
    @ResponseBody
    public String reportExist(@RequestBody Map<String,Object> param) throws JsonProcessingException {
        HashMap<String,Object> hs=new HashMap<>();
        ObjectMapper objectMapper=new ObjectMapper();
        String creditCode = (String)param.get("creditCode") ;
        String reportType = (String)param.get("reportType") ;

        boolean existFlag = companyService.reportExist(reportType, creditCode);

        hs.put("existFlag",existFlag);

        return objectMapper.writeValueAsString(hs);
    }


}
