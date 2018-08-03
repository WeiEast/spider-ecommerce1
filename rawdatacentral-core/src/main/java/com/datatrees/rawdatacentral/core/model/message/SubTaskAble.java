/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.core.model.message;

import com.datatrees.spider.share.service.domain.SubSeed;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2016年1月18日 下午5:39:23
 */
public interface SubTaskAble extends TaskRelated, TemplteAble {

    public SubSeed getSubSeed();

    public boolean isSynced();

    public boolean noStatus();
}
