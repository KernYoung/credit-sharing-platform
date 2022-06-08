package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class LikeQuery {

    //序号
    public String no;
    //操作用户
    public String userName;
    //模糊查询关键字
    public String  keyWord;
    //操作时间
    public String operateTime;
}
