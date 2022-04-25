package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "LOG_ZXB_APPLY")
public class ZhongXinBaoLog {
    //资信报告下单流水号_输入
    @Id
    @Column(name="CORPSERIALNO_IMPORT",length = 30,nullable=false)
    private String corpSerialNo;

    //企业标识[企业填写]
    @Column(name="CLIENTNO_IMPORT",length = 30)
    private String clientNo;

    //待调查企业中国信保企业代码
    @Column(name="REPORTBUYERNO",length = 30)
    private String reportbuyerNo;

    //待调查企业国别
    @Column(name="REPORTCORPCOUNTRYCODE",length = 30)
    private String reportCorpCountryCode;

    //待调查企业中文名称
    @Column(name="REPORTCORPCHNNAME",length = 280)
    private String reportCorpChnName;

    //待调查企业英文名称
    @Column(name="REPORTCORPENGNAME",length = 100)
    private String reportCorpEngName;

    //待调查企业地址
    @Column(name="REPORTCORPADDRESS",length = 100)
    private String reportCorpaddress;

    //待调查企业统一社会信用代码
    @Column(name="CREDITNO",length = 30)
    private String creditno;

    //是否导读（或是否翻译）
    @Column(name="ISTRANSLATION",length = 10)
    private String istranslation;

    //资信报告下单流水号_输出
    @Column(name="CORPSERIALNO_EXPORT",length = 30)
    private String corpSerialNoOut;

    //审批标识
    @Column(name="APPROVECODE",length = 10)
    private String approveCode;

    //审批结果
    @Column(name="APPROVEMSG",length = 500)
    private String approveMsg;

    //下单企业信保通账号
    @Column(name="CLIENTNO_EXPORT",length = 30)
    private String clientNoOut;

    //其它信息
    @Column(name="OTHERMSG",length = 500)
    private String otherMsg;

    //填报人
    @Column(name="UPDATEBY",length = 20)
    private String updateBy;

    //填报时间
    @Column(name="UPDATETIME")
    private Timestamp updateTime;

    //审核人
    @Column(name="APPROVEBY",length = 255)
    private String approveby;

    //审核时间
    @Column(name="APPROVEDATE")
    private Timestamp approveDate;

    //待调查企业联系电话
    @Column(name="PHONE",length = 100)
    private String reportCorptel;

    //待调查企业邮箱
    @Column(name="MAIL",length = 100)
    private String reportCorpemail;

    //速度
    @Column(name="SPEED",length = 100)
    private String speed;



    @Transient
    private String updateName;

    @Transient
    private String approveName;

}
