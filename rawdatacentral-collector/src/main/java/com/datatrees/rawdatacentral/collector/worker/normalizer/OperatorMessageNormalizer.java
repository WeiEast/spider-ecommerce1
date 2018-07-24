/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.worker.normalizer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.data.OperatorData;
import com.datatrees.rawdatacentral.domain.model.Operator;
import com.datatrees.rawdatacentral.service.OperatorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class OperatorMessageNormalizer implements MessageNormalizer {

    private static final Logger          LOGGER = LoggerFactory.getLogger(OperatorMessageNormalizer.class);

    @Resource
    private              OperatorService operatorService;

    @Override
    public boolean normalize(Object data) {
        ExtractMessage message = ((ExtractMessage) data);
        Object object = message.getMessageObject();
        if (object instanceof OperatorData) {
            message.setResultType(ResultType.OPERATOR);
            message.setTypeId(this.getOperatorId(message));
            ((OperatorData) object).setOperatorId(message.getTypeId());
            ((OperatorData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if (object instanceof HashMap &&
                StringUtils.equals((String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES), OperatorData.class.getSimpleName())) {
            OperatorData operatorData = new OperatorData();
            operatorData.putAll((Map) object);
            operatorData.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
            message.setMessageObject(operatorData);
            message.setResultType(ResultType.OPERATOR);
            message.setTypeId(this.getOperatorId(message));

            operatorData.setOperatorId(message.getTypeId());
            operatorData.setResultType(message.getResultType().getValue());
            return true;
        } else {
            return false;
        }
    }

    private int getOperatorId(ExtractMessage message) {
        Operator operator = operatorService.getByWebsiteId(message.getWebsiteId());
        if (operator == null) {
            LOGGER.warn("operator not found websiteId={}", message.getWebsiteId());
            return 0;
        }
        return operator.getId();
    }
}
