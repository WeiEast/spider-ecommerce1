/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.normalizers;

import javax.annotation.Resource;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:53:47
 */
@Component
public class MessageNormalizerFactory {

    private static final Logger             logger = LoggerFactory.getLogger(MessageNormalizerFactory.class);

    @Resource
    private              ApplicationContext context;

    public boolean normalize(Object message) {
        Collection<MessageNormalizer> normalizers = context.getBeansOfType(MessageNormalizer.class).values();
        for (MessageNormalizer messageNormalizer : normalizers) {
            try {
                if (messageNormalizer.normalize(message)) {
                    return true;
                }
            } catch (Exception e) {
                logger.error("Data {} normalizer error ", message, e);
            }
        }
        return false;
    }

}
