package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class pageActive {
    //序号
    public String no;
    //页面名称
    public String pageName;
    //页面路径
    public String pagePath;
    //查看次数
    public Integer num;
    //访问用户个数
    public Integer visitUserNum;
}
