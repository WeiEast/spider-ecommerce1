/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.collector.worker;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.collector.actor.Collector;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.rawdatacentral.core.model.Task;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.ReissueDetectCollectorMessage;
import com.datatrees.rawdatacentral.core.model.message.impl.ReissueDetectMessage;
import com.datatrees.rawdatacentral.core.service.ExtractorResultService;
import com.datatrees.rawdatacentral.core.service.TaskService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 上午10:55:01
 */
@Service
public class ReissueDetectWorker {
    private static final Logger log = LoggerFactory.getLogger(ReissueDetectWorker.class);

    private static String startMsgPattern = PropertiesConfiguration.getInstance().get("tasklog.startMsg.extract.pattern", ",\"startMsg\":(.+)");
    private static String MAIL_REISSUE_DETECT_TEMPLATE = PropertiesConfiguration.getInstance().get("mail.reissue.detect.template",
            "mailServer-detect-template");

    @Resource
    private TaskService taskService;
    @Resource
    private Collector collector;
    @Resource
    private ExtractorResultService extractorResultService;

    public void process(ReissueDetectMessage reissueDetectMessage) {
        Task task = taskService.selectTaskByBankBillsKey(reissueDetectMessage.getUserId(), reissueDetectMessage.getBankBillsKey());
        if (task == null) {
            log.warn("can't find task with userid:" + reissueDetectMessage.getUserId() + ", bankBillsKey:" + reissueDetectMessage.getBankBillsKey());
        } else {
            CollectorMessage collectorMessage = this.getCollectorMessage(task, reissueDetectMessage);
            collector.processMessage(collectorMessage);
        }
    }

    private ReissueDetectCollectorMessage getCollectorMessage(Task task, ReissueDetectMessage reissueDetectMessage) {
        ReissueDetectCollectorMessage collectorMessage = null;
        String startMsg = null;
        String resutMsg = task.getResultMessage();
        try {
            if (StringUtils.isNotBlank(resutMsg) && StringUtils.isNotBlank(startMsg = PatternUtils.group(resutMsg, startMsgPattern, 1))) {
                collectorMessage = (ReissueDetectCollectorMessage) GsonUtils.fromJson(startMsg, ReissueDetectCollectorMessage.class);
            } else {
                log.warn("empty startMsg from with userid:" + reissueDetectMessage.getUserId() + ", bankBillsKey:"
                        + reissueDetectMessage.getBankBillsKey());
                collectorMessage = new ReissueDetectCollectorMessage();
            }
            collectorMessage.setParentTaskID(task.getId());
            collectorMessage.setFinish(false);
            collectorMessage.setTemplateId(MAIL_REISSUE_DETECT_TEMPLATE);
            collectorMessage.setLoginCheckIgnore(true);// ignore login check
            collectorMessage.getProperty().put(
                    "mailBills",
                    GsonUtils.toJson(extractorResultService.getReissueDetectMails(collectorMessage.getUserId(), task.getId(),
                            reissueDetectMessage.getMailBills())));
        } catch (Exception e) {
            log.error("getLoginInfo error, taskid:" + task.getId() + ",resutMsg:" + resutMsg, e);
        }
        return collectorMessage;
    }
}
