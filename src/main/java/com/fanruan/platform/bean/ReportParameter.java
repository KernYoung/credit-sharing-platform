package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class ReportParameter {
    //开始日期
    public String startDate;
    //结束日期
    public String endDate;
    //公司名称
    public String companyName;
    //公司名称
    public String companyName2;
    //用户名
    public String userName;
    //用户编码
    public String userCode;
    //天眼查服务开始时间
    public String tStart;
    //中诚信服务开始时间
    public String zStart;
    //中信保服务开始时间
    public String bStart;
    //企查查或启信宝开始时间
    public String qStart;
    //当月和非当月
    public String flag;
    //数据来源
    public String source;
    //公司名
    public String name;
    //公司编码
    public String code;
    //公司前缀
    public String pre;
    //上级公司
    public String scode;

    public String zcode;

}
