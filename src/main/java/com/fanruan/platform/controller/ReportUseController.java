package com.fanruan.platform.controller;

import com.fanruan.platform.bean.ReportParameter;
import com.fanruan.platform.service.ReportUseService;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.SqlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping
@RestController
public class ReportUseController {

    @Autowired
    ReportUseService reportUseService;
    /**
     * 信保报告使用
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getXbReportUse",method = RequestMethod.POST)
    @ResponseBody
    public String getXbReportUse(HttpServletRequest request, @RequestBody Map<String,Object> para)  throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getXbReportUse(rpVO);
        return json;
    }

    /**
     * 处理参数
     * @return
     */
    public ReportParameter dealReportParameter(Map<String,Object> para,String key){
        ReportParameter rpVo = new ReportParameter();
        //处理日期，如果开始日期和结束日期一样，开始日期为当月第一天，结束日期为当月最后一天
        String startDate = para.get("startDate").toString();
        String endDate = para.get("endDate").toString();
//        if(startDate.equals(endDate)){
//            int year = Integer.valueOf(startDate.substring(0,4));
//            int month = Integer.valueOf(startDate.substring(5,7));
//            String lastDay = DateUtil.getLastDayOfMonth(year,month);
//            startDate = startDate.substring(0,7)+"-01";
//            endDate = lastDay;
//            rpVo.setStartDate(startDate);
//            rpVo.setEndDate(endDate);
//        }else{
            rpVo.setStartDate(para.get("startDate").toString());
            rpVo.setEndDate(para.get("endDate").toString());
//        }
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
            String companyNames = SqlUtil.getOracleSQLIn(companyNameList,100,key);
            companyNames = "("+companyNames+")";
            rpVo.setCompanyName(companyNames);
        }
        String userName = para.get("userName")==null?"":para.get("userName").toString();
        rpVo.setUserName(userName);
        String userCode = para.get("userCode")==null?"":para.get("userCode").toString();
        rpVo.setUserCode(userCode);
        String tStart = para.get("tStart")==null?"":para.get("tStart").toString();
        rpVo.setTStart(tStart);
        String zStart = para.get("zStart")==null?"":para.get("zStart").toString();
        rpVo.setZStart(zStart);
        String bStart = para.get("bStart")==null?"":para.get("bStart").toString();
        rpVo.setBStart(bStart);
        String qStart = para.get("qStart")==null?"":para.get("qStart").toString();
        rpVo.setQStart(qStart);
        String flag = para.get("flag")==null?"":para.get("flag").toString();
        rpVo.setFlag(flag);
        String source = para.get("source")==null?"":para.get("source").toString();
        rpVo.setSource(source);
        return rpVo;
    }

    /**
     * 信保报告使用记录
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getXbReportUseByCompany",method = RequestMethod.POST)
    @ResponseBody
    public String getXbReportUseList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getXbReportUseByCompany(rpVO);
        return json;
    }



    /**
     * 中诚信使用情况
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getZcxReportUse",method = RequestMethod.POST)
    @ResponseBody
    public String getZcxReportUse(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getZcxReportUse(rpVO);
        return json;
    }

    /**
     * 中诚信申请记录
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getZcxReportUseList",method = RequestMethod.POST)
    @ResponseBody
    public String getZcxReportUseList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getZcxReportUseList(rpVO);
        return json;
    }

    /**
     * 中中诚信添加监控情况
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getMonitoring",method = RequestMethod.POST)
    @ResponseBody
    public String getMonitoring(HttpServletRequest request, @RequestBody Map<String,Object> para)  throws Exception{
        //flag 0中诚信 1 天眼查
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getMonitoring(rpVO);
        return json;
    }

    /**
     * 天眼查使用情况
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getTycUse",method = RequestMethod.POST)
    @ResponseBody
    public String getTycUse(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getTycUse(rpVO);
        return json;
    }

    /**
     * 天眼查客商初筛调用明细
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getTycFilterCustomerList",method = RequestMethod.POST)
    @ResponseBody
    public String getTycFilterCustomerList(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"RES_ENAIL(B.COMPANY_NAME)");
        String json = reportUseService.getTycFilterCustomerList(rpVO);
        return json;
    }

    /**
     * 客商初筛调用次数
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getCustomFilter",method = RequestMethod.POST)
    @ResponseBody
    public String getCustomFilter(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"RES_ENAIL(B.COMPANY_NAME)");
        String json = reportUseService.getCustomFilter(rpVO);
        return json;
    }


    /**
     * 页面热度
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getPageActive",method = RequestMethod.POST)
    @ResponseBody
    public String getPageActive(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getPageActive(rpVO);
        return json;
    }

    /**
     * 模糊查询
     * @param request
     * @param para
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/report/getLikeQuery",method = RequestMethod.POST)
    @ResponseBody
    public String getLikeQuery(HttpServletRequest request, @RequestBody Map<String,Object> para) throws Exception{
        ReportParameter rpVO = new ReportParameter();
        rpVO = dealReportParameter(para,"c.companyName");
        String json = reportUseService.getLikeQuery(rpVO);
        return json;
    }
}
