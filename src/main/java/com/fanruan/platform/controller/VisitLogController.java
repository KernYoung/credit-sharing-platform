package com.fanruan.platform.controller;

import com.fanruan.platform.bean.ReportParameter;
import com.fanruan.platform.bean.UserBehavior;
import com.fanruan.platform.bean.UserVisit;
import com.fanruan.platform.bean.UserVisitList;
import com.fanruan.platform.service.VisitLogService;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.ReturnJson;
import com.fanruan.platform.util.SqlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RequestMapping
@RestController
/**
 * 用户访问记录
 */
public class VisitLogController {

    @Autowired
    private VisitLogService visitLogService;

    @RequestMapping(value = "/visitLog/getUserVisit",method = RequestMethod.POST)
    @ResponseBody
    public String getUserVisit(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
       String checkPara = checkPara(para);
       if(checkPara!=null&&!checkPara.equals("")){
           return checkPara;
       }
        ReportParameter rpVo = getReportParameter1(para);
        List<UserVisit> userVisitList =  visitLogService.getUserVisit(rpVo);
        String json = getJson("0","查询成功",userVisitList);
        return json;
    }
    @RequestMapping(value = "/visitLog/getCompanyList",method = RequestMethod.POST)
    @ResponseBody
    public String getCompanyList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{

        List<String> listName = visitLogService.getCompanyList(para);
        String json = ReturnJson.getJson("0","查询成功",listName);
        return json;
    }

    @RequestMapping(value = "/visitLog/getLogMonthTotal",method = RequestMethod.POST)
    @ResponseBody
    public String getLogMonthTotal(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
       try {
           ReportParameter rpVo = new ReportParameter();
           String json = visitLogService.getLogMonthTotal(rpVo);
           return json;
       }catch (Exception e){
           String json = ReturnJson.getJson("1","查询失败",null);
           return json;
       }

    }



    @RequestMapping(value = "/visitLog/getUserVisitList",method = RequestMethod.POST)
    @ResponseBody
    public String getUserVisitList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        String checkPara1 = checkPara(para);
        if(checkPara1!=null&&!checkPara1.equals("")){
            return checkPara1;
        }
        ReportParameter rpVo = getReportParameter2(para);
        List<UserVisitList> userVisitList =  visitLogService.getUserVisitList(rpVo);
        String json = getJson("0","查询成功",userVisitList);
        return json;
    }

    @RequestMapping(value = "/visitLog/getCompanyName",method = RequestMethod.POST)
    @ResponseBody
    public String getCompanyName(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception {
        ReportParameter rpVO = new ReportParameter();
        String userName = para.get("userName")==null?"":para.get("userName").toString();
        rpVO.setUserName(userName);
        List<Map<String,Object>> userVisitList =  visitLogService.getCompanyName(rpVO);
        String json = getJson("0","查询成功",userVisitList);
        return json;
    }

    @RequestMapping(value = "/visitLog/getUserBehavior",method = RequestMethod.POST)
    @ResponseBody
    public String getUserBehavior(HttpServletRequest request, @RequestBody Map<String,Object> para) throws JsonProcessingException {
        String checkPara1 = checkPara(para);
        if(checkPara1!=null&&!checkPara1.equals("")){
            return checkPara1;
        }
        String userCode = para.get("userCode")==null?"":para.get("userCode").toString();
        ReportParameter rpVo = getReportParameter(para);
        rpVo.setUserCode(userCode);
        List<UserBehavior> userVisitList =  visitLogService.getUserBehavior(rpVo);
        String json = getJson("0","查询成功",userVisitList);
        return json;
    }
    @RequestMapping(value = "/visitLog/getLogMonth",method = RequestMethod.POST)
    @ResponseBody
    public String getLogMonth(HttpServletRequest request, @RequestBody Map<String,Object> para)throws JsonProcessingException{

        String checkPara = checkPara(para);
        if(checkPara!=null&&!checkPara.equals("")){
            return checkPara;
        }
        String startDate = para.get("startDate")==null?"":para.get("startDate").toString();
        String endDate = para.get("endDate")==null?"":para.get("endDate").toString();
        String tStart = para.get("tStart")==null?"":para.get("tStart").toString();
        String zStart = para.get("zStart")==null?"":para.get("zStart").toString();
        String bStart = para.get("bStart")==null?"":para.get("bStart").toString();
        String qStart = para.get("qStart")==null?"":para.get("qStart").toString();
        ReportParameter rpVO = new ReportParameter();
        rpVO.setStartDate(startDate);
        rpVO.setEndDate(endDate);
        rpVO.setTStart(tStart);
        rpVO.setZStart(zStart);
        rpVO.setBStart(bStart);
        rpVO.setQStart(qStart);
        String json = visitLogService.getLogMonth(rpVO);
        return json;
    }

    @RequestMapping(value = "/visitLog/checkCompany",method = RequestMethod.POST)
    @ResponseBody
    public String checkCompany(HttpServletRequest request, @RequestBody Map<String,Object> para)throws Exception{

        String checkPara = checkName(para);
        if(checkPara!=null&&!checkPara.equals("")){
            return checkPara;
        }
        String name = para.get("name")==null?"":para.get("name").toString();
        String code = para.get("code")==null?"":para.get("code").toString();
        String pre = para.get("pre")==null?"":para.get("pre").toString();
        String scode= para.get("scode")==null?"":para.get("scode").toString();
        String zcode= para.get("zcode")==null?"":para.get("zcode").toString();

        ReportParameter rpVO = new ReportParameter();
        rpVO.setName(name);
        rpVO.setCode(code);
        rpVO.setPre(pre);
        rpVO.setScode(scode);
        rpVO.setZcode(zcode);
        String json = visitLogService.checkCompany(rpVO);
        return json;
    }

    public String getJson(String code,String msg,Object data) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code",code);
        hs.put("msg",msg);
        hs.put("data",data);
        return objectMapper.writeValueAsString(hs);
    }

    public ReportParameter getReportParameter1(Map<String,Object> para){
        ReportParameter rpVo = new ReportParameter();
        //处理日期，如果开始日期和结束日期一样，开始日期为当月第一天，结束日期为当月最后一天
        String startDate = para.get("startDate").toString();
        String endDate = para.get("endDate").toString();
        if(startDate.equals(endDate)){
            int year = Integer.valueOf(startDate.substring(0,4));
            int month = Integer.valueOf(startDate.substring(5,7));
            String lastDay = DateUtil.getLastDayOfMonth(year,month);
            startDate = startDate.substring(0,7)+"-01";
            endDate = lastDay;
            rpVo.setStartDate(startDate);
            rpVo.setEndDate(endDate);
        }else{
            rpVo.setStartDate(para.get("startDate").toString());
            rpVo.setEndDate(para.get("endDate").toString());
        }
        String companyName = para.get("companyName")==null?"":para.get("companyName").toString();
        //将公司转换成可以in 查询
        List<String> companyNameList = new ArrayList<String>();
        if(companyName!=null&&!companyName.equals("")){
            if(companyName!=null&&!companyName.equals("")){
                String[] companyNameArr =  companyName.split(",");
                for (int i = 0; i < companyNameArr.length; i++) {
                    companyNameList.add(companyNameArr[i]);
                }
            }
            String companyNames = SqlUtil.getOracleSQLIn(companyNameList,100,"c.companyname");
            companyNames = "("+companyNames+")";
            rpVo.setCompanyName(companyNames);
        }
//        rpVo.setCompanyName(companyName);
        return rpVo;
    }

    public ReportParameter getReportParameter(Map<String,Object> para){
        ReportParameter rpVo = new ReportParameter();
        //处理日期，如果开始日期和结束日期一样，开始日期为当月第一天，结束日期为当月最后一天
        String startDate = para.get("startDate").toString();
        String endDate = para.get("endDate").toString();
        rpVo.setStartDate(para.get("startDate").toString());
        rpVo.setEndDate(para.get("endDate").toString());
        String companyName = para.get("companyName")==null?"":para.get("companyName").toString();
        //将公司转换成可以in 查询
        List<String> companyNameList = new ArrayList<String>();
        if(companyName!=null&&!companyName.equals("")){
            if(companyName!=null&&!companyName.equals("")){
                String[] companyNameArr =  companyName.split(",");
                for (int i = 0; i < companyNameArr.length; i++) {
                    companyNameList.add(companyNameArr[i]);
                }
            }
            String companyNames = SqlUtil.getOracleSQLIn(companyNameList,100,"c.companyname");
            companyNames = "("+companyNames+")";
            rpVo.setCompanyName(companyNames);
        }
//        rpVo.setCompanyName(companyName);
        return rpVo;
    }
    public ReportParameter getReportParameter2(Map<String,Object> para){
        ReportParameter rpVo = new ReportParameter();
        //处理日期，如果开始日期和结束日期一样，开始日期为当月第一天，结束日期为当月最后一天
        String startDate = para.get("startDate").toString();
        String endDate = para.get("endDate").toString();
        rpVo.setStartDate(para.get("startDate").toString());
        rpVo.setEndDate(para.get("endDate").toString());
        String companyName = para.get("companyName")==null?"":para.get("companyName").toString();
        //将公司转换成可以in 查询
        List<String> companyNameList = new ArrayList<String>();
        if(companyName!=null&&!companyName.equals("")){
            if(companyName!=null&&!companyName.equals("")){
                String[] companyNameArr =  companyName.split(",");
                for (int i = 0; i < companyNameArr.length; i++) {
                    companyNameList.add(companyNameArr[i]);
                }
            }
            String companyNames = SqlUtil.getOracleSQLIn(companyNameList,100,"RES_ENAIL(B.COMPANY_NAME)");
            //companyNames = "("+companyNames+")";
            rpVo.setCompanyName(companyNames);
        }
//        rpVo.setCompanyName(companyName);
        return rpVo;
    }

    public String checkPara(Map<String,Object> para) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        StringBuilder errMsg = new StringBuilder();
        if(para.get("startDate")==null||para.get("startDate").equals("")){
            errMsg.append("开始日期不能为空");
        }
        if(para.get("endDate")==null||para.get("endDate").equals("")){
            errMsg.append("结束日期不能为空");
        }
        if(errMsg.toString().length()>0){
            String json = getJson("1",errMsg.toString(),null);
           return json;
        }
        return null;
    }

    public String checkName(Map<String,Object> para) throws JsonProcessingException {

        StringBuilder errMsg = new StringBuilder();
        if((para.get("code")==null||para.get("code").equals(""))&&(para.get("name")==null||para.get("name").equals(""))&&(para.get("pre")==null||para.get("pre").equals(""))){
            errMsg.append("公司名字和编码、前缀不能同时为空");
        }

        if(para.get("code")!=null&&!para.get("code").equals("")&&para.get("name")!=null&&!para.get("name").equals("")&&para.get("pre")!=null&&!para.get("pre").equals("")){
            errMsg.append("公司名字和编码、前缀不能同时校验");
        }

        if(errMsg.toString().length()>0){
            String json = getJson("1",errMsg.toString(),null);
            return json;
        }
        return null;
    }


}
