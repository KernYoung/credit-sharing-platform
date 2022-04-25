package com.fanruan.platform.bean;
import lombok.Data;

import java.sql.Timestamp;

/**
 *   @kern
 *   @since 2022/03/08
 *  点数填报收件人信息
 */
@Data
public class PAFCUser {
    private Integer id;
    private String userId;
    private String userName;
    private String email;
    private String companyCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}
