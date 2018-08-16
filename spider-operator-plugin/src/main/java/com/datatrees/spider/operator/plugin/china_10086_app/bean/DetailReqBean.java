/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.operator.plugin.china_10086_app.bean;

import java.io.Serializable;

/**
 * 查询详单 reqBody对应的Bean
 * Created by guimeichao on 18/3/28.
 */
public class DetailReqBean implements Serializable {

    private String billMonth = null;

    private String cellNum   = null;

    private int    page;

    private String tmemType  = null;

    private int    unit;

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
