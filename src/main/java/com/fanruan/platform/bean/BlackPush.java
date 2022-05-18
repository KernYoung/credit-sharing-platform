package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "INPUT_PUSH")
public class BlackPush {

    @Id
    @Column(name="ID")
    private String id;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="PUSH_FLAG")
    private String pushFlag;

    @Column(name="PUSH_TIME")
    private Timestamp pushTime;

    @Column(name="RESULT_TYPE")
    private String resultType;

    @Column(name="RESULT")
    private String result;

    @Column(name="PROMPTINFO",columnDefinition="Clob")
    private String promptInfo;



}
