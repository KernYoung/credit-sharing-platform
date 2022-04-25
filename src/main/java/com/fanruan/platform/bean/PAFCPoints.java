package com.fanruan.platform.bean;
import lombok.Data;

/**
 *   @kern
 *   @since 2022/03/08
 *  点数填报点数信息
 */
@Data
public class PAFCPoints {
    private String supplierName;
    private String companyName;
    private String userName;
    private String email;
    private Integer interfaceDistributePoints;
    private Integer attentionDistributePoints;
    private Integer attentionUsedPoints;
    private Integer interfaceUsedPoints;
    private String companyCode;
    private String versionNo;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public Integer getAttentionDistributePoints() {
        return attentionDistributePoints;
    }

    public void setAttentionDistributePoints(Integer attentionDistributePoints) {
        this.attentionDistributePoints = attentionDistributePoints;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getInterfaceDistributePoints() {
        return interfaceDistributePoints;
    }

    public void setInterfaceDistributePoints(Integer interfaceDistributePoints) {
        this.interfaceDistributePoints = interfaceDistributePoints;
    }

    public Integer getAttentionUsedPoints() {
        return attentionUsedPoints;
    }

    public void setAttentionUsedPoints(Integer attentionUsedPoints) {
        this.attentionUsedPoints = attentionUsedPoints;
    }

    public Integer getInterfaceUsedPoints() {
        return interfaceUsedPoints;
    }

    public void setInterfaceUsedPoints(Integer interfaceUsedPoints) {
        this.interfaceUsedPoints = interfaceUsedPoints;
    }
}
