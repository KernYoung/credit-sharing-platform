package com.fanruan.platform.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "credit_focus_relations")
public class UserFocus {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_focus_seq")
    @SequenceGenerator(name = "user_focus_seq", initialValue = 1, allocationSize = 1, sequenceName = "USER_FOCUS_SEQUENCE")
    private Integer id;


    @Column(name="USER_ID")
    private Integer userId;

    @Column(name="COMPANY_ID")
    private Integer companyId;

    @Column(name="COMPANY_NAME")
    private String companyName;

    @Column(name="RELATIONS")
    private String relations;

    @Transient
    @Column(name="MESSAGE_NUMBER")
    private String messageNumber;

}
