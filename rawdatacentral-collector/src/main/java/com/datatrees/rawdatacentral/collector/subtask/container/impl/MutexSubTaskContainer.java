/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.subtask.container.impl;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.datatrees.rawdatacentral.collector.subtask.container.Container;
import com.datatrees.rawdatacentral.collector.subtask.container.Mutex;
import com.datatrees.spider.share.service.domain.SubTask;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 上午11:12:57
 */
public class MutexSubTaskContainer implements Mutex, Container {

    private Queue<SubTask> exclusiveList;

    private boolean        waiting = true;// thread wait for new subtask add

    /**
     *
     */
    public MutexSubTaskContainer() {
        this(new LinkedBlockingQueue<SubTask>(), true);
    }

    /**
     * @param exclusiveList
     * @param waiting
     */
    public MutexSubTaskContainer(Queue<SubTask> exclusiveList, boolean waiting) {
        super();
        this.exclusiveList = exclusiveList;
        this.waiting = waiting;
    }

    /*
     * (non-Javadoc)
     *
     * @see Container#isMutex()
     */
    @Override
    public boolean isMutex() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see Mutex#stopWaiting()
     */
    @Override
    public void stopWaiting() {
        waiting = false;
    }

    /**
     * @return the exclusiveList
     */
    public Queue<SubTask> getExclusiveList() {
        return exclusiveList;
    }

    /**
     * @param exclusiveList the exclusiveList to set
     */
    public void setExclusiveList(Queue<SubTask> exclusiveList) {
        this.exclusiveList = exclusiveList;
    }

    /**
     * @return the waiting
     */
    public boolean isWaiting() {
        return waiting;
    }

    /**
     * @param waiting the waiting to set
     */
    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    /*
     * (non-Javadoc)
     *
     * @see Container#getSubTask()
     */
    @Override
    public SubTask popSubTask() {
        return exclusiveList.poll();
    }

    /*
     * (non-Javadoc)
     *
     * @see Mutex#waiting()
     */
    @Override
    public boolean waiting() {
        return waiting;
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
        exclusiveList.offer(subTask);
    }

}
