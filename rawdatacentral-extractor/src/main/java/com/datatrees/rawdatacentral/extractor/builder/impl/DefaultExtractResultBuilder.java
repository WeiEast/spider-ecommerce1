/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.extractor.builder.impl;

import com.datatrees.rawdatacentral.extractor.builder.ExtractResultBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.core.common.UniqueKeyGenUtil;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.data.DefaultData;
import com.datatrees.rawdatacentral.core.model.data.EBankData;
import com.datatrees.rawdatacentral.core.model.data.EcommerceData;
import com.datatrees.rawdatacentral.core.model.data.MailBillData;
import com.datatrees.rawdatacentral.core.model.data.OperatorData;
import com.datatrees.rawdatacentral.domain.result.AbstractExtractResult;
import com.datatrees.rawdatacentral.domain.model.DefaultExtractResult;
import com.datatrees.rawdatacentral.domain.model.EBankExtractResult;
import com.datatrees.rawdatacentral.domain.model.EcommerceExtractResult;
import com.datatrees.rawdatacentral.domain.model.MailExtractResult;
import com.datatrees.rawdatacentral.domain.model.OperatorExtractResult;
import com.datatrees.rawdatacentral.extractor.common.StoragePathUtil;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午9:09:05
 */
@Service
public class DefaultExtractResultBuilder implements ExtractResultBuilder {

    /*
     * (non-Javadoc)
     * 
     * @see
     * ExtractResultBuilder#buildExtractResult(com.datatrees
     * .rawdatacentral.core.model.ExtractMessage)
     */
    @Override
    public AbstractExtractResult buildExtractResult(ExtractMessage extractMessage) {
        ResultType resultType = extractMessage.getResultType();
        Object object = extractMessage.getMessageObject();
        AbstractExtractResult result = null;
        switch (resultType) {
            case MAILBILL:
                result = new MailExtractResult();
                ((MailExtractResult) result).setBankId(extractMessage.getTypeId());
                ((MailExtractResult) result).setReceiveAt(((MailBillData) object).getReceiveAt());
                ((MailExtractResult) result).setSubject(((MailBillData) object).getSubject());
                ((MailExtractResult) result).setSender(((MailBillData) object).getSender());
                ((MailExtractResult) result).setReceiver(((MailBillData) object).getReceiver());
                ((MailExtractResult) result).setUniqueSign(((MailBillData) object).getUniqueSign());
                ((MailExtractResult) result).setUrl(((MailBillData) object).getUrl());
                ((MailExtractResult) result).setFirstHand(BooleanUtils.isTrue(((MailBillData) object).getFirstHand()));
                ((MailExtractResult) result).setExtraInfo(((MailBillData) object).getExtraInfo());
                ((MailExtractResult) result).setMailHeader(((MailBillData) object).getMailHeader());
                break;
            case OPERATOR:
                result = new OperatorExtractResult();
                ((OperatorExtractResult) result).setOperatorId(extractMessage.getTypeId());
                ((OperatorExtractResult) result).setUrl(((OperatorData) object).getUrl());
                ((OperatorExtractResult) result).setUniqueSign(((OperatorData) object).getUniqueSign());
                ((OperatorExtractResult) result).setExtraInfo(((OperatorData) object).getExtraInfo());
                break;
            case ECOMMERCE:
                result = new EcommerceExtractResult();
                ((EcommerceExtractResult) result).setEcommerceId(extractMessage.getTypeId());
                ((EcommerceExtractResult) result).setUniqueSign(((EcommerceData) object).getUniqueSign());
                ((EcommerceExtractResult) result).setUrl(((EcommerceData) object).getUrl());
                ((EcommerceExtractResult) result).setExtraInfo(((EcommerceData) object).getExtraInfo());
                break;
            case EBANKBILL:
                result = new EBankExtractResult();
                ((EBankExtractResult) result).setBankId(extractMessage.getTypeId());
                ((EBankExtractResult) result).setUniqueSign(((EBankData) object).getUniqueSign());
                ((EBankExtractResult) result).setUrl(((EBankData) object).getUrl());
                ((EBankExtractResult) result).setExtraInfo(((EBankData) object).getExtraInfo());
                break;
            case DEFAULT:
                result = new DefaultExtractResult();
                ((DefaultExtractResult) result).setUrl(((DefaultData) object).getUrl());
                ((DefaultExtractResult) result).setUniqueSign(((DefaultData) object).getUniqueSign());
                ((DefaultExtractResult) result).setExtraInfo(((DefaultData) object).getExtraInfo());
                break;
            default:
                return null;
        }
        result.setUniqueMd5(UniqueKeyGenUtil.uniqueKeyGen(result.getUniqueSign()));
        result.setTaskId(extractMessage.getTaskId());
        result.setWebsiteId(extractMessage.getWebsiteId());
        result.setStoragePath(StoragePathUtil.genStoragePath(extractMessage, result.getUniqueMd5()));
        return result;
    }
}
