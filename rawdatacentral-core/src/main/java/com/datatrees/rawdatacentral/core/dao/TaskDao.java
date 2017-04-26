/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao;

import java.util.Date;
import java.util.List;

import com.datatrees.rawdatacentral.domain.common.Task;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午3:16:39
 */
public interface TaskDao {
    public int insertTask(Task task);

    public void updateTask(Task task);

    public Date selectNow();

    public List<Task> selectWebisteTaskWithinPeriod(int userId, int webitseid, Date startedAt);
    
    public Task selectTaskByBankBillsKey(int userId, String bankBillsKey);

}
