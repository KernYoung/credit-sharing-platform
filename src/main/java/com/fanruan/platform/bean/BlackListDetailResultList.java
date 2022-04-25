package com.fanruan.platform.bean;

import lombok.Data;

@Data
public class BlackListDetailResultList {

    /**
     * 天眼查公司id
     */
    private Long tycCompanyId;

    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 组织机构代码
     */
    private String orgNo;
    /**
     * 法人名称
     */
    private String juridicalPersonName;
    /**
     * 企业状态
     */
    private String enterpriseStatus;
    /**
     * 行业分类
     */
    private String industryCategory;
    /**
     * 科目大类
     */
    private String subjectCategory;
    /**
     * 业务大类
     */
    private String businessCategory;
    /**
     * 状态
     */
    private String status;
    /**
     * 风险原因
     */
    private String riskReason;
    /**
     * 计入风险资产时间
     */
    private String beRiskyAssetsDate;
    /**
     * 存在的困难
     */
    private String existingDifficulties;
    /**
     * 账面标的金额
     */
    private String amountOfMark;
    /**
     * 计入来源
     */
    private String dataSource;

    public Long getTycCompanyId() {
        return tycCompanyId;
    }

    public void setTycCompanyId(Long tycCompanyId) {
        this.tycCompanyId = tycCompanyId;
    }

    public String getExistingDifficulties() {
        return existingDifficulties;
    }

    public void setExistingDifficulties(String existingDifficulties) {
        this.existingDifficulties = existingDifficulties;
    }

    public String getAmountOfMark() {
        return amountOfMark;
    }

    public void setAmountOfMark(String amountOfMark) {
        this.amountOfMark = amountOfMark;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getJuridicalPersonName() {
        return juridicalPersonName;
    }

    public void setJuridicalPersonName(String juridicalPersonName) {
        this.juridicalPersonName = juridicalPersonName;
    }

    public String getEnterpriseStatus() {
        return enterpriseStatus;
    }

    public void setEnterpriseStatus(String enterpriseStatus) {
        this.enterpriseStatus = enterpriseStatus;
    }

    public String getIndustryCategory() {
        return industryCategory;
    }

    public void setIndustryCategory(String industryCategory) {
        this.industryCategory = industryCategory;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subjectCategory) {
        this.subjectCategory = subjectCategory;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRiskReason() {
        return riskReason;
    }

    public void setRiskReason(String riskReason) {
        this.riskReason = riskReason;
    }

    public String getBeRiskyAssetsDate() {
        return beRiskyAssetsDate;
    }

    public void setBeRiskyAssetsDate(String beRiskyAssetsDate) {
        this.beRiskyAssetsDate = beRiskyAssetsDate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
