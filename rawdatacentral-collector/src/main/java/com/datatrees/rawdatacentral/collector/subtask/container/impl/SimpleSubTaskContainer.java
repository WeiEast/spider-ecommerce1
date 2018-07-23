/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.subtask.container.impl;

import com.datatrees.rawdatacentral.collector.subtask.container.Container;
import com.datatrees.rawdatacentral.core.model.subtask.SubTask;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 上午11:21:30
 */
public class SimpleSubTaskContainer implements Container {

    private SubTask subTask;

    /**
     *
     */
    public SimpleSubTaskContainer() {
        super();
    }

    /**
     * @param subTask the subTask to set
     */
    public void setSubTask(SubTask subTask) {
        this.subTask = subTask;
    }

    /*
     * (non-Javadoc)
     *
     * @see Container#getSubTask()
     */
    @Override
    public SubTask popSubTask() {
        return subTask;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * Container#addSubTask(com.datatrees.rawdatacentral
     * .core.model.subtask.SubTask)
     */
    @Override
    public void addSubTask(SubTask subTask) {
        this.subTask = subTask;
    }

}
