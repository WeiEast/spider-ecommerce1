/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.utils;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月7日 下午3:54:59
 */
public class UniqueKeyGenUtil {

    private static final Logger logger = LoggerFactory.getLogger(UniqueKeyGenUtil.class);

    public static String uniqueKeyGen(String seed) {
        String uniqueMd5 = null;
        if (seed == null) {
            logger.info("Empty uniqueSign, use uuid to generate uniqueMd5.");
            uniqueMd5 = UUID.randomUUID().toString().replace("-", "");
        } else {
            uniqueMd5 = DigestUtils.md5Hex(seed);
        }
        return uniqueMd5;
    }
}
