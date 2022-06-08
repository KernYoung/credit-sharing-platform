package com.fanruan.platform.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LogMonthActive {
    //板块
    public String businessSegments;
    //公司
    public String companyName;
    //开通用户数
    public Integer userNum;
    //活跃用户数
    public Integer activeUserNum;
    //活跃用户占比
    public BigDecimal activeUserRatio;
    //访问次数
    public Integer visitNum;
    //用户活跃度
    //访问/活跃用户
    public BigDecimal acticeVisitRatio;
}
