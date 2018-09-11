/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.service.extract;

import javax.annotation.Resource;

import com.datatrees.spider.share.service.domain.data.MailBillData;
import com.datatrees.spider.bank.dao.MailExtractResultDAO;
import com.datatrees.spider.bank.domain.model.MailExtractResult;
import com.datatrees.spider.share.domain.AbstractExtractResult;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.extract.ExtractResultHandler;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

@Component
public class MailExtractResultHandler implements ExtractResultHandler {

    @Resource
    private MailExtractResultDAO mailExtractResultDao;

    @Override
    public ResultType getSupportResultType() {
        return ResultType.MAILBILL;
    }

    @Override
    public AbstractExtractResult build(ExtractMessage extractMessage) {
        Object object = extractMessage.getMessageObject();
        MailExtractResult result = new MailExtractResult();
        result.setBankId(extractMessage.getTypeId());
        result.setReceiveAt(((MailBillData) object).getReceiveAt());
        result.setSubject(((MailBillData) object).getSubject());
        result.setSender(((MailBillData) object).getSender());
        result.setReceiver(((MailBillData) object).getReceiver());
        result.setUniqueSign(((MailBillData) object).getUniqueSign());
        result.setUrl(((MailBillData) object).getUrl());
        result.setFirstHand(BooleanUtils.isTrue(((MailBillData) object).getFirstHand()));
        result.setExtraInfo(((MailBillData) object).getExtraInfo());
        result.setMailHeader(((MailBillData) object).getMailHeader());
        return result;
    }

    @Override
    public Class<? extends AbstractExtractResult> getSupportResult() {
        return MailExtractResult.class;
    }

    @Override
    public void save(AbstractExtractResult result) {
        mailExtractResultDao.insert((MailExtractResult) result);
    }
}
