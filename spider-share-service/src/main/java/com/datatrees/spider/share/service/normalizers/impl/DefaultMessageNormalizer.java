/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.normalizers.impl;

import java.util.Map;

import com.datatrees.spider.share.domain.DefaultData;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.treefinance.crawler.framework.process.domain.ExtractObject;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class DefaultMessageNormalizer implements MessageNormalizer {

    @Override
    public boolean normalize(ExtractMessage message) {
        Object object = message.getMessageObject();
        if (object instanceof DefaultData) {
            message.setResultType(ResultType.DEFAULT);
            message.setTypeId(message.getWebsiteId());
            return true;
        } else if (object instanceof ExtractObject && DefaultData.class.getSimpleName().equals(((ExtractObject) object).getResultClass())) {
            DefaultData defaultData = new DefaultData();
            defaultData.putAll((Map) object);

            message.setResultType(ResultType.DEFAULT);
            message.setTypeId(message.getWebsiteId());
            message.setMessageObject(defaultData);
            return true;
        } else {
            return false;
        }
    }

}
