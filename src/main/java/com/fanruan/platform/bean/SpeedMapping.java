package com.fanruan.platform.bean;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Data
public class SpeedMapping implements Serializable {
    @Id
    private String id;

    /**
     * 州
     */
    @ExcelProperty(value = "大洲")
    private String continent;
    /**
     * 国家编码
     */
    @ExcelProperty(value = "国家代码")
    private String nationCode;

    /**
     * 国家名称
     */
    @ExcelProperty(value = "国家名称")
    private String nationName;

    /**
     * 报告类型
     */
    @ExcelProperty(value = "报告类型")
    private String reportType;
    /**
     * 紧急程度
     */
    @ExcelProperty(value = "速度")
    private String speed;
    /**
     * 申请时间RESPONSE_DAYS
     */
    @ExcelProperty(value = "工作日")
    private String responseDays;
    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}