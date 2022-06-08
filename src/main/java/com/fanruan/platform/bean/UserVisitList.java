package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class UserVisitList {
    //序号
    public String no;
    //成员公司
    public String companyName;
    //子公司
    public String subCompanyName;
    //启用用户数
    public Integer userNum;
    //访问用户数
    public  Integer visitUserNum;
    //访问用户
    public String  userName;
    //用户编号
    public String userCode;
    //访问次数
    public Integer visitNum;
    //访问页面数
    public Integer visitPageNum;
    //访问次数合计
    public Integer visitTotalNum;
    //访问页面次数合计
    public Integer visitPageTotalNum;
    //最新访问时间
    public Integer lastVisitTime;

    public Object getAttributeValue(String key){
        if("companyName".equals(key)){
            return getCompanyName();
        }else if("visitNum".equals(key)){
            return getVisitNum();
        }else if("visitPageNum".equals(key)){
            return getVisitPageNum();
        }
        return null;
    }

    public void setAttributeValue(String key,Object value){
        if("visitTotalNum".equals(key)){
            Integer num = value==null?0:Integer.valueOf(value.toString());
            setVisitTotalNum(num);
        }else if("visitPageTotalNum".equals(key)){
            Integer num = value==null?0:Integer.parseInt(value.toString());
            setVisitPageTotalNum(num);
        }
    }
}
