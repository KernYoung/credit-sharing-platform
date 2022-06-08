package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class LogMonthUse {

    //板块
    public String businessSegments;
    //公司
    public String companyName;
    //天眼查使用
    public Integer tycUseNum;
    //爱企查使用
    public Integer aqcUseNum;
    public Integer zcxUseNum;
    public Integer zxbUseNum;
    //天眼查客户初筛
    public Integer tycSxNum;
    //爱企查客户初筛
    public Integer aqcSxNum;
    //天眼查api调用数量
    public Integer tycApiNum;
    //爱企查api调用数量
    public Integer aqcApiNum;
    //中诚信api调用数量
    public Integer zcxApiNum;
    //天眼查关注
    public Integer tycGzNum;
    //爱企查关注
    public Integer aqcGzNum;
    //中诚信关注
    public Integer zcxGzNum;
    //天眼查消息推送
    public Integer tycMessageNum;
    //爱企查消息推送
    public Integer aqcMessageNum;
    //中诚信消息推送
    public Integer zcxMessageNum;
}
