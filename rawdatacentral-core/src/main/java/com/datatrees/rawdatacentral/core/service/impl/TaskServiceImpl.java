/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.service.TaskService;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.core.dao.TaskDao;
import com.datatrees.rawdatacentral.domain.common.Task;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 上午12:09:08
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Resource
    private TaskDao taskDao;

    /*
     * (non-Javadoc)
     * 
     * @see
     * TaskService#insertTask(com.datatrees.rawdatacentral.core.model
     * .Task)
     */
    @Override
    public int insertTask(Task task) {
        return taskDao.insertTask(task);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * TaskService#updateTask(com.datatrees.rawdatacentral.core.model
     * .Task)
     */
    @Override
    public void updateTask(Task task) {
        taskDao.updateTask(task);
    }

    /*
     * (non-Javadoc)
     * 
     * @see TaskService#selectNow()
     */
    @Override
    public Date selectNow() {
        return taskDao.selectNow();
    }



}
