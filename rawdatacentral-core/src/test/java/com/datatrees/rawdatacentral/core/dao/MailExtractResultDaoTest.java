package com.datatrees.rawdatacentral.core.dao;

import com.datatrees.rawdatacentral.core.AbstractTest;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.domain.model.MailExtractResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by wuminlang on 15/7/29.
 */
public class MailExtractResultDaoTest extends AbstractTest {

    @Autowired
    private MailExtractResultDao mailExtractResultDao;

    @Test
    public void doTest(){
        MailExtractResult domain = new MailExtractResult();
        MailExtractResult mailExtractResult = new MailExtractResult();
        mailExtractResult.setUserId(1);
        mailExtractResult.setTaskId(1);
        mailExtractResult.setWebsiteId(1);
        mailExtractResult.setUniqueSign("test sign");
        mailExtractResult.setResultType(ResultType.MAILBILL.getValue());
        mailExtractResult.setStatus(0);
        mailExtractResult.setRemark("Remark");
        mailExtractResult.setBankId(1);
        mailExtractResult.setSender("123@xxx.com");
        mailExtractResult.setSubject("中信账单");
        mailExtractResult.setReceiveAt(new Date());
        mailExtractResult.setStoragePath("/date/filename");
        mailExtractResultDao.insertMailExtractResult(domain);

    }
}
