package com.fanruan.platform.bean;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 公司基本信息，保存oracle，基于用户名称和曾用名在es中建立索引
 */
@Data
@Entity
@Table(name = "CREDIT_OPEN_API")
public class OpenAPIToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "open_api_seq")
    @SequenceGenerator(name = "OPEN_API_SEQ", initialValue = 1, allocationSize = 1, sequenceName = "OPEN_API_SEQUENCE")
    private Integer openId;

    @Column(name="TOKEN_ID")
    private String tokenId;

    @Column(name="TOKEN_KEY")
    private String tokenKey;

    @Column(name="URI")
    private String uri;

}
