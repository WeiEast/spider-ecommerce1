/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.worker.normalizer;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.rawdatacentral.core.model.data.DefaultData;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class DefaultMessageNormalizer implements MessageNormalizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageNormalizer.class);

    @Override
    public boolean normalize(Object data) {
        ExtractMessage message = ((ExtractMessage) data);
        Object object = ((ExtractMessage) message).getMessageObject();
        if (object instanceof DefaultData) {
            message.setResultType(ResultType.DEFAULT);
            message.setTypeId(message.getWebsiteId());
            return true;
        } else if (object instanceof HashMap &&
                StringUtils.equals((String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES), DefaultData.class.getSimpleName())) {
            DefaultData defaultData = new DefaultData();
            defaultData.putAll((Map) object);
            defaultData.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
            message.setResultType(ResultType.DEFAULT);
            message.setTypeId(message.getWebsiteId());
            message.setMessageObject(defaultData);
            return true;
        } else {
            return false;
        }
    }

}
