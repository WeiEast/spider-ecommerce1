package com.datatrees.rawdatacentral.plugin.operator.china_10086_app.bean;

import java.io.Serializable;

/**
 * 查询账单 reqBody对应的Bean
 * Created by guimeichao on 18/3/28.
 */
public class BillReqBean implements Serializable {

    private String bgnMonth = null;

    private String cellNum  = null;

    private String endMonth = null;

    private String qryMonth = null;

    public String getCellNum() {
        return this.cellNum;
    }

    public void setCellNum(String cellNum) {
        this.cellNum = cellNum;
    }

    public String getQryMonth() {
        return this.qryMonth;
    }

    public void setQryMonth(String qryMonth) {
        this.qryMonth = qryMonth;
    }

    public String getEndMonth() {
        return this.endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public String getBgnMonth() {
        return this.bgnMonth;
    }

    public void setBgnMonth(String bgnMonth) {
        this.bgnMonth = bgnMonth;
    }
}
