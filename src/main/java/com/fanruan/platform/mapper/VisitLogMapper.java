package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface VisitLogMapper {
    /**
     * 获取用户访问统计
     * @param reportParameter
     * @return
     */
    public List<UserVisit> getUserVisit(ReportParameter reportParameter);

    /**
     *
     */
    public List<Map<String,Object>> getCompanyList(ReportParameter reportParameter);
    /**
     * 获取用户访问统计明细
     * @param reportParameter
     * @return
     */
    public  List<UserVisitList> getUserVisitList(ReportParameter reportParameter);

    public List<UserBehavior> getUserBehavior(ReportParameter reportParameter);

    public List<Map<String,Object>> getUseUserCompanyName(ReportParameter reportParameter);

    /**
     * 用户活跃数
     * @param reportParameter
     * @return
     */
    public List<LogMonthActive> getLogMonthActive(ReportParameter reportParameter);

    /**
     * 用户使用
     * @param reportParameter
     * @return
     */
    public List<LogMonthUse> getLogMonthUse(ReportParameter reportParameter);

    public List<Map<String,Object>> getLogMonthTotal(ReportParameter reportParameter);

    
}
