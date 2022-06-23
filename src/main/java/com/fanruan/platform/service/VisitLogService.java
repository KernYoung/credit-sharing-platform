package com.fanruan.platform.service;

import com.ctc.wstx.util.DataUtil;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.mapper.VisitLogMapper;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.SqlUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VisitLogService {

    @Autowired
    private VisitLogMapper visitLogMapper;

    /**
     * 折线图查询
     * @param rpVO
     * @return
     */
    public List<UserVisit> getUserVisit(ReportParameter rpVO){
        List<UserVisit> usreVisitList =  visitLogMapper.getUserVisit(rpVO);
        return usreVisitList;
    }

    public List<String> getCompanyList(Map<String,Object> rpVO){
        String userName = rpVO.get("userName")==null?"":rpVO.get("userName").toString();
        ReportParameter rp = new ReportParameter();
        rp.setUserName(userName);
        List<String> companyNames = new ArrayList<>();
        List list = visitLogMapper.getCompanyList(rp);
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> map = (Map<String,Object>)list.get(i);
                String name = map.get("NAME")==null?"":map.get("NAME").toString();
                if(!"".equals(name)){
                    companyNames.add(name);
                }
            }
        }
        return companyNames;
    }

    /**
     * 用户行为查询
     * @param rpVO
     * @return
     */
    public List<UserBehavior> getUserBehavior(ReportParameter rpVO){
        List<UserBehavior> usreVisitList =  visitLogMapper.getUserBehavior(rpVO);
        return usreVisitList;
    }

    public List<Map<String,Object>> getCompanyName(ReportParameter rpVO) throws Exception{
        return null;
    }

    /**
     * 访问明细查询
     * @param rpVO
     * @return
     */
    public List<UserVisitList> getUserVisitList(ReportParameter rpVO){
        List<UserVisitList> userVisitList = visitLogMapper.getUserVisitList(rpVO);

        if(userVisitList!=null&&userVisitList.size()>0){
            //查询启用用户
            List<Map<String,Object>> list = visitLogMapper.getUseUserCompanyName(rpVO);

            for (int i = 0; i < userVisitList.size(); i++) {
                String companyName = userVisitList.get(i).getCompanyName();
                //处理成员公司启用总数
                if(list!=null&&list.size()>0){
                    for (int j = 0; j < list.size(); j++) {
                        Map<String,Object> map = list.get(j);
                        if(map.get("COMPANYNAME")==null)
                            continue;
                        String queryCompanyName = map.get("COMPANYNAME").toString();
                        int userNumber = Integer.valueOf(map.get("USERNUMBER").toString());
                        if(companyName.equals(queryCompanyName)){
                            userVisitList.get(i).setUserNum(userNumber);
                        }
                    }
                }
            }
            //小计处理
            subNum(userVisitList,"visitNum","visitTotalNum");
            subNum(userVisitList,"visitPageNum","visitPageTotalNum");
        }
        return userVisitList;
    }

    /**
     * 根据成员公司小计访问次数
     */
    public void subNum(List<UserVisitList> userVisitList,String key,String totalKey){
        Map<String,Integer> map = new HashedMap();
        for (UserVisitList userVisit:
                userVisitList) {
            String name = userVisit.getCompanyName();
            Integer num = userVisit.getAttributeValue(key)==null?0:Integer.valueOf(userVisit.getAttributeValue(key).toString());
            if(map.containsKey(name)){
                Integer totalNum = map.get(name)+num;
                map.put(name,totalNum);
            }else{
                map.put(name,num);
            }
        }
        //遍历将成员公司汇总值存入
        for (UserVisitList userVisit:
                userVisitList) {
            String companyName = userVisit.getCompanyName();
            if(map.containsKey(companyName)){
                Integer num = map.get(companyName);
                userVisit.setAttributeValue(totalKey,num);
            }
        }
    }

    /**
     * 月报
     * @param rpVO
     * @return
     */
    public String getLogMonth(ReportParameter rpVO) throws JsonProcessingException {

        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        //查询累计用户活跃数
        rpVO.setFlag("1");
        List<LogMonthActive> sumActive = visitLogMapper.getLogMonthActive(rpVO);
        Map<String,Object> totalSumActive = subActive(sumActive);
        //累计用户使用数量
        rpVO.setFlag("1");
        List<LogMonthUse> sumUse = visitLogMapper.getLogMonthUse(rpVO);
        Map<String,Object> totalSumUse = subUse(sumUse);
        //当月用户活跃
        rpVO.setFlag("0");
        List<LogMonthActive> currentMonthActive = visitLogMapper.getLogMonthActive(rpVO);
        Map<String,Object> totalCurrentActive = subActive(currentMonthActive);
        //当月用户使用
        rpVO.setFlag("0");
        List<LogMonthUse> currentMonthUse = visitLogMapper.getLogMonthUse(rpVO);
        Map<String,Object> totalCurrentSumUse = subUse(currentMonthUse);

//        //累计用户使用数量
//        List<LogMonthUse> sumUse = visitLogMapper.getLogMonthUse(rpVO);
//        //当月用户活跃
//        List<LogMonthActive> currentMonthActive = visitLogMapper.getLogMonthActive(rpVO);
//        //当月用户使用
//        List<LogMonthUse> currentMonthUse = visitLogMapper.getLogMonthUse(rpVO);
        hs.put("code","0");
        hs.put("message","查询成功");
        hs.put("wholeTitle","总共启用12321");

        hs.put("sumActiveTitle","累计活跃");
        hs.put("sumActive",sumActive);
        hs.put("totalSumActive",totalSumActive);

        hs.put("totalSumActiveTitle","累计使用");
        hs.put("sumUse",sumUse);
        hs.put("totalSumUse",totalSumUse);

        hs.put("currentMonthActiveTitle","当月活跃");
        hs.put("currentMonthActive",currentMonthActive);
        hs.put("totalCurrentActive",totalCurrentActive);

        hs.put("currentMonthUseTitle","当月使用");
        hs.put("currentMonthUse",currentMonthUse);
        hs.put("totalCurrentSumUse",totalCurrentSumUse);

//       String json = getJson("1","查询成功",sumActive);
        return objectMapper.writeValueAsString(hs);

//        return null;
    }

    public Map<String,Object> subUse(List<LogMonthUse> monthUse){
        return null;
    }

    public Map<String,Object> subActive(List<LogMonthActive> activeList){
        Map<String,Object> map = new HashMap<>();
        if(activeList!=null&&activeList.size()>0){
            int userNumTotal = 0;
            int visitNumTotal = 0;
            int activeUserNumTotal = 0;
            for (LogMonthActive logMonthActive:
                    activeList) {
                userNumTotal = logMonthActive.getUserNum()+userNumTotal;
                visitNumTotal = logMonthActive.getVisitNum()+visitNumTotal;
                activeUserNumTotal = logMonthActive.getActiveUserNum()+activeUserNumTotal;
            }
            map.put("title","合计");
            map.put("userNum",userNumTotal);
            map.put("visitNum",visitNumTotal);
            map.put("activeUserNum",activeUserNumTotal);
            BigDecimal dfd = new BigDecimal(activeUserNumTotal);
            BigDecimal dfd1 = new BigDecimal(userNumTotal);

            BigDecimal activeUserRatio = userNumTotal==0?BigDecimal.ZERO:new BigDecimal(activeUserNumTotal).divide(new BigDecimal(userNumTotal),4,BigDecimal.ROUND_HALF_UP);
            BigDecimal acticeVisitRatio = activeUserNumTotal==0?BigDecimal.ZERO:new BigDecimal(visitNumTotal).divide(new BigDecimal(activeUserNumTotal),4,BigDecimal.ROUND_HALF_UP);
            map.put("activeUserRatio",activeUserRatio);
            map.put("acticeVisitRatio",acticeVisitRatio);
        }
        return map;
    }


    public String getJson(String code,String msg,Object data) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code",code);
        hs.put("msg",msg);
        hs.put("data",data);
        return objectMapper.writeValueAsString(hs);
    }


}
