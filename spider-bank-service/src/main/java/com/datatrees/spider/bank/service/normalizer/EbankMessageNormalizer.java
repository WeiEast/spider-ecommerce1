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

package com.datatrees.spider.bank.service.normalizer;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.spider.bank.domain.EBankData;
import com.datatrees.spider.bank.domain.model.Bank;
import com.datatrees.spider.bank.service.BankService;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.treefinance.crawler.framework.process.domain.ExtractObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class EbankMessageNormalizer implements MessageNormalizer {

    private static final Logger      LOGGER = LoggerFactory.getLogger(EbankMessageNormalizer.class);

    @Resource
    private              BankService bankService;

    @Override
    public boolean normalize(ExtractMessage message) {
        Object object = message.getMessageObject();
        if (object instanceof EBankData) {
            message.setResultType(ResultType.EBANKBILL);
            message.setTypeId(this.getBankId(message));
            ((EBankData) object).setBankId(message.getTypeId());
            ((EBankData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if (object instanceof ExtractObject && EBankData.class.getSimpleName().equals(((ExtractObject) object).getResultClass())) {
            EBankData eBankData = new EBankData();
            eBankData.putAll((Map) object);

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
