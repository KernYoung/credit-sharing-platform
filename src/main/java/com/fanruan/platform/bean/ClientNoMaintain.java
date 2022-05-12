package com.fanruan.platform.bean;
import lombok.Data;

/**
 *   @kern
 *   @since 2022/05/09
 *  信保代码维护
 */
@Data
public class ClientNoMaintain {

    private String companyCode;
    private String companyName;
    private String companyCodeSuperior;
    private String companyNameSuperior;
    private String dataSource;
    private String clientNo;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyCodeSuperior() {
        return companyCodeSuperior;
    }

    public void setCompanyCodeSuperior(String companyCodeSuperior) {
        this.companyCodeSuperior = companyCodeSuperior;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNameSuperior() {
        return companyNameSuperior;
    }

    public void setCompanyNameSuperior(String companyNameSuperior) {
        this.companyNameSuperior = companyNameSuperior;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getClientNo() {
        return clientNo;
    }

    public void setClientNo(String clientNo) {
        this.clientNo = clientNo;
    }
}
