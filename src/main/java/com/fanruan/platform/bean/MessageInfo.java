package com.fanruan.platform.bean;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "INPUT_PUSH")
public class MessageInfo {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PUSH_FLAG")
    private String pushFlag;

    @Column(name = "PROMPTINFO",columnDefinition="Clob")
    private String promptinfo;

    @Column(name = "PUSH_TIME")
    private Timestamp pushTime;

    @Column(name = "PUSH_TYPE")
    private String pushType;

}
