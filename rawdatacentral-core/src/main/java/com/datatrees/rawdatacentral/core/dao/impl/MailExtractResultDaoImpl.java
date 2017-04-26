package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datatrees.rawdatacentral.core.dao.MailExtractResultDao;
import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.domain.model.MailExtractResult;

/**
 * Created by wuminlang on 15/7/29.
 */
@Component
public class MailExtractResultDaoImpl extends BaseDao implements MailExtractResultDao {

    @Override
    public int insertMailExtractResult(MailExtractResult result) {
        return (int) sqlMapClientTemplate.insert("MailExtractResult.insert", result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see MailExtractResultDao#getUserSuccessParseDMail(int)
     */
    @Override
    public List<String> getUserSuccessParsedMailKeySet(int userId) {
        return sqlMapClientTemplate.queryForList("MailExtractResult.getUserSuccessParsedMailKeySet", userId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see MailExtractResultDao#getReissueDetectMails(int, int,
     * java.util.Set)
     */
    @Override
    public List<Map> getReissueDetectMails(int userId, int taskid, Set<String> mailIds) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("taskId", taskid);
        map.put("mailIds", mailIds.toArray());
        return (List<Map>) sqlMapClientTemplate.queryForList("MailExtractResult.getReissueDetectMails", map);
    }
}
