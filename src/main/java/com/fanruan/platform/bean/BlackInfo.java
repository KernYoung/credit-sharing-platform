package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "INPUT_BLACKLIST")
public class BlackInfo {

    @Id
    @Column(name="PID")
    private String pid;

    @Column(name = "ENTNAME")
    private String entName;

    @Column(name = "CODE")
    private String code;

    @Column(name = "GRADE")
    private String grade;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "REASON_URL")
    private String reasonUrl;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "PUBLISH_DEPT")
    private String publishDept;

    @Column(name = "PUBLISH_SDEPT")
    private String publishSdept;

    @Column(name = "PUBLISH_BY")
    private String publishBy;

    @Column(name = "PUBLISH_TIME")
    private Timestamp publishTime;

    @Column(name = "UPDATEBY")
    private String updateBy;

    @Column(name = "UPDATETIME")
    private Timestamp updateTime;

    @Column(name = "COMPANYNAME")
    private String companyName;

    @Column(name = "START_DATE")
    private Timestamp startDate;

    @Column(name = "APPROVE_BY")
    private String approveBy;

    @Column(name = "APPROVE_TIME")
    private Timestamp approveTime;

    @Column(name = "REASONS_FOR_RENEWAL")
    private String reasonsForRenewal;

    @Column(name = "APPROVE_REMARKS")
    private String approveRemarks;

    @Column(name = "CANCEL_BY")
    private String cancelBy;

    @Column(name = "CANCEL_TIME")
    private Timestamp cancelTime;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "TYC_COMPANY_ID")
    private Long tycCompanyId;

}
