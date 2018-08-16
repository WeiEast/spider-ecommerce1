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

package com.datatrees.spider.share.domain;

import com.datatrees.spider.share.domain.ErrorCode;

/**
 * Created by zhouxinghai on 2017/7/4.
 */
public abstract class AbstractTask {

    protected String  websiteName;

    /**
     * 是否子任务
     */
    private   boolean isSubTask = false;

    private   boolean isDuplicateRemoved;

    public void setErrorCode(ErrorCode errorCode) {
        this.setErrorCode(errorCode, null);
    }

    public void setErrorCode(ErrorCode errorCode, String message) {
        synchronized (this) {
            if (null == getStatus() || 0 == getStatus() || errorCode.getErrorCode() < getStatus()) {
                this.setStatus(errorCode.getErrorCode());
                if (message != null) {
                    this.setRemark(message);
                } else {
                    this.setRemark(errorCode.getErrorMsg());
                }
            }
        }
    }

    public abstract void setRemark(String remark);

    public abstract Integer getStatus();

    public abstract void setStatus(Integer status);

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public boolean isSubTask() {
        return isSubTask;
    }

    public void setSubTask(boolean subTask) {
        isSubTask = subTask;
    }

    public boolean isDuplicateRemoved() {
        return isDuplicateRemoved;
    }

    public void setDuplicateRemoved(boolean duplicateRemoved) {
        isDuplicateRemoved = duplicateRemoved;
    }
}
