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

package com.datatrees.spider.share.service.collector.actor;

import java.util.Map;
import java.util.Objects;

import com.datatrees.spider.share.domain.CollectorMessage;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.model.Task;
import com.datatrees.spider.share.domain.website.WebsiteType;
import com.datatrees.spider.share.service.domain.SubTaskAble;
import com.datatrees.spider.share.service.domain.TaskRelated;
import com.datatrees.spider.share.service.domain.TemplteAble;
import com.datatrees.spider.share.service.util.UnifiedSysTime;
import com.treefinance.crawler.framework.context.SearchProcessorContext;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午5:47:32
 */
public class TaskMessage {

    private final Task task;

    private final SearchProcessorContext context;

    private Boolean messageSend;

    private Integer parentTaskId;

    private CollectorMessage collectorMessage;

    private String templateId;

    private String uniqueSuffix;

    private Boolean statusSend;

    public TaskMessage(final Task task, final SearchProcessorContext context) {
        this.task = Objects.requireNonNull(task);
        this.context = Objects.requireNonNull(context);
        this.messageSend = true;
        this.statusSend = true;
    }

    public Task getTask() {
        return task;
    }

    public Integer getProcessId() {
        return task.getId();
    }

    public SearchProcessorContext getContext() {
        return context;
    }

    public Long getTaskId() {
        return context.getTaskId();
    }

    public String getWebsiteName() {
        return context.getWebsiteName();
    }

    public WebsiteType getWebsiteType() {
        return WebsiteType.getWebsiteType(context.getWebsiteType());
    }

    public CollectorMessage getCollectorMessage() {
        return collectorMessage;
    }

    public void setCollectorMessage(CollectorMessage message) {
        this.collectorMessage = message;

        if (message instanceof TemplteAble) {
            this.templateId = ((TemplteAble) message).getTemplateId();
        }
        if (message instanceof TaskRelated) {
            this.parentTaskId = ((TaskRelated) message).getParentTaskId();
            context.setAttribute("parentTaskLogId", this.parentTaskId);
        }
        // set subtask parameter
        if (message instanceof SubTaskAble) {
            //标记子任务
            markSubtask(!((SubTaskAble) message).isSynced(), !((SubTaskAble) message).noStatus());

            if (((SubTaskAble) message).getSubSeed() != null) {
                this.uniqueSuffix = ((SubTaskAble) message).getSubSeed().getUniqueSuffix();
            }
        }
    }

    private void markSubtask(boolean messageSend, boolean statusSend) {
        task.setSubTask(true);
        this.messageSend = messageSend;
        this.statusSend = statusSend;
    }

    public String getTemplateId() {
        return templateId;
    }

    public int getParentTaskId() {
        return parentTaskId;
    }

    public Boolean getMessageSend() {
        return messageSend;
    }

    public Boolean getStatusSend() {
        return statusSend;
    }

    public String getUniqueSuffix() {
        return uniqueSuffix;
    }


    @Override
    public String toString() {
        return "TaskMessage [websiteName=" + getWebsiteName() + ", templateId=" + templateId + ", messageSend=" + messageSend + ", parentTaskID=" + parentTaskId + "]";
    }

    public void setErrorCode(ErrorCode errorCode) {
        task.setErrorCode(errorCode);
    }

    public void setErrorCode(ErrorCode errorCode, String message) {
        task.setErrorCode(errorCode, message);
    }

    public void completeSearch(HttpResult<Map<String, Object>> searchResult) {
        Task task = getTask();
        task.setFinishedAt(UnifiedSysTime.INSTANCE.getSystemTime());
        task.setDuration((task.getFinishedAt().getTime() - task.getStartedAt().getTime()) / 1000);
        //释放代理
        if (searchResult.getResponseCode() != ErrorCode.TASK_INTERRUPTED_ERROR.getErrorCode()) {
            getContext().release();
        }
    }

}
