/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin;

import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.service.MessageService;
import com.datatrees.spider.share.service.MonitorService;

/**
 * @author Jerry
 * @date 2019-02-15 13:39
 */
public final class QRLoginMonitor {

    private static MonitorService monitorService;

    private static MessageService messageService;

    private QRLoginMonitor() {}

    private static MonitorService getMonitorService() {
        if (monitorService == null) {
            monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        }
        return monitorService;
    }

    private static MessageService getMessageService() {
        if (messageService == null) {
            messageService = BeanFactoryUtils.getBean(MessageService.class);
        }
        return messageService;
    }

    public static void notifyLogger(CommonPluginParam param, String taskMsg, String monitorMsg) {
        notifyLogger(param, taskMsg, monitorMsg, null, null);
    }

    public static void notifyLogger(CommonPluginParam param, String taskMsg, String monitorMsg, ErrorCode monitorErrorCode, String monitorError) {
        sendTaskLog(param.getTaskId(), taskMsg);

        String msg = TemplateUtils.format("{}-->{}", FormType.getName(FormType.LOGIN), monitorMsg);
        if (monitorErrorCode == null) {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg);
        } else if (monitorError == null) {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg, monitorErrorCode);
        } else {
            getMonitorService().sendTaskLog(param.getTaskId(), param.getWebsiteName(), msg, monitorErrorCode, monitorError);
        }
    }

    public static void sendTaskLog(Long taskId, String msg) {
        getMessageService().sendTaskLog(taskId, msg);
    }
}
