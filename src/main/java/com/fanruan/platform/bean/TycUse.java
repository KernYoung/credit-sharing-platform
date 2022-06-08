package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class TycUse {
    //模块
    public String module;
    //细分模块
    public String segmentModule;
    //总次数
    public String num;
    //使用次数
    public Integer userNum;
    //剩余次数
    public String surplusNum;
}
