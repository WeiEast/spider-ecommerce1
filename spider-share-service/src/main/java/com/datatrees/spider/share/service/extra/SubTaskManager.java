/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.extra;

import java.util.List;
import java.util.Map;

import com.datatrees.spider.share.service.domain.SubTask;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年12月21日 上午10:47:52
 */
public interface SubTaskManager {

    List<Map> getSyncedSubTaskResults(int taskid);

    void submitSubTask(SubTask task);

}
