package com.fanruan.platform.bean;

import lombok.Data;

import java.sql.Timestamp;


/**
 *   @kern
 *   @since 2022/03/20
 *  中诚信、天眼查关注历史记录
 */
@Data
public class LogConcernHistory {
    /**
     * 企业名称
     */
    private String entName;

    /**
     * 统一社会信用代码
     */
    private String code;

    /**
     * 唯一识别流水号
     */
    private String requestId;

    /**
     * 填报人USERNAME
     */
    private String updateBy;

    /**
     * 插入时间
     */
    private String insertDate;

    /**
     * 当前状态（0：取消关注 1：关注）
     */
    private String concernFlag;

    /**
     * 当前操作（0：取消关注 1：关注）
     */
    private String operateFlag;

    /**
     * 计入来源（1：天眼查 2：中诚信）
     */
    private String dataSource;

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getConcernFlag() {
        return concernFlag;
    }

    public void setConcernFlag(String concernFlag) {
        this.concernFlag = concernFlag;
    }

    public String getOperateFlag() {
        return operateFlag;
    }

    public void setOperateFlag(String operateFlag) {
        this.operateFlag = operateFlag;
    }

}
