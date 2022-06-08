package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class XbReportUseByCompany {
    //报告单号
    public String reportCode;
    //信保代码
    public String xbCode;
    //中英文名称
    public String name;
    //申请人
    public String userName;
    //申请人所在企业
    public String applyCompanyName;
    //申请时间
    public String applyTime;
    //审核时间
    public String approveTime;
    //预览次数
    public Integer previewNum;
    //下载次数
    public Integer downLoadNum;
}
