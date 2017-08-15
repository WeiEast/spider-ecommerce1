package com.datatrees.rawdatacentral.domain.model.base;

import com.datatrees.rawdatacentral.domain.enums.ErrorCode;

/**
 * Created by zhouxinghai on 2017/7/4.
 */
public abstract class AbstractTask {

    protected String  websiteName;

    /**
     * 是否子任务
     */
    protected boolean isSubTask = false;

    private boolean   isDuplicateRemoved;

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
                    this.setRemark(errorCode.getErrorMessage());
                }
            }
        }
    }

    public abstract void setRemark(String remark);

    public abstract void setStatus(Integer status);

    public abstract Integer getStatus();

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
