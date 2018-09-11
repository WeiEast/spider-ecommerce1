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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.spider.bank.service.BankService;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.constants.SubmitConstant;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.domain.data.MailBillData;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.treefinance.crawler.exception.UncheckedInterruptedException;
import com.treefinance.crawler.framework.download.WrappedFile;
import com.treefinance.crawler.framework.process.domain.ExtractObject;
import com.treefinance.crawler.framework.util.FieldUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class MailBillMessageNormalizer implements MessageNormalizer {

    private static final Logger       logger            = LoggerFactory.getLogger(MailBillMessageNormalizer.class);
    @Resource
    private              BankService  bankService;
    private              String       loadFileBankIds   = PropertiesConfiguration.getInstance().get("need.load.file.bankids", "3");

    private              List<String> loadFileBankList  = Arrays.asList(loadFileBankIds.split(" *; *"));

    private              List<String> needLoadFieldList = Arrays.asList(SubmitConstant.SUBMITTER_NEEDUPLOAD_KEY.split(" *, *"));


    @Override
    public boolean normalize(ExtractMessage message) {
        Object object = message.getMessageObject();
        if (object instanceof MailBillData) {
            message.setResultType(ResultType.MAILBILL);
            message.setTypeId(this.getBankId((MailBillData) object));

            ((MailBillData) object).setBankId(message.getTypeId());
            ((MailBillData) object).setResultType(message.getResultType().getValue());
            processLoadFile((MailBillData) object);
            return true;
        } else if (object instanceof ExtractObject && MailBillData.class.getSimpleName().equals(((ExtractObject) object).getResultClass())) {
            MailBillData mailBillData = new MailBillData();
            mailBillData.putAll((Map) object);

            message.setResultType(ResultType.MAILBILL);
            message.setTypeId(this.getBankId(mailBillData));
            message.setMessageObject(mailBillData);

            mailBillData.setBankId(message.getTypeId());
            mailBillData.setResultType(message.getResultType().getValue());
            processLoadFile(mailBillData);
            return true;
        } else {
            return false;
        }
    }

    private void processLoadFile(MailBillData mailData) {
        if (mailData == null || mailData.getBankId() == null || !loadFileBankList.contains(mailData.getBankId().toString())) {
            logger.warn("bankid or result is empty! bankid: " + mailData.getBankId());
            return;
        }
        for (Map.Entry<String, Object> entry : ((Map<String, Object>) mailData).entrySet()) {
            try {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null && (needLoadFieldList.contains(key) || key.endsWith("File"))) {
                    loadFile(value);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void loadFile(Object obj) throws Exception {
        if (obj instanceof WrappedFile) {
            logger.info("need load file before extractor! fileName: {}", ((WrappedFile) obj).getName());
            ((WrappedFile) obj).download();
        } else if (obj instanceof Collection) {
            for (Object sub : (Collection) obj) {
                loadFile(sub);
            }
        } else {
            logger.debug("only load file wrapper and skip other type!");
        }
    }

    private int getBankId(MailBillData data) {
        String sender = data.getSender();
        Integer bankId = 0;
        Map<String, Integer> mailBankMap = bankService.getMailBankMap();
        if (StringUtils.isNotBlank(sender)) {
            sender = sender.trim().toLowerCase();
            if (mailBankMap.containsKey(sender)) {
                bankId = mailBankMap.get(sender);
                logger.info("get bank success by sender,sender={},bankId={}", sender, bankId);
                data.setFirstHand(true);
                return bankId;
            }
        }

        String pageContent;
        try {
            pageContent = FieldUtils.getFieldValueAsString(data, MailBillData.PAGECONTENT);
        } catch (UncheckedInterruptedException e) {
            logger.warn(e.getMessage(), e);
            pageContent = StringUtils.EMPTY;
        }
        for (Map.Entry<String, Integer> entry : mailBankMap.entrySet()) {
            Pattern pattern = PatternUtils.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
            if (PatternUtils.match(pattern, pageContent)) {
                logger.info("get bank by pageContent,mail={},bankId={}", entry.getKey(), entry.getValue());
                return entry.getValue();
            }
        }
        logger.warn("bankId not found data");
        return bankId;
    }

}
