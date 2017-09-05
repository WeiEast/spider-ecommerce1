/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import com.datatrees.rawdatacentral.dao.TaskDAO;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.domain.model.example.TaskExample;
import com.datatrees.rawdatacentral.service.TaskService;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 上午12:09:08
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDAO taskDAO;

    @Override
    public int insertTask(Task task) {
        return taskDAO.insertSelective(task);
    }

    @Override
    public void updateTask(Task task) {
        taskDAO.updateByPrimaryKeySelective(task);
    }

    @Override
    public Date selectNow() {
        return taskDAO.selectNow();
    }

    @Override
    public Task getByTaskId(Long taskId) {
        TaskExample example = new TaskExample();
        example.createCriteria().andTaskidEqualTo(taskId);
        List<Task> list = taskDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

}
