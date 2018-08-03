/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.collector.worker.deduplicate.impl;

import java.util.Set;

import com.datatrees.rawdatacentral.collector.worker.deduplicate.DuplicateChecker;
import com.datatrees.spider.share.service.util.UniqueKeyGenUtil;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 下午3:15:11
 */
public class DuplicateCheckerImpl implements DuplicateChecker {

    Set<String> existedKeySet;

    /**
     * @param existedKeySet
     */
    public DuplicateCheckerImpl(Set<String> existedKeySet) {
        super();
        this.existedKeySet = existedKeySet;
    }

    @Override
    public boolean isDuplicate(String websiteType, String seed) {
        String uniqueKey = UniqueKeyGenUtil.uniqueKeyGen(seed);
        return existedKeySet != null && existedKeySet.contains(uniqueKey);
    }

}
