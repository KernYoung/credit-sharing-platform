package com.fanruan.platform.bean;
import lombok.Data;

import java.sql.Timestamp;

/**
 *   @author
 *   @since 2021/3/1
 *
 */
@Data
public class ZhongXinBaoApplyProgressList {
    private String reportbuyerno;
    private String reportcorpchnname;
    private String reportcorpengname;
    private String reportName;
    private String updatetime;
    private String getTime; //更新时间
    private String approveCode;//审核标识
    private String approveby;//审核人
    private String approveDate;//审核时间
    private String TBTIME;//填报时间
    private String ZXBresults;//中信保反馈
}
