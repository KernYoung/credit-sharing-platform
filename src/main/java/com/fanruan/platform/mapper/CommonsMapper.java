package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.*;
import com.mysql.cj.log.Log;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.HashMap;
import java.util.List;

/**
 * <p>TODO</p>
 *
 * @author DELk
 * @@version 1.0.0
 * @since 2020/9/28
 */
@Repository
public interface CommonsMapper {
    /**黑名单*/
//    List<BlackList> getBlackList(String userCode);
    /*首页展示黑名单*/
    List<BlackListDetailResultList> getBlackList(Integer pageIndex,Integer pageSize,String sortCriteria);
    Integer getBlackListTotalCount();
    /*首页展示灰名单*/
    List<BlackListDetailResultList> getGreyList(Integer pageIndex,Integer pageSize,String sortCriteria);
    Integer getGreyListTotalCount();
    /**子管理员获取可选公司*/
    public List<UserCompany> getUserCompany(String username);
    /**根据companyCode获取公司*/
    CompanyLevel getCompanyName(String companyCode);
    /**获取公司二级公司*/
    List<CompanyLevel> getLV2Company();
//    /**获取角色*/
//    public List<String> getUserRole();
    /**信保报告审核列表*/
    List<ZhongXinBaoLog> getZxbApplyList(HashMap<String, Object> hs, Integer pageIndex, Integer pageSize,
                                        String zxbCode, String zxbCompanyName,String approveCode,String zxbInformant,String zxbApprover,Integer isSubAdmin, String operator);
    /**信保报告审核列表-条数*/
    Integer getZxbApplyListCount(Integer pageIndex,Integer pageSize,
                                String zxbCode,String zxbCompanyName,String approveCode,String zxbInformant,String zxbApprover,Integer isSubAdmin,String operator);
    /**审核不通过向OA推送消息*/
    Integer insertOAMsg(String updateBy, String approveBy);

    /**获取组织结构树的数据*/
    List<CompanyLevel> getAllCompanyLevel(@Param(value="companyCode") String companyCode);
    /**获取公司信息*/
    CompanyLevel getCompanyLevel(@Param(value="companyCode") String companyCode);
    /**判断公司是否启用   1：代表启用 */
    String getCompanyStatus(@Param(value="companyCode") String companyCode);
    /**获取客商初筛查看结果页面填报人和流水号*/
    List<String> getAllUpdataBy();
    List<String> getAllSerialid(@Param(value="updataBy") String updataBy);
    /**客商初筛查看结果页面  根据填报人和流水号进行筛选*/
    List<MerchantsViewResults> getMerchantsViewResults(@Param(value="updataBy") String updataBy,@Param(value="serialid") String serialid,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    Integer getMerchantsViewResultsCount(@Param(value="updataBy") String updataBy,@Param(value="serialid") String serialid,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    List<MerchantsViewResults> getMerchantsViewExcel(@Param(value="updataBy") String updataBy,@Param(value="serialid") String serialid,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    /**获取黑名单申请查询结果列表*/
    List<BlacklistResultList> getAllBlackListResultList(@Param(value="status") List<Integer> status,@Param(value="publishBy") String publishBy,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    Integer getAllBlackListResultListCount(@Param(value="status") List<Integer> status,@Param(value="publishBy") String publishBy,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    /**黑名单申请页面查询公司和信保代码*/
    List<CompayNameCode> getCompayNameAndCreditCode();
    /**获取审批专员*/
    List<String> getApproveBy(@Param(value="approveBy") String approveBy);
    /**获取二级公司*/
    String getLevelCompanyName(@Param(value="CompanyName") String CompanyName);
    /**每个类型的历史报告只显示一个*/
    List<Report> getAllHistoricalReport(@Param(value="creditCode") String creditCode);
    /**获取黑名单审批查询结果列表*/
    List<BlacklistResultList> getBlacklistApprovalList(@Param(value="status") List<Integer> status,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize,@Param(value="userName") String userName);
    Integer getBlacklistApprovalListCount(@Param(value="status") List<Integer> status,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize,@Param(value="userName") String userName);
    List<BlacklistResultList> getBlacklistApprovalExcel(@Param(value="status") List<Integer> status,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize,@Param(value="userName") String userName);
    /**获取黑名单详情查询结果列表*/
    List<BlackListDetailResultList> getBlackListDetailList(@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize, @Param(value="companyName") String companyName, @Param(value="dataSource") String dataSource, @Param(value="startDate") String startDate, @Param(value="endDate") String endDate, @Param(value="userCode") String userCode);
    Integer getBlackListDetailListCount(@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize, @Param(value="companyName") String companyName, @Param(value="dataSource") String dataSource, @Param(value="startDate") String startDate, @Param(value="endDate") String endDate, @Param(value="userCode") String userCode);
    /**消息中心 实时预警 获取企业名称列表*/
    List<String> getCompayNameList(@Param(value="userName") String userName);
    /**消息中心 实时预警 获取事件类型列表*/
    List<String> getEventTypeList(@Param(value="userName") String userName, @Param(value="riskleve") List<String> riskleve,@Param(value="companyName")  List<String> companyName,@Param(value="startDate") String startDate,@Param(value="endDate") String endDate);
    /**消息中心 实时预警 列表数据*/
    List<RealTimeWarning> getRealTimeWarning(@Param(value="userName") String userName, @Param(value="riskleve") List<String> riskleve,@Param(value="companyName")  List<String> companyName,@Param(value="eventType")  List<String> eventType,@Param(value="startDate") String startDate,@Param(value="endDate") String endDate,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    Integer getRealTimeWarningCount(@Param(value="userName") String userName, @Param(value="riskleve") List<String> riskleve,@Param(value="companyName")  List<String> companyName,@Param(value="eventType")  List<String> eventType,@Param(value="startDate") String startDate,@Param(value="endDate") String endDate,@Param(value="pageIndex") Integer pageIndex,@Param(value="pageSize") Integer pageSize);
    /**消息中心 风险早报 and 新闻早报获取企业名称*/
    List<String> getZCXCompayNameList(@Param(value="userName") String userName);
    /**消息中心 平台消息*/
    List<PlatformNews> getPlatformNews(String userName, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    Integer getPlatformNewsCount(String userName, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    /**消息中心 风险早报*/
    List<RiskMorningPost> getRiskMorningPost(String userName, List<String> riskleve, List<String> companyName, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    Integer getRiskMorningPostCount(String userName, List<String> riskleve, List<String> companyName, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    /**消息中心 新闻早报*/
    List<MorningNews> getMorningNews(String userName, List<String> riskleve, List<String> companyName, List<String> newsEmotion, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    Integer getMorningNewsCount(String userName, List<String> riskleve, List<String> companyName, List<String> newsEmotion, String startDate, String endDate, Integer pageIndex, Integer pageSize);
    //jina
    /*根据userName查出多个审核人*/
    List<String> getReportApplyUserNameList(String userName);
    /*将信报申请落库*/
    Integer insertReportApply(String userName);
    /*根据用户名查询当前登录人的二级单位*/
    String getSecondaryDepartmentByLoginName(String loginName);
    /**/
    Integer reportbuyerNoIsExist(String reportbuyerNo);
    /*根据信保代码查询是否存在记录的ApproveCode & updateTime*/
    List<ZhongXinBaoLog> reportbuyerNoIsExist4Apply(String reportbuyerNo);
    /*更新天眼查Json*/
    Integer updateTYCJson(TianYanChaJson tianYanChaJson);
    Integer updateTYCJsonByIdBlank(TianYanChaJson tianYanChaJson);

    /*插入天眼查Json*/
    Integer insertTYCJson(TianYanChaJson tianYanChaJson);

//    /*插入天眼查Json*/
//    Integer insertTYCJsonWithParam(TianYanChaJson tianYanChaJson);

    /*查询[credit_company]里是否有当天的数据*/
    Integer companyExist(Company company);
    /*若查到不是当天的数据则更新*/
    Integer updateCompany(Company company);

    /*根据信保代码查询PDF FileName*/
    String getXBPDFFileName(String reportBuyerNo);

    /*根据信保代码查询PDF File UpdateTime*/
    String getXBPDFFileUpdateTime(String reportBuyerNo);

    /*根据userName查询userId*/
    String getUserId(String userName);

    /**申请报告成功后向OA推送消息*/
    Integer insertZXBApplyInfo4ETL(InputPush inputPush);
    /**申请报告成功后拼接字段为后续插入input push*/
    List<InputPush> getApplyInfo(String userName);

    String getOrderState(String corpSerialNo);

    String getNoticeSerialNo(String corpSerialNo);

    /**插入人员信息-点数填报*/
    Integer insertPAFCUser(PAFCUser pafcUser);
    /**更新人员信息-点数填报*/
    Integer updatePAFCUserById(PAFCUser pafcUser);

    /**更新公司信息-点数填报*/
    Integer updatePAFCCompany(PAFCCompany pafcCompany);
    /**删除公司信息-点数填报*/
    Integer deletePAFCCompany(PAFCCompany pafcCompany);
    /**新增公司信息-点数填报*/
    Integer insertPAFCCompany(PAFCCompany pafcCompany);

    /**删除人员信息-点数填报*/
    Integer deletePAFCUserById(Integer id);

//    /**插入公司信息-点数填报*/
//    Integer insertPAFCCompany(PAFCUser pafcUser);

//    /**插入填报信息-点数填报*/
//    Integer insertPAFCDistributeInfo(PAFCUser pafcUser);

    /**更新版本信息-点数填报*/
    Integer updatePAFCVersion(PAFCVersion pafcVersion);

    /**新增版本信息-点数填报*/
    Integer insertPAFCVersion(PAFCVersion pafcVersion);

    /**新增点数填报基本信息-点数填报*/
    Integer insertPAFCPoints(PAFCPoints pafcPoints);

    /**更新填报信息-点数填报*/
    Integer updatePAFCPoints(PAFCPoints pafcPoints);


    /**查询版本信息-点数填报*/
    List<PAFCVersion> getPAFCVersionList();

    /**获取版本点数总计 天眼查-点数填报*/
    PAFCVersion getVersionPointsTYC(PAFCVersion pafcVersion);

    /**获取版本点数总计 中诚信-点数填报*/
    PAFCVersion getVersionPointsZCX(PAFCVersion pafcVersion);

    /**查询版本信息-点数填报*/
    PAFCVersion getPAFCVersion(String versionNo);

    /**查询点数信息-点数填报*/
    List<PAFCPoints> getPAFCPointsList(String versionNo);

    /**查询总计使用点数-点数填报*/
    List<PAFCPoints> getCompanyPoints(PAFCVersion versionNo);


    /**查询人员信息-点数填报*/
    List<PAFCUser> getPAFCUserList();
    List<PAFCUser> getPAFCUserListByCompanyCode(String companyCode);


    /**查询公司信息-点数填报*/
    List<PAFCCompany> getPAFCCompanyList();

    /**查询tokenId关联*/
    String getENTTokenId(String companyName);

    /**存历史记录：中诚信和天眼查的关注/取消关注操作*/
    Integer insertConcernHistory(LogConcernHistory logConcernHistory);

    /**根据tokenId查询关联公司名字*/
    String getCompanyNameByTokenId(String tokenId);

    /**查询接口使用数量*/
    Integer getInterfaceUsedNumber(String jsonFlag, String companyName);

    /**查询接口限制数量*/
    Integer getInterfaceLimitNumber(String jsonFlag, String companyName);

    /**获取天眼查当天的有效请求*/
    List<TianYanChaJson> getValidRequest4TheDay(String companyName, String jsonFlag);

    List<OpenAPI> getTokenList(String companyName, String uri, String interfaceName, Integer status, Integer pageIndex, Integer pageSize);

    Integer getTokenListTotalCount(String companyName, String uri, String interfaceName, Integer status);
    /**更新状态(启用、停用)*/
    Integer updateOpenAPIStatus(String tokenId, Integer openId);

    /**获取成员公司下发token的前三码*/
    List<OpenAPIRelation> getOpenAPIRelationList();

    Integer insertTokenRelation(String companyName, String companyNameShort, String tokenIdPrefix);

    Integer updateTokenRelation(String companyName, String companyNameShort, String tokenIdPrefix, Integer id);

    Integer insertToken(String tokenId, String uri, String interfaceName, String remark);

    Integer updateToken(Integer openId, String tokenId, String uri, String interfaceName, String remark);

    List<InterfaceUsedLimit> getInterfaceLimitList(String companyName, String interfaceName, String jsonFlag, Integer pageIndex, Integer pageSize);

    Integer getInterfaceLimitListTotalCount(String companyName, String interfaceName, String jsonFlag);

    Integer insertInterfaceLimit(InterfaceUsedLimit interfaceUsedLimit);

    Integer updateInterfaceLimit(InterfaceUsedLimit interfaceUsedLimit);

    Integer deleteInterfaceLimit(InterfaceUsedLimit interfaceUsedLimit);

    Integer deleteTokenRelation(Integer id);

    List<OpenAPI> getCreditOpenApi(String tokenId, String uri, Integer openId);

    List<OpenAPIRelation> getCreditApiRelation(String tokenIdPrefix);

    void deleteCreditOpenAPI(String tokenId);

    List<ClientNoMaintain> getClientNoMaintainList(String companyName, String dataSource, Integer pageIndex, Integer pageSize);

    Integer getClientNoMaintainListTotalCount(String companyName, String dataSource);
}
