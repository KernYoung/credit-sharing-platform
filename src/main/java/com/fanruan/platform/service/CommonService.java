package com.fanruan.platform.service;

import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.controller.CommonController;
import com.fanruan.platform.dao.*;
import com.fanruan.platform.mapper.CommonMapper;
import com.fanruan.platform.mapper.CommonsMapper;
import com.fanruan.platform.util.CommonUtil;
import com.fanruan.platform.util.DateUtil;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;



@Service
public class CommonService {

    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

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

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserService userService;

    @Deprecated
    public List<BlackList> getBlackList() {//废弃
        java.util.Date date = DateUtils.addMonths(new java.util.Date(System.currentTimeMillis()), -1);
        return blackListDao.findAllByStatusAndStartDateAfter("2", new Date(date.getTime()));
    }

    //改进后的黑名单
//    public List<BlackList> getBlackList(String userCode) {
//        return commonsMapper.getBlackList(userCode);
//    }
    //首页看黑名单
    public List<BlackListDetailResultList> getBlackList(Integer pageIndex,Integer pageSize,String sortCriteria){
        return commonsMapper.getBlackList(pageIndex,pageSize,sortCriteria);
    }
    public Integer getBlackListTotalCount(){
        return commonsMapper.getBlackListTotalCount();
    }

    //首页看灰名单
    public List<BlackListDetailResultList> getGreyList(Integer pageIndex,Integer pageSize,String sortCriteria){
        return commonsMapper.getGreyList(pageIndex,pageSize,sortCriteria);
    }
    public Integer getGreyListTotalCount(){
        return commonsMapper.getGreyListTotalCount();
    }

    public TianYanChaJson getLocalJson(String id, String name, String jsonFlag) {
        List<TianYanChaJson> jsonList = null;
        id = id == null ? "" : id;
//        jsonList = tianYanChaJsonDao.findByNameAndIdAndJsonFlagOrderByUpdateTimeDesc(name, id, jsonFlag);
        if (StringUtils.isNotBlank(id)) {
            jsonList = tianYanChaJsonDao.findByNameAndIdAndJsonFlagOrderByUpdateTimeDesc(name,id,jsonFlag);
        } else {
            jsonList = tianYanChaJsonDao.findByNameAndJsonFlagOrderByUpdateTimeDesc(name, jsonFlag);
        }
        if (CollectionUtils.isEmpty(jsonList)) {
            return null;
        }
        return jsonList.get(0);
    }

    public TianYanChaJson SaveLocalJsonWithoutDataStr(String companyName, String jsonFlag, String tokenId) {
        TianYanChaJson tianYanChaJson = new TianYanChaJson();
        tianYanChaJson.setId("");
        tianYanChaJson.setJson("");
        tianYanChaJson.setErrorCode("");
        tianYanChaJson.setReason("");
        tianYanChaJson.setName(companyName);
        tianYanChaJson.setJsonFlag(jsonFlag);
        tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        tianYanChaJson.setTokenId(tokenId);

        commonsMapper.insertTYCJson(tianYanChaJson);
        return tianYanChaJson;
    }

    public Integer SaveLocalJson(String name, String dataStr, String paramStr, String jsonFlag, String tokenId) {
        TianYanChaJson tianYanChaJson = new TianYanChaJson();
        if(!StringUtils.isBlank(dataStr)){
            JSONObject jsonObject = JSONObject.parseObject(dataStr);
            String reason = jsonObject.getString("reason");
            Integer errorCode = jsonObject.getInteger("error_code");

            if(jsonFlag.equals("1001") || jsonFlag.equals("818") || jsonFlag.equals("966")){
                if(jsonObject.getString("result") != null){
                    JSONObject result = JSONObject.parseObject(jsonObject.getString("result"));
                    String id = result.getString("id");
                    tianYanChaJson.setId(id);
                }

            }else{
                tianYanChaJson.setId("");
            }

            tianYanChaJson.setName(name);
            tianYanChaJson.setJson(dataStr);
            tianYanChaJson.setErrorCode(String.valueOf(errorCode));
            tianYanChaJson.setReason(reason);
            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            tianYanChaJson.setTokenId(tokenId);
            tianYanChaJson.setParamIn(paramStr);
        }else{
            tianYanChaJson.setId("");
            tianYanChaJson.setJson("");
            tianYanChaJson.setErrorCode("");
            tianYanChaJson.setReason("");
            tianYanChaJson.setName(name);
            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            tianYanChaJson.setTokenId(tokenId);
            tianYanChaJson.setParamIn(paramStr);
        }
        Integer insertFlag = commonsMapper.insertTYCJson(tianYanChaJson);
        return insertFlag;
    }

//    public TianYanChaJson SaveLocalJson1(String dataStr, String jsonFlag, String tokenId) {
//        String id = "";
//        String name = "";
//        TianYanChaJson tianYanChaJson = new TianYanChaJson();
//        JSONObject jsonObject = JSONObject.parseObject(dataStr);
//        if(jsonObject.getString("result") != null){
//            JSONObject result = JSONObject.parseObject(jsonObject.getString("result"));
//            id = result.getString("id");
//            name = result.getString("name");
//        }
//
//        String reason = jsonObject.getString("reason");
//        Integer errorCode = jsonObject.getInteger("error_code");
//
//        tianYanChaJson.setId(id);
//        tianYanChaJson.setName(name);
//        tianYanChaJson.setJson(dataStr);
//        tianYanChaJson.setErrorCode(String.valueOf(errorCode));
//        tianYanChaJson.setReason(reason);
//        tianYanChaJson.setJsonFlag(jsonFlag);
//        tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//        tianYanChaJson.setTokenId(tokenId);
//
//        commonsMapper.insertTYCJson(tianYanChaJson);
//        return tianYanChaJson;
//    }

//    public TianYanChaJson SaveLocalJsonWithoutId(String name, String dataStr, String jsonFlag, String tokenId) {
//        TianYanChaJson tianYanChaJson = new TianYanChaJson();
//
//        if(!StringUtils.isBlank(dataStr)){
//            JSONObject jsonObject = JSONObject.parseObject(dataStr);
//            String reason = jsonObject.getString("reason");
//            Integer errorCode = jsonObject.getInteger("error_code");
//            tianYanChaJson.setId("");
//            tianYanChaJson.setName(name);
//            tianYanChaJson.setJson(dataStr);
//            tianYanChaJson.setErrorCode(String.valueOf(errorCode));
//            tianYanChaJson.setReason(reason);
//            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            tianYanChaJson.setTokenId(tokenId);
//        }else{
//            tianYanChaJson.setId("");
//            tianYanChaJson.setJson("");
//            tianYanChaJson.setErrorCode("");
//            tianYanChaJson.setReason("");
//            tianYanChaJson.setName(name);
//            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            tianYanChaJson.setTokenId(tokenId);
//        }
//        commonsMapper.insertTYCJson(tianYanChaJson);
//        return tianYanChaJson;
//    }

//    public TianYanChaJson SaveLocalJsonWithoutIdTest4Param(String name, String dataStr, String paramStr, String jsonFlag, String tokenId) {
//
//        TianYanChaJson tianYanChaJson = new TianYanChaJson();
//
//        if(!StringUtils.isBlank(dataStr)){
//            JSONObject jsonObject = JSONObject.parseObject(dataStr);
//            String reason = jsonObject.getString("reason");
//            Integer errorCode = jsonObject.getInteger("error_code");
//            tianYanChaJson.setId("");
//            tianYanChaJson.setName(name);
//            tianYanChaJson.setJson(dataStr);
//            tianYanChaJson.setErrorCode(String.valueOf(errorCode));
//            tianYanChaJson.setReason(reason);
//            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            tianYanChaJson.setTokenId(tokenId);
//            tianYanChaJson.setParamIn(paramStr);
//        }else{
//            tianYanChaJson.setId("");
//            tianYanChaJson.setJson("");
//            tianYanChaJson.setErrorCode("");
//            tianYanChaJson.setReason("");
//            tianYanChaJson.setName(name);
//            tianYanChaJson.setJsonFlag(jsonFlag);
//            tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//            tianYanChaJson.setTokenId(tokenId);
//            tianYanChaJson.setParamIn(paramStr);
//        }
//        commonsMapper.insertTYCJsonWithParam(tianYanChaJson);
//        return tianYanChaJson;
//    }

    //    public TianYanChaJson SaveLocalJson(String id, String name, String dataStr, String jsonFlag) {
//        TianYanChaJson tianYanChaJson = new TianYanChaJson();
//        JSONObject jsonObject = JSONObject.parseObject(dataStr);
//        String reason = jsonObject.getString("reason");
//        Integer errorCode = jsonObject.getInteger("error_code");
//        tianYanChaJson.setId(id);
//        tianYanChaJson.setName(name);
//        tianYanChaJson.setJson(dataStr);
//        tianYanChaJson.setErrorCode(String.valueOf(errorCode));
//        tianYanChaJson.setReason(reason);
//        tianYanChaJson.setJsonFlag(jsonFlag);
//        tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//
//        commonsMapper.insertTYCJson(tianYanChaJson);
//        return tianYanChaJson;
//    }

//    public TianYanChaJson UpdateTYCJson(String id, String name, String dataStr, String jsonFlag) {
//        TianYanChaJson tianYanChaJson = new TianYanChaJson();
//        JSONObject jsonObject = JSONObject.parseObject(dataStr);
//        String reason = jsonObject.getString("reason");
//        Integer errorCode = jsonObject.getInteger("error_code");
//        tianYanChaJson.setId(id);
//        tianYanChaJson.setName(name);
//        tianYanChaJson.setJson(dataStr);
//        tianYanChaJson.setErrorCode(String.valueOf(errorCode));
//        tianYanChaJson.setReason(reason);
//        tianYanChaJson.setJsonFlag(jsonFlag);
//        tianYanChaJson.setUpdateTime(new Timestamp(System.currentTimeMillis()));
//
//        if (StringUtils.isNotBlank(id)) {
//            commonsMapper.updateTYCJson(tianYanChaJson);
//        } else {
//            commonsMapper.updateTYCJsonByIdBlank(tianYanChaJson);
//        }
//
//        return tianYanChaJson;
//
//    }

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

//    public List<String> getUserRole() {
//
//        List<String> userRole = null;
////         if(StringUtils.equals("admin",operator)){
////             userCompany = commonMapper.getAllUserCompany();
////         }else {
//        userRole = commonMapper.getUserRole();
////         }
//        return userRole;
//    }


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
        for(ZhongXinBaoLog item : applyList){
            String speed = item.getSpeed() == null ? "" : item.getSpeed();
            switch (speed){
                case "1":
                    item.setSpeed("普通");
                    break;
                case "2":
                    item.setSpeed("加急");
                    break;
                case "3":
                    item.setSpeed("特急");
                    break;
                default:
                    break;
            }
        }

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
       /* hs.put("msg", "数据更新成功");*/
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
       /* hs.put("msg", "数据更新成功");*/
        return  hs;
    }

    /***
     * 黑名单审批  列表数据
     * @param hs
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public HashMap<String,Object> getBlackListDetailList(HashMap<String,Object> hs,Integer pageIndex, Integer pageSize, String companyName, String dataSource, String startDate, String endDate, String userCode){
        List<BlackListDetailResultList> blackListDetailResultList = commonsMapper.getBlackListDetailList(pageIndex, pageSize, companyName, dataSource, startDate, endDate, userCode);
        Integer totalRecords = commonsMapper.getBlackListDetailListCount(pageIndex, pageSize, companyName, dataSource, startDate, endDate, userCode);

        if(pageIndex != null && pageSize != null){
            hs.put("totalRecords",totalRecords);
            hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        }

        hs.put("blackListDetailResultList",blackListDetailResultList);
        hs.put("code",0);
        return hs;
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
        /* hs.put("msg", "数据更新成功");*/
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
        List<BlackInfo> infoList = null;
        BlackInfo info = null;
        boolean isExist = false;
        if(!isNew && StringUtils.isNotBlank(pid)){
            blackInfo = getBlackInfo(pid).get();
        }else{
            if("被驳回".equals(status) || "已撤销".equals(status)){
                //根据用户名和公司名找到数据库中的数据做修改
                String nowSecondaryDepartment = commonsMapper.getSecondaryDepartmentByLoginName(publishBy);
                //根据公司名称在表中查是否有该条申请记录的申请人的二级单位
                 infoList = blackInfoDao.findByEntName(entName);
              //  info = blackInfoDao.findByEntNameAndCodeAndPublishBy(entName,creditCode, publishBy);
                for(BlackInfo oldBlackInfo:infoList) {
                    String oldSecondaryDepartment = commonsMapper.getSecondaryDepartmentByLoginName(oldBlackInfo.getPublishBy());
                    if (oldSecondaryDepartment != null && oldSecondaryDepartment.equals(nowSecondaryDepartment)) {
                        //说明已经被同一二级公司的人申请过了
                        blackInfo = oldBlackInfo;
                        info = oldBlackInfo;
                        isExist = true;
                    }
                }
            }else {
                blackInfo = new BlackInfo();
                blackInfo.setPid(UUID.randomUUID().toString());
            }

            if("已撤销".equals(status)){
                blackInfo.setCancelBy("");
            }

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
        if(info != null){
            blackInfo.setStatus("1");
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
        if(isExist){
            blackInfo.setApproveBy(null);
            blackInfo.setApproveTime(null);
            blackInfo.setApproveRemarks(null);
            blackInfo.setStartDate(null);
        }
        if(StringUtils.isNotBlank(entName)){
            blackInfo.setEntName(entName);
        }

        if(StringUtils.isNotBlank(publishBy)){
            if(operationSelectionApproval == null && cancelReason ==null){//审核和撤销不更改信息
                blackInfo.setPublishBy(publishBy);
            }
           blackInfo.setUpdateBy(publishBy);
        }

        blackInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        String companyName = getLevelCompanyName(blackInfo.getPublishDept());
        if(operationSelectionApproval == null && cancelReason ==null){
            blackInfo.setCompanyName(companyName);
        }

        blackInfo.setPublishSdept(companyName);
        List<String> approveBy1 = getApproveBy(blackInfo.getPublishDept());

        //门户申报的黑名单数据新增天眼查关联id Kern on 20220330
//        if(blackInfo.getTycCompanyId() == null){
//            Long tycCompanyId = getCompanyId(blackInfo.getCompanyName(), blackInfo.getCode(), blackInfo.getPublishBy());
//            blackInfo.setTycCompanyId(tycCompanyId);
//        }
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

//    private Long getCompanyId(String companyName, String code, String userName) {
//        Long companyId = -1L;
//        Company company = null;
//        String dataStr = "";
//        User user =  userService.getUserByUsername(userName);
//        if(code != null && !code.isEmpty()){
//            company = companyService.findCompanyByCode(code);
//        }else{
//            company = companyService.findCompanyByName(companyName);
//        }
//
//        if(company == null){
//            Map<String,Object> resultMap = new HashMap<>();
//            resultMap = companyService.creditCompanyNew(companyName, user.getUserId());
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
//
//            String companyCode = user == null ? "" : user.getCompanyCode();
//            String tokenId = companyService.getTokenIdByCompanyCode(companyCode,"getBaseInfo");
//            if(StringUtils.isBlank(tokenId)) log.info("获取tokenId失败，userName: " + userName + ", jsonFlag: 1001");
//
//            if(StringUtils.isBlank(dataStr)){
//                SaveLocalJsonWithoutDataStr(companyName,jsonFlag,tokenId);
//            }else{
//                SaveLocalJson1(dataStr,jsonFlag,tokenId);
//            }
//        }
//
//        companyId = company.getId();
//
//        return companyId;
//    }

    public List<String> getEventTypeList(String userName, List<String> riskleve, List<String> companyName, String startDate, String endDate) {
        return commonsMapper.getEventTypeList(userName,riskleve,companyName,startDate,endDate);
    }

    public HashMap<String,Object> getRealTimeWarning(HashMap<String,Object> hs,String userName, List<String> riskleve, List<String> companyName, List<String> eventType,String startDate, String endDate,Integer pageIndex, Integer pageSize) {
        List<RealTimeWarning> realTimeWarningList =  commonsMapper.getRealTimeWarning(userName,riskleve,companyName,eventType,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getRealTimeWarningCount(userName,riskleve,companyName,eventType,startDate,endDate,pageIndex,pageSize);
        hs.put("code", "0");
        /*hs.put("msg", "数据更新成功！");*/
        hs.put("realTimeWarningList", realTimeWarningList);
        hs.put("totalRecords",totalRecords);
        hs.put("totalPages",Math.ceil(totalRecords/pageSize));
        return hs;
    }

    public HashMap<String, Object> getPlatformNews(HashMap<String, Object> hs, String userName, String startDate, String endDate, Integer pageIndex, Integer pageSize) {
        List<PlatformNews> platformNewsList =  commonsMapper.getPlatformNews(userName,startDate,endDate,pageIndex,pageSize);
        Integer totalRecords = commonsMapper.getPlatformNewsCount(userName,startDate,endDate,pageIndex,pageSize);
        hs.put("code", "0");
        /*hs.put("msg", "数据更新成功！");*/
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
       /* hs.put("msg", "数据更新成功！");*/
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
       /* hs.put("msg", "数据更新成功！");*/
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
    public Integer reportbuyerNoIsExist(String reportbuyerNo){
        Integer i =  commonsMapper.reportbuyerNoIsExist(reportbuyerNo);
        return  i;
    }

    public List<ZhongXinBaoLog> reportbuyerNoIsExist4Apply(String reportbuyerNo){
        List<ZhongXinBaoLog> zhongXinBaoLog =  commonsMapper.reportbuyerNoIsExist4Apply(reportbuyerNo);
        return  zhongXinBaoLog;
    }

    public String getXBPDFFileName(String reportBuyerNo) {
        return commonsMapper.getXBPDFFileName(reportBuyerNo);
    }

    public String getXBPDFFileUpdateTime(String reportBuyerNo) {
        return commonsMapper.getXBPDFFileUpdateTime(reportBuyerNo);
    }

    public Integer getUserId(String userName) {
        return CommonUtils.getIntegerValue(commonsMapper.getUserId(userName));
    }

    public List<InputPush> getApplyInfo(String userName) {
        return commonsMapper.getApplyInfo(userName);
    }

    public Integer insertZXBApplyInfo4ETL(InputPush inputPush){
        Integer i = commonsMapper.insertZXBApplyInfo4ETL(inputPush);
        return i;
    }

    public String getOrderState(String corpSerialNo) {
        return commonsMapper.getOrderState(corpSerialNo);
    }

    public String getNoticeSerialNo(String corpSerialNo) {
        return commonsMapper.getNoticeSerialNo(corpSerialNo);
    }



    public Integer addPAFCUserInfo(PAFCUser pafcUser){
        Integer i = commonsMapper.insertPAFCUser(pafcUser);
        return i;
    }
    public Integer updatePAFCUserInfoById(PAFCUser pafcUser){
        Integer i = commonsMapper.updatePAFCUserById(pafcUser);
        return i;
    }

    public Integer updatePAFCCompany(PAFCCompany pafcCompany){
        Integer i = commonsMapper.updatePAFCCompany(pafcCompany);
        return i;
    }

    public Integer deletePAFCCompany(PAFCCompany pafcCompany){
        Integer i = commonsMapper.deletePAFCCompany(pafcCompany);
        return i;
    }

    public Integer insertPAFCCompany(PAFCCompany pafcCompany){
        Integer i = commonsMapper.insertPAFCCompany(pafcCompany);
        return i;
    }


    public Integer deletePAFCUserInfoById(Integer id){
        Integer i = commonsMapper.deletePAFCUserById(id);
        return i;
    }
    public Integer savePAFCUser(PAFCUser pafcUser){
        Integer i = commonsMapper.insertPAFCUser(pafcUser);
        return i;
    }

//    public Integer savePAFCCompany(PAFCUser pafcUser){
//        Integer i = commonsMapper.insertPAFCCompany(pafcUser);
//        return i;
//    }
//    public Integer savePAFCDistributeInfo(PAFCUser pafcUser){
//        Integer i = commonsMapper.insertPAFCDistributeInfo(pafcUser);
//        return i;
//    }


    public Integer updatePAFCVersion(PAFCVersion pafcVersion){
        Integer i = commonsMapper.updatePAFCVersion(pafcVersion);
        return i;
    }

    public Integer insertPAFCVersion(PAFCVersion pafcVersion){
        Integer i = commonsMapper.insertPAFCVersion(pafcVersion);
        return i;
    }

    public Integer insertPAFCPoints(PAFCPoints pafcPoints){
        Integer i = commonsMapper.insertPAFCPoints(pafcPoints);
        return i;
    }


    public Integer updatePAFCPoints(PAFCPoints pafcPoints){
        Integer i = commonsMapper.updatePAFCPoints(pafcPoints);
        return i;
    }

    public List<PAFCVersion> getPAFCVersionList(){
        return commonsMapper.getPAFCVersionList();
    }

    public List<CompanyLevel> getLV2Company(){
        return commonsMapper.getLV2Company();
    }


    public PAFCVersion getVersionPointsTYC(PAFCVersion pafcVersion){
        return commonsMapper.getVersionPointsTYC(pafcVersion);
    }

    public PAFCVersion getVersionPointsZCX(PAFCVersion pafcVersion){
        return commonsMapper.getVersionPointsZCX(pafcVersion);
    }

    public PAFCVersion getPAFCVersion(String versionNo){
        return commonsMapper.getPAFCVersion(versionNo);
    }


    public List<PAFCPoints> getPAFCPointsList(String versionNo){
        return commonsMapper.getPAFCPointsList(versionNo);
    }

    public List<PAFCPoints> getCompanyPoints(PAFCVersion versionNo){
        return commonsMapper.getCompanyPoints(versionNo);
    }

    public List<PAFCUser> getPAFCUserList(){
        return commonsMapper.getPAFCUserList();
    }

    public List<PAFCUser> getPAFCUserListByCompanyCode(String companyCode){
        return commonsMapper.getPAFCUserListByCompanyCode(companyCode);
    }


    public List<PAFCCompany> getPAFCCompanyList(){
        return commonsMapper.getPAFCCompanyList();
    }

    public List<PAFCCompany> getRecipientInfoList(){
        List<PAFCCompany>  pafcCompanyList = new ArrayList<>();
        List<PAFCUser>  pafcUser = new ArrayList<>();

        pafcCompanyList = getPAFCCompanyList();
        pafcUser = getPAFCUserList();

        String usernameStr;
        String emailStr;
        String idStr;
        String companyCodeC;
        String username;
        String email;
        String id;
        String companyCodeU;

        for(PAFCCompany company : pafcCompanyList){
            usernameStr = "";
            emailStr = "";
            idStr = "";
            companyCodeC = "";
            companyCodeC = company.getCompanyCode();
            for(PAFCUser user : pafcUser){
                username = "";
                email = "";
                id = "";
                companyCodeU = "";
                username = user.getUserName();
                email = user.getEmail();
                id = user.getId().toString();
                companyCodeU = user.getCompanyCode();
                if(companyCodeC.equals(companyCodeU)){
                    usernameStr += username + "; ";
                    emailStr += email + "; ";
                    idStr += id + "; ";
                }
            }
            if(!usernameStr.isEmpty()) company.setUserNameStr(usernameStr.substring(0,usernameStr.lastIndexOf("; ")));
            if(!emailStr.isEmpty()) company.setEmailStr(emailStr.substring(0,emailStr.lastIndexOf("; ")));
            if(!idStr.isEmpty()) company.setIdStr(idStr.substring(0,idStr.lastIndexOf("; ")));
        }

        return pafcCompanyList;
    }


    public Integer getIsOverUsage(String jsonFlag, String companyName) {
        Integer isOverUsageFlag = 0;
        int limitNumber = -1;
        int usedNumber = -2;

        limitNumber = commonsMapper.getInterfaceLimitNumber(jsonFlag, companyName) == null ? -1 : commonsMapper.getInterfaceLimitNumber(jsonFlag, companyName);
        usedNumber = commonsMapper.getInterfaceUsedNumber(jsonFlag, companyName) == null ? 0 : commonsMapper.getInterfaceUsedNumber(jsonFlag, companyName);

        if(limitNumber == -1) return -1;
        if(usedNumber == -2) return -2;

        if(usedNumber >= limitNumber) isOverUsageFlag = 1;

        return isOverUsageFlag;
    }


    public String getCompanyNamebyTokenId(String tokenId) {
        if(tokenId == null || tokenId.length() < 3) return "";

        String companyName = "";
        String companyTokenId = tokenId.substring(0,3);

        companyName = commonsMapper.getCompanyNameByTokenId(companyTokenId);

        return companyName;
    }


    /**
     * 根据当前的请求参数匹配是否有当天的数据
     * @param paramMap
     * @param jsonFlag
     * @return
     */
    public TianYanChaJson getTianYanChaJsonHistory(Map<String, String> paramMap, String jsonFlag) {
        TianYanChaJson tianYanChaJson = null;
        List<TianYanChaJson> tianYanChaJsonsList;
        String paramStrHistory;
        Map<String, Object> paramMapHistory;

        String keyword = "";
        if(paramMap.get("keyword") != null) keyword = paramMap.get("keyword");
        if(paramMap.get("word") != null) keyword = paramMap.get("word");
        tianYanChaJsonsList = commonsMapper.getValidRequest4TheDay(keyword, jsonFlag);
        if(tianYanChaJsonsList == null) return null;
        for(TianYanChaJson tycItem : tianYanChaJsonsList){
            if(!tycItem.getErrorCode().equals("0")) continue;
            paramStrHistory = tycItem.getParamIn();
            if(paramStrHistory != null && !paramStrHistory.isEmpty()){
                paramMapHistory = JSONObject.parseObject(paramStrHistory);
                if(paramMapHistory.size() != paramMap.size()) continue;
                boolean isMatchSingleKey;
                int isMatchNumber = 0;

                for(String paramKeyHistory : paramMapHistory.keySet()){
                    isMatchSingleKey = false;
                    for(String paramKeyCurrent : paramMap.keySet()){
                        if(paramKeyHistory.equals(paramKeyCurrent)){
                            if(paramMapHistory.get(paramKeyHistory).equals(paramMap.get(paramKeyCurrent))){
                                isMatchSingleKey = true;
                                isMatchNumber++;
                                break;
                            }
                        }
                    }
                    if(isMatchSingleKey == false) break;
                }

                if(isMatchNumber == paramMapHistory.size()){
                    tianYanChaJson = tycItem;
                    break;
                }
            }
        }
        return tianYanChaJson;
    }

    public String getDataOutputAndInput2DB(String requestUrl, String jsonFlag, String tokenId, Map<String, String> paramMap, String companyName) {
        String dataOutput;
        String keyword = "";
        if(paramMap.get("keyword") != null) keyword = paramMap.get("keyword");
        if(paramMap.get("word") != null) keyword = paramMap.get("word");

        TianYanChaJson tianYanChaJson = getTianYanChaJsonHistory(paramMap,jsonFlag);

        String paramStr = JSONObject.toJSONString(paramMap);
        String dataInput;

        if(tianYanChaJson == null){
            Integer isOverUsage = getIsOverUsage(jsonFlag, companyName);
            if(isOverUsage == 1){
                return "失败，调用次数已超当日上限！jsonFlag: " + jsonFlag + ",companyName: " + companyName;
            }else if(isOverUsage == -1){
                return "失败，未能获取到接口限制数量！jsonFlag: " + jsonFlag + ",companyName: " + companyName;
            }else if(isOverUsage == -2){
                return "失败，未能获取到接口已使用数量！jsonFlag: " + jsonFlag + ",companyName: " + companyName;
            }

            dataInput = requestTianYanChaAPI(paramMap,requestUrl);
            log.info("request result: " + dataInput);
            dataOutput = dataInput;
        }else{
            dataInput = "";
            dataOutput = tianYanChaJson.getJson();
        }

        Integer insertFlag = SaveLocalJson(keyword, dataInput, paramStr, jsonFlag, tokenId);
        if(insertFlag != 1) log.error("Insert function failed! tokenId: " + tokenId + ",keyword: " + keyword + ",jsonFlag: " + jsonFlag);

        return dataOutput;
    }

    private String requestTianYanChaAPI(Map<String,String> paramMap,String uri) {
//        String md5Hex = DigestUtils.md5Hex(CommonUtil.TIAN_YAN_CHA_USERNAME+CommonUtil.TIAN_YAN_CHA_KEY);
        StringBuffer sb = new StringBuffer(CommonUtil.TIAN_YAN_CHA+uri);
//        sb.append("username="+CommonUtil.TIAN_YAN_CHA_USERNAME)
//                .append("&authId="+CommonUtil.TIAN_YAN_CHA_AUTHID)
//                .append("&sign="+md5Hex);
        for(Map.Entry<String,String> kv:paramMap.entrySet()){
            String key = kv.getKey();
            String value = kv.getValue();
            sb.append("&").append(key).append("=").append(value);
        }

//        .append("&name="+company.getCompanyName())
        HttpGet get = new HttpGet(sb.toString());
        log.info("request str: " + get);
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

//    private String requestTianYanChaAPI(Map<String,String> paramMap,String uri) {
//        String md5Hex = DigestUtils.md5Hex(CommonUtil.TIAN_YAN_CHA_USERNAME+CommonUtil.TIAN_YAN_CHA_KEY);
//        StringBuffer sb = new StringBuffer(CommonUtil.TIAN_YAN_CHA+uri);
//        sb.append("username="+CommonUtil.TIAN_YAN_CHA_USERNAME)
//                .append("&authId="+CommonUtil.TIAN_YAN_CHA_AUTHID)
//                .append("&sign="+md5Hex);
//        for(Map.Entry<String,String> kv:paramMap.entrySet()){
//            String key = kv.getKey();
//            String value = kv.getValue();
//            sb.append("&").append(key).append("=").append(value);
//        }
////        .append("&name="+company.getCompanyName())
//        HttpGet get = new HttpGet(sb.toString());
//        get.setHeader("Authorization", CommonUtil.TIAN_YAN_CHA_AUTH);
//        HttpClient client = new DefaultHttpClient();
//        HttpResponse rese = null;
//        String dataStr = null;
//        try {
//            rese = client.execute(get);
//            dataStr = EntityUtils.toString(rese.getEntity());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return dataStr;
//    }

    public List<OpenAPI> getTokenList(String companyName, String uri, String interfaceName, Integer status, Integer pageIndex, Integer pageSize) {
        return commonsMapper.getTokenList(companyName, uri, interfaceName, status, pageIndex, pageSize);
    }


    public Integer getTokenListTotalCount(String companyName, String uri, String interfaceName, Integer status) {
        return commonsMapper.getTokenListTotalCount(companyName, uri, interfaceName, status);
    }

    public Integer updateOpenAPIStatus(String tokenId, Integer openId) {
        return commonsMapper.updateOpenAPIStatus(tokenId, openId);
    }

    public List<OpenAPIRelation> getOpenAPIRelationList() {
        return commonsMapper.getOpenAPIRelationList();
    }

    public Integer saveTokenRelation(String companyName, String companyNameShort, String tokenIdPrefix, String type, Integer id) {
        if(type.equals("新增")){
            return commonsMapper.insertTokenRelation(companyName, companyNameShort, tokenIdPrefix);
        }else if(type.equals("编辑")){
            return commonsMapper.updateTokenRelation(companyName, companyNameShort, tokenIdPrefix, id);
        }

        return 0;
    }

    public Integer saveToken(Integer openId, String tokenId, String uri, String interfaceName, String type, String remark) {
        if(type.equals("新增")){
            return commonsMapper.insertToken(tokenId, uri, interfaceName, remark);
        }else if(type.equals("编辑")){
            return commonsMapper.updateToken(openId, tokenId, uri, interfaceName, remark);
        }

        return 0;
    }

    public List<InterfaceUsedLimit> getInterfaceLimitList(String companyName, String interfaceName, String jsonFlag, Integer pageIndex, Integer pageSize) {
        return commonsMapper.getInterfaceLimitList(companyName, interfaceName, jsonFlag, pageIndex, pageSize);
    }

    public Integer getInterfaceLimitListTotalCount(String companyName, String interfaceName, String jsonFlag) {
        return commonsMapper.getInterfaceLimitListTotalCount(companyName, interfaceName, jsonFlag);
    }


    public Integer saveInterfaceLimit(String editType, String jsonFlag, String companyName, Integer limitNumber, String interfaceName, Integer id) {

        InterfaceUsedLimit interfaceUsedLimit = new InterfaceUsedLimit();
        if(id != null) interfaceUsedLimit.setId(id);
        interfaceUsedLimit.setCompanyName(companyName);
        interfaceUsedLimit.setInterfaceName(interfaceName);
        interfaceUsedLimit.setJsonFlag(jsonFlag);
        interfaceUsedLimit.setLimitNumber(limitNumber);

        if(editType.equals("新增")){
            return commonsMapper.insertInterfaceLimit(interfaceUsedLimit);
        }else if(editType.equals("编辑")){
            return commonsMapper.updateInterfaceLimit(interfaceUsedLimit);
        }

        return 0;
    }

    public Integer deleteTokenRelation(Integer id) {
        return commonsMapper.deleteTokenRelation(id);
    }

    public Integer tokenValidate(String tokenId, String companyName, String uri, Integer openId) {

        //token前三码和当前公司是否匹配
        String companyNameBasic = commonsMapper.getCompanyNameByTokenId(tokenId.substring(0,3)) == null ? "" : commonsMapper.getCompanyNameByTokenId(tokenId.substring(0,3));
        if(!companyName.equals(companyNameBasic)) return 2;
        //是否有相同token
        List<OpenAPI> isSame = commonsMapper.getCreditOpenApi(tokenId, null, null);
        if(isSame != null && isSame.size() > 0){
            if(openId == null) return 1;
            for (OpenAPI openAPI : isSame){
                if(openAPI.getOpenId() != openId){
                    return 1;
                }
            }
        }
        return 0;
    }

    public Integer tokenPrefixValidate(String tokenIdPrefix, Integer id) {
        List<OpenAPIRelation> isSame = commonsMapper.getCreditApiRelation(tokenIdPrefix);
        if(isSame != null && isSame.size() > 0){
            if(id == null) return 1;
            for (OpenAPIRelation openAPIRelation : isSame){
                if(openAPIRelation.getId() != id){
                    return 1;
                }
            }
        }else{
            return 0;
        }
        return 0;
    }

    public Integer deleteInterfaceLimit(Integer id) {
        InterfaceUsedLimit interfaceUsedLimit = new InterfaceUsedLimit();
        interfaceUsedLimit.setId(id);
        return commonsMapper.deleteInterfaceLimit(interfaceUsedLimit);
    }

    public void deleteCreditOpenApi(String tokenId) {
        commonsMapper.deleteCreditOpenAPI(tokenId);
    }

    public List<ClientNoMaintain> getClientNoMaintainList(String companyName, String dataSource, Integer pageIndex, Integer pageSize) {
        return commonsMapper.getClientNoMaintainList(companyName, dataSource, pageIndex, pageSize);
    }

    public Integer getClientNoMaintainListTotalCount(String companyName, String dataSource) {
        return commonsMapper.getClientNoMaintainListTotalCount(companyName, dataSource);
    }
}
