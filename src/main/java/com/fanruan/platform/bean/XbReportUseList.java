package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class XbReportUseList {
    //报告编号
    public String reportCode;
    //信保代码
    public String xbCode;
    //中/英问名称
    public String name;
    //用户名称 username|name
    public String userName;
    //用户所在企业
    public String companyName;
    //操作记录
    public String operateNote;
    //操作时间
    public String operateTime;
}
