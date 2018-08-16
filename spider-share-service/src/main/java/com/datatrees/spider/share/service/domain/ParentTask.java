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

package com.datatrees.spider.share.service.domain;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.spider.share.domain.CollectorMessage;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月20日 上午12:36:15
 */
public class ParentTask {

    private int                    taskId;

    private String                 cookie;

    private String                 websiteName;

    private CollectorMessage       collectorMessage;

    private Map<String, Object>    property = new HashMap<String, Object>();

    private SearchProcessorContext processorContext;

    /**
     * @return the processorContext
     */
    public SearchProcessorContext getProcessorContext() {
        return processorContext;
    }

    /**
     * @param processorContext the processorContext to set
     */
    public void setProcessorContext(SearchProcessorContext processorContext) {
        this.processorContext = processorContext;
    }

    /**
     * @return the taskId
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the cookie
     */
    public String getCookie() {
        return cookie;
    }

    /**
     * @param cookie the cookie to set
     */
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    /**
     * @return the property
     */
    public Map<String, Object> getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(Map<String, Object> property) {
        this.property = property;
    }

    /**
     * @return the collectorMessage
     */
    public CollectorMessage getCollectorMessage() {
        return collectorMessage;
    }

    /**
     * @param collectorMessage the collectorMessage to set
     */
    public void setCollectorMessage(CollectorMessage collectorMessage) {
        this.collectorMessage = collectorMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParentTask [taskId=" + taskId + ", websiteName=" + websiteName + ", property=" + property + "]";
    }

}
