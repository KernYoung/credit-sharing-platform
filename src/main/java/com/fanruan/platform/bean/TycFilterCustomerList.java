package com.fanruan.platform.bean;

import lombok.Data;

@Data
/**
 * 天眼查客户初筛使用情况
 */
public class TycFilterCustomerList {
    //已申请企业
    public String applyCompanyName;
    //最初申请人
    public String applyUserName;
    //申请人所在企业
    public String companyName;
    //最初申请时间
    public String applyTime;

}
