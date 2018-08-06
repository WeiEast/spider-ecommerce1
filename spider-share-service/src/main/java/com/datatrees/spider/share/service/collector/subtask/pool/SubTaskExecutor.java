/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.collector.subtask.pool;

import java.util.Map;
import java.util.concurrent.Future;

import com.datatrees.spider.share.service.collector.subtask.container.Container;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 上午11:04:37
 */
public interface SubTaskExecutor {

    public Future<Map> submit(Container container);

    public void shutdown();

    public int getActiveCount();

}
