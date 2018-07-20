package com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean;

import java.io.Serializable;

/**
 * 查询详单 reqBody对应的Bean
 * Created by guimeichao on 18/3/28.
 */
public class DetailReqBean implements Serializable {
    private String billMonth = null;
    private String cellNum = null;
    private int page;
    private String tmemType = null;
    private int unit;

    public String getCellNum() {
        return this.cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    public String getBillMonth() {
        return this.billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public String getTmemType() {
        return this.tmemType;
    }

    public void setTmemType(String tmemType) {
        this.tmemType = tmemType;
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getUnit() {
        return this.unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
