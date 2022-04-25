package com.fanruan.platform.etl;

import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.*;
import com.fanruan.platform.mapper.CompanyReportMapper;
import com.fanruan.platform.service.WebSocket;
import com.fanruan.platform.util.CommonUtil;
import com.fanruan.platform.util.EmailSend;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class RiskPushTimer {

    public static List<Area> areaListCache;
    public static Map<String, List<String>> industryListCache;

    @Autowired
    private IndustryDao industryDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private BlackPushDao blackPushDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CompanyReportMapper companyReportMapper;

    @Autowired
    private ReportPushDao reportPushDao;

    /*
     *
     * DESC :  编写定时任务，每天凌晨1点执行一次
     *
     */
//        @Scheduled(cron = "*/10 * * * * ?")
////        @Scheduled(cron = "0 0/30 * * * ? ")
//    private void scheduleTask(){
//        //       TODO 定时查询库，如果有需要推送的数据则推送
//            zhongxinbaoTimer();
//            blackListTimer();
//
//        }

    private void blackListTimer() {
        List<BlackPush> blackPushList = blackPushDao.findAllByPushFlag("0");
        List<String> usernameList = CommonUtils.getUserNames(blackPushList);
//        //一次执行700笔
//        List<String> usernameList1 = new ArrayList<>();
//        for(int i = 0; i < usernameList.size(); i++){
//            if(i < 700){
//                usernameList1.add(usernameList.get(i));
//            }else{
//                break;
//            }
//        }

        List<User> userList = userDao.findAllByUsernameIn(usernameList);
        Map<String,String> userIdNames = CommonUtils.getUserIds(userList);
        for(BlackPush blackPush:blackPushList){
            String user = userIdNames.get(blackPush.getUserName());
            boolean sendFlag = webSocket.sendInfo(user, blackPush.getPromptInfo());
            if(sendFlag){
                blackPush.setPushFlag("1");
                blackPush.setPushTime(new Timestamp(System.currentTimeMillis()));
                blackPushDao.saveAndFlush(blackPush);
            }
        }
    }

//    private void blackListTimer() {
//        List<BlackPush> blackPushList = blackPushDao.findAllByPushFlag("0");
//        List<String> usernameList = CommonUtils.getUserNames(blackPushList);
//        List<User> userList = userDao.findAllByUsernameIn(usernameList);
//        Map<String,String> userIdNames = CommonUtils.getUserIds(userList);
//        for(BlackPush blackPush:blackPushList){
//            String user = userIdNames.get(blackPush.getUserName());
//            boolean sendFlag = webSocket.sendInfo(user, blackPush.getPromptInfo());
//            if(sendFlag){
//                blackPush.setPushFlag("1");
//                blackPush.setPushTime(new Timestamp(System.currentTimeMillis()));
//                blackPushDao.saveAndFlush(blackPush);
//            }
//        }
//    }

    private void zhongxinbaoTimer() {
        //这里发邮件通知报告已经到了，然后修改数据库中的邮件标识，站内信推送会挑选用户站内信标识是0但是邮件标识是1的，先发邮件后站内推送，邮件发送是最及时的
        List<ReportPush> reportPushes = companyReportMapper.selectNewReportPush();
        for(ReportPush reportPush:reportPushes){
            String corpSerialNo = reportPush.getCorpSerialNo();
            String email = reportPush.getEmail();
            if(StringUtils.isNotBlank(email)){
                sendEmail(email,"中信保报告审批消息","你好，你下单流水号为"+corpSerialNo+"的中信保报告生成成功，请登录资信共享平台查询下载！");
            }
            reportPush.setEmailFlag(1);
            reportPush.setEmailTime(new Timestamp(System.currentTimeMillis()));
            reportPushDao.saveAndFlush(reportPush);
        }
        List<ReportPush> reportPushList = reportPushDao.findAllByEmailFlagAndPushFlag(1, 0);
        for(ReportPush reportPush: reportPushList){
            String corpSerialNo = reportPush.getCorpSerialNo();
            Integer userId = reportPush.getUserId();
            boolean sendFlag = webSocket.sendInfo(String.valueOf(userId), "你好，你下单流水号为" + corpSerialNo + "的中信保报告生成成功，请下载查看！");
            if(sendFlag){
                reportPush.setPushFlag(1);
                reportPush.setPushTime(new Timestamp(System.currentTimeMillis()));
                reportPushDao.saveAndFlush(reportPush);
            }
        }
    }

    private boolean sendEmail(String emailAddress,String title,String content) {
        EmailEntity email = new EmailEntity();
//        email.setUserName("chenkanglin198904@163.com");
//        email.setPassword("EFFTMMYKZSHOOCSZ");
//        email.setHost("smtp.163.com");
        email.setUserName("liting@zibchina.com");
        email.setPassword("Ww0609..1");
        email.setHost("smtp.zibchina.com");
        email.setPort(25);
        email.setFromAddress("liting@zibchina.com");
//        email.setFromAddress("chenkanglin198904@163.com");
        email.setToAddress(emailAddress);
        email.setSubject(title);//标题
        email.setContext(content);//内容
        email.setContextType("text/html;charset=utf-8");
        return EmailSend.sendEmail(email);
    }

    @PostConstruct
    @Scheduled(cron = "0 0 0 * * ?")
    private void scheduleCodeCache(){
        List<Area> areaList = areaDao.findAll();
        Map<String,Area> provinceMap = Maps.newHashMap();
        Map<String,Area> cityMap = Maps.newHashMap();
        Map<String,Area> areaMap = Maps.newHashMap();
        for(Area area: areaList){
            if(StringUtils.equals(area.getAreaLevel(),"1")){
                provinceMap.put(area.getAreaCode(),area);
            }else if(StringUtils.equals(area.getAreaLevel(),"2")){
                cityMap.put(area.getAreaCode(),area);
            }else {
                areaMap.put(area.getAreaCode(),area);
            }
        }
        fillChildren(cityMap, areaMap);
        fillChildren(provinceMap, cityMap);
        areaListCache = Lists.newArrayList(provinceMap.values());

        List<Industry> industries = industryDao.findAll();
        Map<String,List<String>> result = Maps.newHashMap();
        for(Industry industry:industries){
            String industry1 = industry.getIndustry1();
            String industry2 = industry.getIndustry2();
            List<String> list = result.get(industry1);
            if(list==null){
                list = Lists.newArrayList();
                result.put(industry1,list);
            }
            if(!list.contains(industry2)){
                list.add(industry2);
            }
        }
        industryListCache = result;
    }
    private void fillChildren(Map<String, Area> cityMap, Map<String, Area> areaMap) {
        for(Area area: areaMap.values()){
            String areaScode = area.getAreaScode();
            Area cityArea = cityMap.get(areaScode);
            if(StringUtils.isBlank(areaScode)||cityArea==null){
                continue;
            }
            List<Area> children = cityArea.getChildren();
            if(children==null){
                children = Lists.newArrayList();
            }
            children.add(area);
            cityArea.setChildren(children);
        }
    }
}
