package com.fanruan.platform.bean;

import lombok.Data;

/**
 * 监控情况
 */
@Data
public class Monitoring {
    //序号
    public String no;
    //已关注企业
    public  String gzCompanyName;
    //关注人
    public String userName;
    //关注人所在企业
    public String companyName;
    //关注时间
    public String gzTime;
}
