package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
@Data
public class SpeedMapping {
    @Id
    private String id;

    /**
     * 州
     */
    private String continent;
    /**
     * 国家编码
     */
    private String nationCode;

    /**
     * 国家名称
     */
    private String nationName;

    /**
     * 报告类型
     */
    private String reportType;
    /**
     * 紧急程度
     */
    private String speed;
    /**
     * 申请时间RESPONSE_DAYS
     */
    private String responseDays;
    /**
     * 备注
     */
    private String remark;


}