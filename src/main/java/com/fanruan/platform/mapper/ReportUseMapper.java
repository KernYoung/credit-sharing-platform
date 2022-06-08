package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ReportUseMapper {

    public List<XbReportUse> getXbReportUse(ReportParameter reportParameter);

    public List<XbReportUseList> getXbReportUseList(ReportParameter reportParameter);

    public List<XbReportUseByCompany> getXbReportUseByCompany(ReportParameter reportParameter);

    /**
     * 使用数量
     * @param reportParameter
     * @return
     */
    public List<Map<String,Object>> getUseNum(ReportParameter reportParameter);

    public List<ZcxReportUse> getZcxReportUse(ReportParameter reportParameter);

    public List<ZcxReportUseList> getZcxReportUseList(ReportParameter reportParameter);

    public List<Map<String,Object>> getZcxReportGz(ReportParameter reportParameter);

    public Map<String,Object> getZxcShareSum(ReportParameter reportParameter);

    public List<Monitoring> getMonitoring(ReportParameter reportParameter);

//    public List<TycUse> getTycUse(ReportParameter reportParameter);

    /**
     * 天眼查总关注
     * @param reportParameter
     * @return
     */
    public List<Map<String,Object>> getTycUseGz(ReportParameter reportParameter);

    /**
     * 天眼查模糊查询
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycUseLike(ReportParameter reportParameter);

    /**
     * 天眼查基本
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycBase(ReportParameter reportParameter);

    /**
     * 天眼查客户初筛
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycFilterCustomer(ReportParameter reportParameter);

    /**
     * 天眼查下发
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycXf(ReportParameter reportParameter);

    /**
     * 天眼查嵌入
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycQr(ReportParameter reportParameter);

    /**
     * 天眼查总关注
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycZgz(ReportParameter reportParameter);

    /**
     * 天眼查总关注不联动
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycZgzBld(ReportParameter reportParameter);


    /**
     * 天眼查客户初筛调用次数
     * @param reportParameter
     * @return
     */
    public List<Map<String,Object>> getCustomFilter(ReportParameter reportParameter);

    /**
     * 天眼查模糊查询
     * @param reportParameter
     * @return
     */
    public Map<String,Object> getTycMhcx(ReportParameter reportParameter);

    /**
     * 天眼查客户初筛使用明细
     * @param reportParameter
     * @return
     */
    public List<TycFilterCustomerList> getTycFilterCustomerList(ReportParameter reportParameter);

    /**
     * 天眼查页面关注
     * @param reportParameter
     * @return
     */
    public List<pageActive> getPageActive(ReportParameter reportParameter);

    /**
     * 模糊查询记录
     * @param reportParameter
     * @return
     */
    public List<LikeQuery> getLikeQuery(ReportParameter reportParameter);



}
