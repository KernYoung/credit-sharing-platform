package com.fanruan.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.*;
import com.fanruan.platform.mapper.CommonMapper;
import com.fanruan.platform.mapper.CommonsMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;



@Service
public class CommonService {

    @Autowired
    private TianYanChaJsonDao tianYanChaJsonDao;

    @Autowired
    private BlackListDao blackListDao;

    @Autowired
    private NationCodeDao nationCodeDao;

    @Autowired
    private PermissionPointDao permissionPointDao;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CommonsMapper commonsMapper;

    @Autowired
    private LogCreditOperDao logCreditOperDao;

    @Autowired
    private InitialScreeningOfMerchantsDao initialScreeningOfMerchantsDao;

    @Autowired
    private BlackInfoDao blackInfoDao;

    @Autowired
    private MessageInfoDao messageInfoDao;

    @Deprecated
    public List<BlackList> getBlackList() {//废弃
        java.util.Date date = DateUtils.addMonths(new java.util.Date(System.currentTimeMillis()), -1);
        return blackListDao.findAllByStatusAndStartDateAfter("2", new Date(date.getTime()));
    }

    //改进后的黑名单
    public List<BlackList> getBlackList(String userCode) {
        return commonsMapper.getBlackList(userCode);
    }

    public TianYanChaJson getLocalJson(String id, String name, String jsonFlag) {
        List<TianYanChaJson> jsonList = null;
        if (StringUtils.isNotBlank(id)) {
            jsonList = tianYanChaJsonDao.findByNameAndIdAndAndJsonFlagOrderByUpdateTimeDesc(id, name, jsonFlag);
        } else {
            jsonList = tianYanChaJsonDao.findByNameAndJsonFlagOrderByUpdateTimeDesc(name, jsonFlag);
        }
        if (CollectionUtils.isEmpty(jsonList)) {
            return null;
        }
        return jsonList.get(0);
    }

    public TianYanChaJson SaveLocalJson(String id, String name, String dataStr, String jsonFlag) {
        TianYanChaJson tianYanChaJson = new TianYanChaJson();
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        String reason = jsonObject.getString("reason");
        Integer errorCode = jsonObject.getInteger("error_code");
        tianYanChaJson.setId(id);
        tianYanChaJson.setName(name);
        tianYanChaJson.setJson(dataStr);
        tianYanChaJson.setErrorCode(String.valueOf(errorCode));
        tianYanChaJson.setReason(reason);
        tianYanChaJson.setJsonFlag(jsonFlag);
        tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return tianYanChaJsonDao.saveAndFlush(tianYanChaJson);
    }

    public List<NationCode> getAreaInfo() {
        return nationCodeDao.findAll();
    }

    public List<PermissionPoint> getPermissionRoles() {
        return commonMapper.getPermissionPoint();
    }

    public List<PermissionPoint> getPermissionPointList() {
        return permissionPointDao.findAll();
    }

    public PermissionPoint getPermissionPoint(Map<String, Object> param) {
        PermissionPoint permissionPoint = null;
        Integer id = CommonUtils.getIntegerValue((String) param.get("id"));
        String point = (String) param.get("permissionPoint");
        if (id != null) {
            Optional<PermissionPoint> allById = permissionPointDao.findById(id);
            if (allById.isPresent()) {
                permissionPoint = allById.get();
            }
        } else if (StringUtils.isNotBlank(point)) {
            Optional<PermissionPoint> permissionPointOptional = permissionPointDao.findByPermissionPoint(point);
            if (permissionPointOptional.isPresent()) {
                permissionPoint = permissionPointOptional.get();
            }
        }
        return permissionPoint;
    }

    public PermissionPoint savePermissionPoint(Integer id, Map<String, Object> param) {
        PermissionPoint point = new PermissionPoint();
        if (id != null) {
            Optional<PermissionPoint> allById = permissionPointDao.findAllById(id);
            if (allById.isPresent()) {
                point = allById.get();
            }
        }
        String permissionRole = (String) param.get("permissionRole");
        String permissionRoleName = (String) param.get("permissionRoleName");
        String permissionPoint = (String) param.get("permissionPoint");
        String permissionPointName = (String) param.get("permissionPointName");
        String userId = (String) param.get("userId");
        point.setPermissionPoint(permissionPoint);
        point.setPermissionPointName(permissionPointName);
        point.setPermissionRole(permissionRole);
        point.setPermissionRoleName(permissionRoleName);
        point.setUpdateBy(userId);
        point.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return permissionPointDao.saveAndFlush(point);
    }

    public List<PermissionPoint> getPermissionRolePoint(Map<String, Object> param) {
        List<PermissionPoint> permissionPointList = Lists.newArrayList();
        String permissionRole = (String) param.get("permissionRole");
        if (StringUtils.isNotBlank(permissionRole)) {
            permissionPointList = permissionPointDao.findAllByPermissionRole(permissionRole);
        }
        return permissionPointList;
    }

    public List<UserCompany> getUserCompany(String operator) {
        if (StringUtils.isBlank(operator)) {
            return null;
        }
        List<UserCompany> userCompany = null;
//         if(StringUtils.equals("admin",operator)){
//             userCompany = commonMapper.getAllUserCompany();
//         }else {
        userCompany = commonsMapper.getUserCompany(operator);
//         }
        return userCompany;
    }


    public boolean saveLog(Map<String, Object> param) {
        LogCreditOper logCreditOper = new LogCreditOper();
        String userName = (String) param.get("userName");
        String userCode = (String) param.get("userCode");
        String formPath = (String) param.get("formPath");
        String formPageName = (String) param.get("formPageName");
        String toPath = (String) param.get("toPath");
        String toPageName = (String) param.get("toPageName");
        String queryPara = (String) param.get("queryPara");
        logCreditOper.setUserName(userName);
        logCreditOper.setUserCode(userCode);
        logCreditOper.setFormPath(formPath);
        logCreditOper.setFormPageName(formPageName);
        logCreditOper.setToPath(toPath);
        logCreditOper.setToPageName(toPageName);
        logCreditOper.setQueryPara(queryPara);
        logCreditOper.setQDate(new java.util.Date());
        logCreditOperDao.saveAndFlush(logCreditOper);
        return true;
    }

    public HashMap<String, Object> searchApplyList(HashMap<String, Object> hs, Integer pageIndex, Integer pageSize, String zxbCode, String zxbCompanyName, String approveCode, String zxbInformant, String zxbApprover, Integer isSubAdmin, String operator) {
        Integer totalRecords = commonsMapper.getZxbApplyListCount(pageIndex, pageSize, zxbCode, zxbCompanyName, approveCode, zxbInformant, zxbApprover, isSubAdmin, operator);
        hs.put("totalRecords", totalRecords);
        hs.put("totalPages", Math.ceil(totalRecords / pageSize));
        List<ZhongXinBaoLog> applyList = commonsMapper.getZxbApplyList(hs, pageIndex, pageSize, zxbCode, zxbCompanyName, approveCode, zxbInformant, zxbApprover, isSubAdmin, operator);
        hs.put("applyList", applyList);
        hs.put("code", 0);
        return hs;
    }

    public void saveInitialScreeningOfMerchants(String serialid, List list, String updateBy) {
        List<InitialScreeningOfMerchants> newList = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) list.get(i);
            InitialScreeningOfMerchants info = new InitialScreeningOfMerchants();
            info.setFid(UUID.randomUUID().toString());
            info.setSerialid(serialid);
            info.setCustomSocialCode(map.get("customSocialCode"));
            info.setCustomName(map.get("customName"));
            info.setCustomCreditCode(map.get("customCreditCode"));
            info.setDunsCode(map.get("dunsCode"));
            info.setCustomExportCode(map.get("customExportCode"));
            info.setProvince(map.get("province"));
            info.setCity(map.get("city"));
            info.setNationType(map.get("nationType"));
            info.setCompanyType(map.get("companyType"));
            info.setUpdateBy(updateBy);
            info.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            newList.add(info);
        }
        initialScreeningOfMerchantsDao.saveAll(newList);
        initialScreeningOfMerchantsDao.flush();
    }

    public Map<String,List<List<String>>> getSearchFillInInfo(String userName) {
        Map<String, List<List<String>>> map = new HashMap<>();
        List<List<String>> allSerialid = new ArrayList<>();
        List<List<String>> allUpdataBy = new ArrayList<>();
        if("admin".equals(userName)){
            List<String> updataBy = commonsMapper.getAllUpdataBy();
            allUpdataBy.add(updataBy);
            for(String name : updataBy){
                List<String> list = commonsMapper.getAllSerialid(name);
                allSerialid.add(list);
            }
        }else{
            List<String> updataBy = new ArrayList<>();
            updataBy.add(userName);
            allUpdataBy.add(updataBy);
            allSerialid.add(commonsMapper.getAllSerialid(userName));
        }
        map.put("allUpdataBy",allUpdataBy);
        map.put("allSerialid",allSerialid);
        return map;
    }

    public HashMap<String,Object> getMerchantsViewResults(HashMap<String,Object> hs ,String updataBy,String serialid,Integer pageIndex, Integer pageSize){
        List<MerchantsViewResults> merchantsViewResults = commonsMapper.getMerchantsViewResults(updataBy, serialid,pageIndex,pageSize);
        List<MerchantsViewResults> excel = commonsMapper.getMerchantsViewExcel(updataBy, serialid,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getMerchantsViewResultsCount(updataBy, serialid,pageIndex,pageSize);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        hs.put("merchantsViewResults",merchantsViewResults);
        hs.put("excel", excel);
        hs.put("code",0);
        hs.put("msg", "数据更新成功");
        return  hs;
    }

    /***
     *  黑名单上申报  列表数据
     * @param hs
     * @param status
     * @param publishBy
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public HashMap<String,Object> getAllBlackListResultList(HashMap<String,Object> hs ,List<Integer> status,String publishBy,Integer pageIndex, Integer pageSize){
        List<BlacklistResultList> blackListResultList = commonsMapper.getAllBlackListResultList(status,publishBy,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getAllBlackListResultListCount(status, publishBy,pageIndex,pageSize);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        hs.put("blackListResultList",blackListResultList);
        hs.put("code",0);
        hs.put("msg", "数据更新成功");
        return  hs;
    }

    /***
     * 黑名单审批  列表数据
     * @param hs
     * @param status
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public HashMap<String,Object> getBlacklistApprovalList(HashMap<String,Object> hs,List<Integer> status,Integer pageIndex, Integer pageSize,String userName){
        List<BlacklistResultList> blackListResultList = commonsMapper.getBlacklistApprovalList(status,pageIndex,pageSize,userName);
        List<BlacklistResultList> excel = commonsMapper.getBlacklistApprovalExcel(status,pageIndex,pageSize,userName);

        Integer totalRecords = commonsMapper.getBlacklistApprovalListCount(status,pageIndex,pageSize,userName);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        hs.put("excel", excel);
        hs.put("blackListResultList",blackListResultList);
        hs.put("code",0);
        hs.put("msg", "数据更新成功");
        return hs;
    }


    public Optional<BlackInfo> getBlackInfo(String pid){
        return  blackInfoDao.findByPid(pid);
    }

    public String getLevelCompanyName(String CompanyName){
      return  commonsMapper.getLevelCompanyName(CompanyName);
    }

    public List<String> getApproveBy(String approveBy){
        return  commonsMapper.getApproveBy(approveBy);
    }

    public HashMap<String,Object> saveOrEdit(HashMap<String,Object> hs,Map<String,Object> param){
        String pid = (String)param.get("pid");
        String publishDept = (String)param.get("publishDept");
        String publishTime = (String)param.get("publishTime");
        String creditCode = (String)param.get("creditCode");
        String type = (String)param.get("type");
        String grade = (String)param.get("grade");
        String status = (String)param.get("status");
        String reason = (String)param.get("reason");
        String reasonUrl = (String)param.get("reasonUrl");
        String approveBy = (String)param.get("approveBy");
        String approveTime = (String)param.get("approveTime");
        String operationSelection = (String)param.get("operationSelection");
        String operationSelectionApproval = (String)param.get("operationSelectionApproval");
        String reasonsForRenewal = (String)param.get("reasonsForRenewal");
        String entName = (String)param.get("entName");
        Boolean isNew = (Boolean)param.get("isNew");
        String publishBy = (String)param.get("publishBy");
//        String startDate = (String)param.get("startDate");
        String cancelBy = (String)param.get("cancelBy");
        String cancelTime = (String)param.get("cancelTime");
        String cancelReason = (String)param.get("cancelReason");
        String approveRemarks = (String)param.get("approveRemarks");
        BlackInfo blackInfo = null;
        if(!isNew && StringUtils.isNotBlank(pid)){
            blackInfo = getBlackInfo(pid).get();
        }else{
            blackInfo = new BlackInfo();
            blackInfo.setPid(UUID.randomUUID().toString());
        }

        if(StringUtils.isNotBlank(pid)){
            blackInfo.setPid(pid);
        }

        if(StringUtils.isNotBlank(publishDept)){
            blackInfo.setPublishDept(publishDept);
        }

        if(StringUtils.isNotBlank(publishTime)){
            blackInfo.setPublishTime(Timestamp.valueOf(publishTime));
        }

        if(StringUtils.isNotBlank(creditCode)){
            blackInfo.setCode(creditCode);
        }

        if(StringUtils.isNotBlank(type)){
            blackInfo.setType(type);
        }

        if(StringUtils.isNotBlank(grade)){
            blackInfo.setGrade(grade);
        }

        if(StringUtils.isNotBlank(status)){   // 更新状态码
            //审核状态(0取消续期 1未审核 2已审核 3已驳回 4已撤回 5已过期 6取消过期)
            switch (status){
                case "取消续期":
                    blackInfo.setStatus("0");
                    break;
                case "未审核":
                case "未申请":
                    blackInfo.setStatus("1");
                    break;
                case "已审核":
                    blackInfo.setStatus("2");
                    break;
                case "已驳回":
                    blackInfo.setStatus("3");
                    break;
                case "已撤回":
                    blackInfo.setStatus("4");
                    break;
                case "已过期":
                    blackInfo.setStatus("5");
                    break;
                case "取消过期":
                    blackInfo.setStatus("6");
                    break;
            }
        }

        if(StringUtils.isNotBlank(reason)){
            blackInfo.setReason(reason);
        }

        if(StringUtils.isNotBlank(reasonUrl)){
            blackInfo.setReasonUrl(reasonUrl);
        }

        if(StringUtils.isNotBlank(approveBy)){
            blackInfo.setApproveBy(approveBy);
        }

        if(StringUtils.isNotBlank(approveTime)){
            blackInfo.setApproveTime(Timestamp.valueOf(approveTime));
        }

        if(StringUtils.isNotBlank(operationSelection)){  // 续期/取消续期 reasonsForRenewal 取消原因
            switch (operationSelection){
                case "续期":
                    blackInfo.setStatus("2");
                    blackInfo.setStartDate(new Timestamp(System.currentTimeMillis()));
                    break;
                case "取消续期":
                    blackInfo.setStatus("6");
                    if(StringUtils.isNotBlank(reasonsForRenewal)){
                        blackInfo.setReasonsForRenewal(reasonsForRenewal);
                    }
                    break;
            }
        }
        if(StringUtils.isNotBlank(cancelBy)){
            blackInfo.setCancelBy(cancelBy);
        }

        if(StringUtils.isNotBlank(cancelTime)){
            blackInfo.setCancelTime(Timestamp.valueOf(cancelTime));
        }
        if(StringUtils.isNotBlank(cancelBy) && StringUtils.isNotBlank(cancelTime)){
            blackInfo.setStatus("4");
        }
        if(StringUtils.isNotBlank(cancelReason)){
            blackInfo.setCancelReason(cancelReason);
        }
        if(StringUtils.isNotBlank(operationSelectionApproval)){   //审核 or 驳回
            switch (operationSelectionApproval){
                case "审核":
                    blackInfo.setStatus("2");
                    blackInfo.setStartDate(new Timestamp(System.currentTimeMillis()));
                    break;
                case "驳回":
                    blackInfo.setStatus("3");
                    break;
            }
        }
        if(StringUtils.isNotBlank(approveRemarks)){
            blackInfo.setApproveRemarks(approveRemarks);
        }
        if(StringUtils.isNotBlank(entName)){
            blackInfo.setEntName(entName);
        }

        if(StringUtils.isNotBlank(publishBy)){
           blackInfo.setPublishBy(publishBy);
           blackInfo.setUpdateBy(publishBy);
        }

        blackInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        String companyName = getLevelCompanyName(blackInfo.getPublishDept());
        blackInfo.setCompanyName(companyName);
        blackInfo.setPublishSdept(companyName);
        List<String> approveBy1 = getApproveBy(blackInfo.getPublishDept());
        blackInfoDao.saveAndFlush(blackInfo);
        if(isNew){  // 新增的黑名单申请存到消息表
            List<MessageInfo> messageInfos = new ArrayList<>();
            if(null != approveBy1 && approveBy1.size() > 0 ){
                for(int i = 0; i < approveBy1.size(); i++ ){
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setId(UUID.randomUUID().toString());
                    messageInfo.setUserName(approveBy1.get(i));
                    messageInfo.setPushFlag("0");
                    messageInfo.setPushType("黑名单通知");
                    messageInfo.setPromptinfo("您有新的黑名单企业需要审核，请及时查看!");
                    messageInfo.setPushTime(new Timestamp(System.currentTimeMillis()));
                    messageInfos.add(messageInfo);
                }
            }else{
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setId(UUID.randomUUID().toString());
                messageInfo.setUserName("admin");
                messageInfo.setPushFlag("0");
                messageInfo.setPushType("黑名单通知");
                messageInfo.setPromptinfo("您有新的黑名单企业需要审核，请及时查看!");
                messageInfo.setPushTime(new Timestamp(System.currentTimeMillis()));
                messageInfos.add(messageInfo);
            }
            hs.put("messageInfos", messageInfos);
            messageInfoDao.saveAll(messageInfos);
            messageInfoDao.flush();
        }
        hs.put("msg", "数据更新成功！");
        hs.put("blackInfo", blackInfo);
        return hs;
    }

    public List<String> getEventTypeList(String userName, List<String> riskleve, List<String> companyName, String startDate, String endDate) {
        return commonsMapper.getEventTypeList(userName,riskleve,companyName,startDate,endDate);
    }

    public HashMap<String,Object> getRealTimeWarning(HashMap<String,Object> hs,String userName, List<String> riskleve, List<String> companyName, List<String> eventType,String startDate, String endDate,Integer pageIndex, Integer pageSize) {
        List<RealTimeWarning> realTimeWarningList =  commonsMapper.getRealTimeWarning(userName,riskleve,companyName,eventType,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getRealTimeWarningCount(userName,riskleve,companyName,eventType,startDate,endDate,pageIndex,pageSize);
        hs.put("code", "0");
        hs.put("msg", "数据更新成功！");
        hs.put("realTimeWarningList", realTimeWarningList);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        return hs;
    }

    public HashMap<String, Object> getPlatformNews(HashMap<String, Object> hs, String userName, String startDate, String endDate, Integer pageIndex, Integer pageSize) {
        List<PlatformNews> platformNewsList =  commonsMapper.getPlatformNews(userName,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getPlatformNewsCount(userName,startDate,endDate,pageIndex,pageSize);
        hs.put("code", "0");
        hs.put("msg", "数据更新成功！");
        hs.put("platformNewsList", platformNewsList);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        return hs;
    }

    public HashMap<String, Object> getRiskMorningPost(HashMap<String, Object> hs, String userName, List<String> riskleve, List<String> companyName, String startDate, String endDate, Integer pageIndex, Integer pageSize) {
        List<RiskMorningPost> riskMorningPostList =  commonsMapper.getRiskMorningPost(userName,riskleve,companyName,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getRiskMorningPostCount(userName,riskleve,companyName,startDate,endDate,pageIndex,pageSize);
        for(int i = 0; i < riskMorningPostList.size(); i++){
            riskMorningPostList.get(i).setPushSource("中诚信");
        }
        hs.put("code", "0");
        hs.put("msg", "数据更新成功！");
        hs.put("riskMorningPostList", riskMorningPostList);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        return hs;
    }

    public HashMap<String, Object> getMorningNews(HashMap<String, Object> hs, String userName, List<String> riskleve, List<String> companyName, List<String> newsEmotion, String startDate, String endDate, Integer pageIndex, Integer pageSize) {
        List<MorningNews> morningNewsList =  commonsMapper.getMorningNews(userName,riskleve,companyName,newsEmotion,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getMorningNewsCount(userName,riskleve,companyName,newsEmotion,startDate,endDate,pageIndex,pageSize);
        for(int i = 0; i < morningNewsList.size(); i++){
            morningNewsList.get(i).setPushSource("中诚信");
        }
        hs.put("code", "0");
        hs.put("msg", "数据更新成功！");
        hs.put("morningNewsList", morningNewsList);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        return hs;
    }
    //jina
    public List<String> getReportApplyUserNameList(String userName){
        List<String> userNameList = new ArrayList<>();
        userNameList = commonsMapper.getReportApplyUserNameList(userName);
        return userNameList;
    }
    public Integer insertReportApply(String userName){
        Integer i = commonsMapper.insertReportApply(userName);
        return i;
    }

}
