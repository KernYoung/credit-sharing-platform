package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class ZcxReportUseList {
    //申请类型
    public String applyType;
    //申请企业
    public String applyCompanyName;
    //申请人
    public String userName;
    //申请人所在公司
    public String companyName;
    //申请时间
    public String applyTime;
}
