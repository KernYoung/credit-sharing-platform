package com.fanruan.platform.bean;
import lombok.Data;

/**
 *   @kern
 *   @since 2022/04/13
 *  天眼查下发接口限制调用次数
 */
@Data
public class InterfaceUsedLimit {

    private int id;
    private String companyName;
    private String interfaceName;
    private String jsonFlag;
    private int limitNumber;
    private String remark;
    private String insertDate;
    private String updateDate;

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getJsonFlag() {
        return jsonFlag;
    }

    public void setJsonFlag(String jsonFlag) {
        this.jsonFlag = jsonFlag;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getLimitNumber() {
        return limitNumber;
    }

    public void setLimitNumber(int limitNumber) {
        this.limitNumber = limitNumber;
    }
}
