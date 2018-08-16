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

import com.datatrees.spider.share.service.domain.SubTaskAble;
import com.datatrees.spider.share.service.domain.SubSeed;
import com.datatrees.spider.share.domain.CollectorMessage;
import org.apache.commons.lang.BooleanUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午4:14:22
 */
public class SubTaskCollectorMessage extends CollectorMessage implements SubTaskAble {

    private String  templateId;

    private int     parentTaskID;

    private boolean synced;

    private SubSeed subSeed;

    /**
     * @return the subSeed
     */
    public SubSeed getSubSeed() {
        return subSeed;
    }

    /**
     * @param subSeed the subSeed to set
     */
    public void setSubSeed(SubSeed subSeed) {
        this.subSeed = subSeed;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId the templateId to set
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the parentTaskID
     */
    public int getParentTaskID() {
        return parentTaskID;
    }

    /**
     * @param parentTaskID the parentTaskID to set
     */
    public void setParentTaskID(int parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    /**
     * @return the synced
     */
    public boolean isSynced() {
        return synced;
    }

    /**
     * @param synced the synced to set
     */
    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    /*
     * (non-Javadoc)
     *
     * @see SubTaskAble#noStatusSend()
     */
    @Override
    public boolean noStatus() {
        return subSeed != null && BooleanUtils.isTrue(subSeed.noStatus());
    }

}
