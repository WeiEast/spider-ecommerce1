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

package com.datatrees.spider.bank.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 运营商登陆过程中的参数
 * Created by zhouxinghai on 2017/7/13.
 */
public class MailParam implements Serializable {

    /**
     * 任务id 必填
     */
    private Long                taskId;

    /**
     * 用户名
     */
    private String              username;

    /**
     * 登陆密码或者服务密码
     */
    @JSONField(serialize = false)
    private String              password;

    /**
     * 指令id
     */
    private String              directiveId;

    /**
     * 扩展属性
     */
    private Map<String, Object> extral = new HashMap<>();

    /**
     * 给自定义plugin方法用
     */
    private String[]            args;

    public MailParam() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<String, Object> getExtral() {
        return extral;
    }

    public void setExtral(Map<String, Object> extral) {
        this.extral = extral;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public String getDirectiveId() {
        return directiveId;
    }

    public void setDirectiveId(String directiveId) {
        this.directiveId = directiveId;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
