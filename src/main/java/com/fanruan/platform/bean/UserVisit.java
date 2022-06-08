package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class UserVisit {
    //日期
    public String billDate;
    //访问次数
    public Integer visitNum;
    //访问用户数
    public Integer visitUserNum;
    //访问页面数
    public  Integer visitPageNum;

}
