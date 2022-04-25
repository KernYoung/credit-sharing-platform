package com.fanruan.platform.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * 公司基本信息，保存oracle，基于用户名称和曾用名在es中建立索引
 */
@Data
@Entity
@Table(name = "credit_company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "company_seq")
    @SequenceGenerator(name = "company_seq", initialValue = 1, allocationSize = 1, sequenceName = "COMPANY_SEQUENCE")
    private Integer companyId;

//    @Column(name="cid")
//    private Integer cid;
    /**
     * 公司名称
     */
    @Column(name="company_name")
    private String companyName;

    /**
     * 模糊查询id
     */
    @Column(name="ID")
    private Long id;

    /**
     * 曾用名
     */
    @Column(name="original_name")
    private String originalName;

    /**
     * 成立时间
     */
    @Column(name="build_date")
    private String buildDate;

    /**
     * 社会统一信用编码
     */
    @Column(name="credit_code")
    private String creditCode;

    /**
     * 法人
     */
    @Column(name="oper_name")
    private String operName;

    /**
     * 注册资本
     */
    @Column(name="regist_capi")
    private String registCapi;

    /**
     * 注册地址
     */
    @Column(name="regist_address")
    private String registAddress;

    /**
     * 企业类型
     */
    @Column(name="entType")
    private String entType;

    /**
     * 电话
     */
    @Column(name="phone_number")
    private String phoneNumber;

    /**
     * 传真
     */
    @Column(name="fax_number")
    private String faxNumber;

    /**
     * 运营地址
     */
    @Column(name="service_address")
    private String serviceAddress;

    /**
     * email
     */
    @Column(name="email")
    private String email;


    /**
     * 主营业务
     */
    @Column(name="scope")
    private String scope;

    /**
     * 网址
     */
    @Column(name="web_address")
    private String webAddress;


    /**
     * sinoRating说明
     */
    @Column(name="sinoRating")
    private String sinoRating;

    /**
     * 行业代码
     */
    @Column(name="trade_code")
    private String tradeCode;


    /**
     * 员工人数
     */
    @Column(name="staff_count")
    private String staffCount;

    /**
     * 上市公司
     */
    @Column(name="stock_type")
    private String stockType;

    /**
     * 实控人性质
     */
    @Column(name="controller_type")
    private String controllerType;

    /**
     * 高亮查询名
     */
    @Column(name="ent_name_ws")
    private String entNameWs;

    /**
     * 中信保注册名
     */
    @Column(name="reg_no")
    private String regNo;


    @Column(name="update_time")
    private Timestamp updateTime;

    @Transient
    private Integer total;



}
