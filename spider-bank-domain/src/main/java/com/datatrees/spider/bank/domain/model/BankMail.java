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

package com.datatrees.spider.bank.domain.model;

import java.io.Serializable;
import java.util.Date;

/** create by system from table t_bank_email(Bank email info) */
public class BankMail implements Serializable {

    private static final long    serialVersionUID = 1L;

    /** bank Id */
    private              Integer id;

    /** bank Id */
    private              Integer bankId;

    /**  */
    private              String  bankEmailAddr;

    /**  */
    private              Date    createdAt;

    /**  */
    private              Date    updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankEmailAddr() {
        return bankEmailAddr;
    }

    public void setBankEmailAddr(String bankEmailAddr) {
        this.bankEmailAddr = bankEmailAddr == null ? null : bankEmailAddr.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}