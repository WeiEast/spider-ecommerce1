/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.ecommerce.service.normalizer;

import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.SubmitMessage;
import com.datatrees.spider.share.service.normalizers.SubmitNormalizer;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月4日 下午5:33:54
 */
@Service
public class EcommerceSubmitDataNormalizer implements SubmitNormalizer {

    @Override
    public boolean normalize(SubmitMessage message) {
        return message.getExtractMessage().getResultType().equals(ResultType.ECOMMERCE);
    }

}
