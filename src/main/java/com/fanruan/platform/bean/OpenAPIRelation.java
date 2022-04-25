package com.fanruan.platform.bean;
import lombok.Data;

/**
 *   @kern
 *   @since 2022/04/12
 *  成员公司下发token前三码
 */
@Data
public class OpenAPIRelation {
    private String companyName;
    private String tokenIdPrefix;
    private String companyNameShort;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getTokenIdPrefix() {
        return tokenIdPrefix;
    }

    public void setTokenIdPrefix(String tokenIdPrefix) {
        this.tokenIdPrefix = tokenIdPrefix;
    }

    public String getCompanyNameShort() {
        return companyNameShort;
    }

    public void setCompanyNameShort(String companyNameShort) {
        this.companyNameShort = companyNameShort;
    }
}
