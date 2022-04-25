package com.fanruan.platform.bean;
import lombok.Data;

/**
 *   @kern
 *   @since 2022/03/08
 *  点数填报版本信息
 */
@Data
public class PAFCVersion {

    private String versionNo;
    private Integer supplierId;
    private String supplierName;
    private Integer interfaceTotalPoints;
    private Integer attentionTotalPoints;
    private Integer interfaceDistributeTotalPoints;
    private Integer attentionDistributeTotalPoints;
    private Integer interfaceUsedTotalPoints;
    private Integer attentionUsedTotalPoints;
    private String startDate;
    private String endDate;

    public Integer getInterfaceDistributeTotalPoints() {
        return interfaceDistributeTotalPoints;
    }

    public void setInterfaceDistributeTotalPoints(Integer interfaceDistributeTotalPoints) {
        this.interfaceDistributeTotalPoints = interfaceDistributeTotalPoints;
    }

    public Integer getAttentionDistributeTotalPoints() {
        return attentionDistributeTotalPoints;
    }

    public void setAttentionDistributeTotalPoints(Integer attentionDistributeTotalPoints) {
        this.attentionDistributeTotalPoints = attentionDistributeTotalPoints;
    }

    public Integer getAttentionTotalPoints() {
        return attentionTotalPoints;
    }

    public void setAttentionTotalPoints(Integer attentionTotalPoints) {
        this.attentionTotalPoints = attentionTotalPoints;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getInterfaceTotalPoints() {
        return interfaceTotalPoints;
    }

    public void setInterfaceTotalPoints(Integer interfaceTotalPoints) {
        this.interfaceTotalPoints = interfaceTotalPoints;
    }

    public Integer getInterfaceUsedTotalPoints() {
        return interfaceUsedTotalPoints;
    }

    public void setInterfaceUsedTotalPoints(Integer interfaceUsedTotalPoints) {
        this.interfaceUsedTotalPoints = interfaceUsedTotalPoints;
    }

    public Integer getAttentionUsedTotalPoints() {
        return attentionUsedTotalPoints;
    }

    public void setAttentionUsedTotalPoints(Integer attentionUsedTotalPoints) {
        this.attentionUsedTotalPoints = attentionUsedTotalPoints;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
