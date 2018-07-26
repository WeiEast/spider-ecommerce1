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
