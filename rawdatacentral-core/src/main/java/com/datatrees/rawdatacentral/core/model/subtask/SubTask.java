/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.core.model.subtask;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月19日 下午3:57:07
 */
public class SubTask {

    private long       taskId;

    private ParentTask parentTask;

    private SubSeed    seed;

    private long       submitAt;

    /**
     *
     */
    public SubTask() {
        super();
    }

    public SubTask(long taskId, ParentTask parentTask, SubSeed seed) {
        super();
        this.taskId = taskId;
        this.parentTask = parentTask;
        this.seed = seed;
    }

    /**
     * @return the submitAt
     */
    public long getSubmitAt() {
        return submitAt;
    }

    /**
     * @param submitAt the submitAt to set
     */
    public void setSubmitAt(long submitAt) {
        this.submitAt = submitAt;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the parentTask
     */
    public ParentTask getParentTask() {
        return parentTask;
    }

    /**
     * @param parentTask the parentTask to set
     */
    public void setParentTask(ParentTask parentTask) {
        this.parentTask = parentTask;
    }

    /**
     * @return the seed
     */
    public SubSeed getSeed() {
        return seed;
    }

    /**
     * @param seed the seed to set
     */
    public void setSeed(SubSeed seed) {
        this.seed = seed;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SubTask [taskId=" + taskId + ", parentTask=" + parentTask + ", seed=" + seed + "]";
    }

}
