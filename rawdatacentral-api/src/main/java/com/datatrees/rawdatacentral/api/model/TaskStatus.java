package com.datatrees.rawdatacentral.api.model;

public class TaskStatus {
    private int id;
    private int userId;
    private String websiteName;
    private String status;
    private int taskSequence;
    private String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTaskSequence() {
        return taskSequence;
    }

    public void setTaskSequence(int taskSequence) {
        this.taskSequence = taskSequence;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "TaskStatus[id:" + id + ",userId:" + userId + ",websiteName:" + websiteName + ",status:" + status + ",remark" + remark + "]";
    }
}
