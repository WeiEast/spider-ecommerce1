/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.collector.worker.normalizer;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.extractor.util.SourceFieldUtil;
import com.datatrees.rawdatacentral.core.common.DataNormalizer;
import com.datatrees.rawdatacentral.core.model.ExtractMessage;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.data.MailBillData;
import com.datatrees.rawdatacentral.core.service.BankService;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class MailBillMessageNormalizer implements DataNormalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MailBillMessageNormalizer.class);

    @Resource
    private BankService bankService;
    
    private String loadFileBankIds = PropertiesConfiguration.getInstance().get("need.load.file.bankids", "3");

    private List<String> loadFileBankList = null;
    private List<String> needLoadFieldList = null;
    
    {
        loadFileBankList = Arrays.asList(loadFileBankIds.split(" *; *"));
        needLoadFieldList = Arrays.asList(SubmitConstant.SUBMITTER_NEEDUPLOAD_KEY.split(" *, *"));

    }
    
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
        if (object instanceof MailBillData) {
            message.setResultType(ResultType.MAILBILL);
            message.setTypeId(this.getBankId((MailBillData) object));

            ((MailBillData) object).setBankId(message.getTypeId());
            ((MailBillData) object).setResultType(message.getResultType().getValue());
            processLoadFile((MailBillData)object);
            return true;
        } else if (object instanceof HashMap
                && StringUtils.equals((String) ((Map) object).get(Constants.SEGMENT_RESULT_CLASS_NAMES), MailBillData.class.getSimpleName())) {
            MailBillData mailBillData = new MailBillData();
            mailBillData.putAll((Map) object);
            mailBillData.remove(Constants.SEGMENT_RESULT_CLASS_NAMES);
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
            LOGGER.warn("bankid or result is empty! bankid: " + mailData.getBankId());
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
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private void loadFile(Object obj) throws Exception {
        if (obj instanceof FileWapper) {
            LOGGER.info("need load file before extractor! fileName: " + ((FileWapper) obj).getName());
            ((FileWapper) obj).getFileInputStream();
        } else if (obj instanceof Collection) {
            for (Object sub : (Collection) obj) {
                loadFile(sub);
            }
        } else {
            if (LOGGER.isDebugEnabled()) LOGGER.debug("only load file wapper and skip other type!");
        }
    }
    
    private int getBankId(MailBillData data) {
        String sender = data.getSender();
        Bank bank = null;
        if (StringUtils.isNotBlank(sender)) {
            bank = bankService.getBank(sender.trim().toLowerCase());
        }
        if (bank == null) {
            Map<String, Bank> bankEmailMap = bankService.getBankEmailMap();
            String pageContent = SourceFieldUtil.getInputFieldString(data, MailBillData.PAGECONTENT);
            for (Map.Entry<String, Bank> entry : bankEmailMap.entrySet()) {
                Pattern pattern = PatternUtils.compile(entry.getKey(), Pattern.CASE_INSENSITIVE);
                if (PatternUtils.match(pattern, pageContent)) {
                    bank = entry.getValue();
                    break;
                }
            }
        } else {
            LOGGER.info("get bank with sender" + sender + ",set mail first hand");
            data.setFirstHand(true);
        }
        if (bank == null) {
            LOGGER.warn("get null bank with data sign " + data.getUniqueSign() + ", set default bankId 0");
            return 0;
        } else {
            return bank.getBankId();
        }
    }


}
