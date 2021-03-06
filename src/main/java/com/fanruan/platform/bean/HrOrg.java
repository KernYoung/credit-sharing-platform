package com.fanruan.platform.bean;

import lombok.Data;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class HrOrg {
    private String code;
    private String name;
    private String spkOrg;
    private Integer enableState;
    private String shortName;
    private String scode;

    private String sname;

    private String ts;

    private String orgType;

    private Integer dr;

    private String updateTime;

    private String updateTimeBy;

    private String pkOrg;

    private String rule;

    private String flag;

    private List<HrOrg> childHrOrg = new ArrayList<>();


}