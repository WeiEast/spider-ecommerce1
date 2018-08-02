/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.constants;

import com.datatrees.common.conf.PropertiesConfiguration;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 下午5:26:35
 */
public interface Constants {

    String GSON_TYPE                  = "GSON_TYPE";
    int    REDIS_KEY_TIMEOUT          = PropertiesConfiguration.getInstance().getInt("rawdatacentral.redisKey.timeout", 600);
    String DEFAULT_ENCODE_CHARSETNAME = "UTF-8";

}
