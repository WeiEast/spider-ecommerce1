/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker.normalizer;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.rawdatacentral.core.common.DataNormalizer;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.data.EBankData;
import com.datatrees.rawdatacentral.service.BankService;
import com.datatrees.rawdatacentral.domain.model.Bank;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class EbankMessageNormalizer implements DataNormalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EbankMessageNormalizer.class);

    @Resource
    private BankService         bankService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.rawdatacentral.collector.worker.MessageNormalizer#normalize(com.datatrees.rawdatacentral.
     * core.model.ExtractMessage)
     */
    @Override
    public boolean normalize(Object data) {
        ExtractMessage message = ((ExtractMessage) data);
        Object object = ((ExtractMessage) message).getMessageObject();
        if (object instanceof EBankData) {
            message.setResultType(ResultType.EBANKBILL);
            message.setTypeId(this.getBankId(message));
            ((EBankData) object).setBankId(message.getTypeId());
            ((EBankData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if (object instanceof HashMap && StringUtils.equals(
            (String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES), EBankData.class.getSimpleName())) {
            EBankData eBankData = new EBankData();
            eBankData.putAll((Map) object);
            eBankData.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
            message.setResultType(ResultType.EBANKBILL);
            message.setTypeId(this.getBankId(message));
            message.setMessageObject(eBankData);
            eBankData.setBankId(message.getTypeId());
            eBankData.setResultType(message.getResultType().getValue());
            return true;
        } else {
            return false;
        }
    }

    private int getBankId(ExtractMessage message) {
        Bank bank = bankService.getByWebsiteIdFromCache(message.getWebsiteId());
        if (bank == null) {
            LOGGER.warn("bank not found websiteId={}", message.getWebsiteId());
            return 0;
        }
        return bank.getBankId();
    }

}
