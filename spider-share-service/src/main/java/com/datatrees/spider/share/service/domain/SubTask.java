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

/**
 * @author <A HREF="">Cheng Wang</A>
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
