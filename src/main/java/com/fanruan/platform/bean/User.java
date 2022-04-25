package com.fanruan.platform.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@Entity
@Table(name = "credit_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", initialValue = 1, allocationSize = 1, sequenceName = "USER_SEQUENCE")
    private Integer userId;

//    @Column(name="uid")
//    private Integer uid;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="mobile")
    private String mobile;

    @Column(name="email")
    private String email;

    @Column(name="company_Name")
    private String companyName;

    @Column(name="dept_Name")
    private String deptName;

    @Column(name="name")
    private String name;

    @Column(name="COMPANY_CODE")
    private String companyCode;

    @Column(name="DEPT_CODE")
    private String deptCode;

    @Column(name="status")
    private Integer status;

    @Column(name="NEWCOMPANY_FLAG")
    private Integer newCompanyFlag;

    @Column(name="ROLE_NAME")
    private String roleName;

    @Column(name="PSW_CREATE_TIME")
    private Timestamp passwordCreateTime;

    @Column(name="PSW_UPDATE_TIME")
    private Timestamp passwordUpdateTime;

    @Transient
    private String permissionRoles;

    @Transient
    private String updatePermission;

    private String RolePermission;

    public String toString(){
        String result = "";
        result = "userId: " + userId + ",userName: " + username + ",name: " + name + ",password: "
                + password+ ",mobile: " + mobile + ",email: " + email + ",companyName: "
                + companyName + ",companyCode: " + companyCode + ",deptName: " + deptName
                + ",roleName: " + roleName;
        return result;
    }

}
