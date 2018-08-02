/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.bank.service.normalizer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.rawdatacentral.core.model.data.EBankData;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.service.BankService;
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
public class EbankMessageNormalizer implements MessageNormalizer {

    private static final Logger      LOGGER = LoggerFactory.getLogger(EbankMessageNormalizer.class);

    @Resource
    private              BankService bankService;

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
        } else if (object instanceof HashMap &&
                StringUtils.equals((String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES), EBankData.class.getSimpleName())) {
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
        //需要测试websiteName能否
        Bank bank = bankService.getByWebsiteName(message.getWebsiteName());
        if (bank == null) {
            LOGGER.warn("bank not found websiteId={}", message.getWebsiteId());
            return 0;
        }
        return bank.getBankId();
    }

}
