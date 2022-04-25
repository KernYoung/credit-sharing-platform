package com.fanruan.platform.bean;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 公司基本信息，保存oracle，基于用户名称和曾用名在es中建立索引
 */
@Data
@Entity
@Table(name = "ODS_TYC_GSINFO")
public class TianYanChaInfo {
    @Id
    @Column(name="ID")
    private String id;

    /**
     * 公司名称
     */
    @Column(name="NAME")
    private String name;

    @Column(name="COMPANYORGTYPE")
    private String companyorgtype;

    @Column(name="REGCAPITAL")
    private String regcapital;

    @Column(name="ESTIBLISHTIME")
    private String estiblishtime;

    @Column(name="REGLOCATION")
    private String reglocation;

    @Column(name="INDUSTRY")
    private String industry;

    @Column(name="DS_CREDITCODE")
    private String dsCreditcode;

    @Column(name="GS_CREDITCODE")
    private String gsCreditcode;

    @Column(name="REGT_CREDIDTCODE")
    private String regtCredidtcode;

    @Column(name="REG_CREDIDTCODE")
    private String regCredidtcode;

    @Column(name="CREDIT_CODE")
    private String creditCode;

    @Column(name="LEGALPERSONNAME")
    private String legalPersonName;

    @Column(name="FROMTIME")
    private String fromTime;

    @Column(name="TOTIME")
    private String toTime;

    @Column(name="UPDATETIME")
    private String updateTime;

//    private String resultStr;

}
