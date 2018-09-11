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
