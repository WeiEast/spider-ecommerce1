/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.submitter.normalizer;

import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.core.common.DataNormalizer;
import com.datatrees.rawdatacentral.core.model.ResultType;
import com.datatrees.rawdatacentral.core.model.SubmitMessage;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月4日 下午5:33:54
 */
@Service
public class EcommerceSubmitDataNormalizer implements DataNormalizer {

    /*
     * (non-Javadoc)
     * 
     * @see DataNormalizer#normalize(java.lang.Object)
     */
    @Override
    public boolean normalize(Object data) {
        SubmitMessage message = ((SubmitMessage) data);
        if (message.getExtractMessage().getResultType().equals(ResultType.ECOMMERCE)) {
            return true;
        }
        return false;
    }

}
