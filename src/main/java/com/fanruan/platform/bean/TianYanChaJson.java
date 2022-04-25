package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "ODS_TYC_JSON")
public class TianYanChaJson {

    @Column(name="ID")
    private String id;

    @Id
    @Column(name="NAME")
    private String name;

    @Column(name="ERROR_CODE")
    private String errorCode;

    @Column(name="REASON")
    private String reason;

    @Basic(fetch = FetchType.LAZY)
    @Column(name="JSON",columnDefinition="Clob",nullable=true)
    private String json;

    @Column(name="UPDATETIME")
    private Timestamp updateTime;

    @Column(name="JSONFLAG")
    private String jsonFlag;

    @Column(name="TOKEN_ID")
    private String tokenId;

    @Basic(fetch = FetchType.LAZY)
    @Column(name="PARAM_IN",columnDefinition="Clob",nullable=true)
    private String paramIn;

    @Column(name="INSERT_Date")
    private Timestamp insertDate;
}
