/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.bank.service.normalizer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.rawdatacentral.core.model.SubmitMessage;
import com.datatrees.spider.share.service.normalizers.SubmitNormalizer;
import com.datatrees.spider.bank.domain.model.MailExtractResult;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月4日 下午5:21:18
 */
@Service
public class MailBillSubmitDataNormalizer implements SubmitNormalizer {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean normalize(Object data) {
        SubmitMessage message = ((SubmitMessage) data);
        if (message.getExtractMessage().getResultType().equals(ResultType.MAILBILL)) {
            MailExtractResult result = (MailExtractResult) message.getResult();
            Set<Map.Entry<String, Object>> entrySet = message.getExtractResultMap().entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                if (entry.getValue() instanceof Collection) {
                    for (Map map : (Collection<Map>) entry.getValue()) {
                        this.mailNormalize(map, result);
                    }
                } else if (entry.getValue() instanceof Map) {
                    Map map = (Map) entry.getValue();
                    this.mailNormalize(map, result);
                }
            }
            return true;
        }
        return false;
    }

    private void mailNormalize(Map map, MailExtractResult result) {
        map.put("BankId", result.getBankId());
        map.put("ReceiveAt", result.getReceiveAt());
        map.put("UUID", result.getUniqueMd5());
        map.put("FirstHand", result.getFirstHand());
        map.put("PageExtractId", result.getPageExtractId());// Distinguish between jianban &
        // xiangban
        map.put("ExtraInfo", result.getExtraInfo());
        map.put("MailHeader", null == result.getMailHeader() ? "" : result.getMailHeader());
    }
}
