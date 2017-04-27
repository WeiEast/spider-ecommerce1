/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.datatrees.rawdatacentral.core.dao.TaskDao;
import com.datatrees.rawdatacentral.domain.common.Task;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 上午12:11:31
 */
@Component
public class TaskDaoImpl extends BaseDao implements TaskDao {

    /*
     * (non-Javadoc)
     * 
     * @see TaskDao#insertTask(Task)
     */
    @Override
    public int insertTask(Task task) {
        return (int) sqlMapClientTemplate.insert("Task.insertTask", task);
    }

    /*
     * (non-Javadoc)
     * 
     * @see TaskDao#updateTask(Task)
     */
    @Override
    public void updateTask(Task task) {
        sqlMapClientTemplate.update("Task.updateTask", task);
    }

    /*
     * (non-Javadoc)
     * 
     * @see TaskDao#selectNow()
     */
    @Override
    public Date selectNow() {
        return (Date) sqlMapClientTemplate.queryForObject("Task.selectNow");
    }


}
