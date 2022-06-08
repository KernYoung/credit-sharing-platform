package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class ZcxReportUse {
    //序号
    public String no;
    //模块
    public String module;
    //点数
    public String numberOfHits;
    //使用次数
    public Integer userNumber;
    //共享次数
    public Integer shareNumber;
}
