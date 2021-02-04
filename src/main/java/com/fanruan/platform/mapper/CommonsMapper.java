package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.*;
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
    List<BlackList> getBlackList(String userCode);
    /**子管理员获取可选公司*/
    public List<UserCompany> getUserCompany(String username);
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
}
