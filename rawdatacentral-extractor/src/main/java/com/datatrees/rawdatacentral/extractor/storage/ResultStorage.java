/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.extractor.storage;

import com.datatrees.rawdatacentral.domain.result.AbstractExtractResult;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午8:23:19
 */
public interface ResultStorage {
    void doExtractResultSave(AbstractExtractResult result);
}
