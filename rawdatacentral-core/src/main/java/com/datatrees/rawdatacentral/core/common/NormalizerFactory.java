/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:53:47
 */
public class NormalizerFactory implements DataNormalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NormalizerFactory.class);

    List<DataNormalizer> normalizerList;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.rawdata.collector.worker.MessageNormalizer#normalize(com.datatrees.rawdata.
     * core.model.ExtractMessage)
     */
    @Override
    public boolean normalize(Object message) {
        for (DataNormalizer messageNormalizer : normalizerList) {
            try {
                if (messageNormalizer.normalize(message)) {
                    return true;
                }
            } catch (Exception e) {
                LOGGER.warn("Data " + message + " normalizer error " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * @return the normalizerList
     */
    public List<DataNormalizer> getNormalizerList() {
        return normalizerList;
    }

    /**
     * @param normalizerList the normalizerList to set
     */
    public void setNormalizerList(List<DataNormalizer> normalizerList) {
        this.normalizerList = normalizerList;
    }
    
}
