/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker.normalizer;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.rawdatacentral.core.common.DataNormalizer;
import com.datatrees.rawdatacentral.core.model.Ecommerce;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.data.EcommerceData;
import com.datatrees.rawdatacentral.core.service.EcommerceService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class EcommerceMessageNormalizer implements DataNormalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EcommerceMessageNormalizer.class);

    @Resource
    private EcommerceService ecommerceService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.rawdata.collector.worker.MessageNormalizer#normalize(com.datatrees.rawdata.
     * core.model.ExtractMessage)
     */
    @Override
    public boolean normalize(Object data) {
        ExtractMessage message = ((ExtractMessage) data);
        Object object = message.getMessageObject();
        if (object instanceof EcommerceData) {
            message.setResultType(ResultType.ECOMMERCE);
            message.setTypeId(this.getEcommerceId(message));
            ((EcommerceData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if ((object instanceof HashMap && StringUtils.equals((String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES),
                EcommerceData.class.getSimpleName()))) {
            EcommerceData ecommerceData = new EcommerceData();
            ecommerceData.putAll((Map) object);
            ecommerceData.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
            message.setMessageObject(ecommerceData);
            message.setResultType(ResultType.ECOMMERCE);
            message.setTypeId(this.getEcommerceId(message));
            ecommerceData.setResultType(message.getResultType().getValue());
            return true;
        } else {
            return false;
        }
    }

    private int getEcommerceId(ExtractMessage message) {
        Ecommerce ecommerce = ecommerceService.getEcommerceByWebsiteId(message.getWebsiteId());
        if (ecommerce == null) {
            LOGGER.warn("get null ecommerce with website id " + message.getWebsiteId() + ", set default EcommerceId 0");
            return 0;
        } else {
            return ecommerce.getId();
        }
    }
}
