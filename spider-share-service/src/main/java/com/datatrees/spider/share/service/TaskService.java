/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service;

import java.util.Date;

import com.datatrees.spider.share.domain.model.Task;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午5:38:06
 */
public interface TaskService {

    public int insertTask(Task task);

    public void updateTask(Task task);

    public Date selectNow();

    /**
     * 根据taskId查询
     * @param taskId
     * @return
     */
    Task getByTaskId(Long taskId);

}
