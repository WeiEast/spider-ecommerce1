/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.format.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import com.datatrees.crawler.core.processor.service.ServiceBase;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午1:11:32
 */
public class ResourceStringFormatImpl extends AbstractFormat {
    private static final Logger logger = LoggerFactory.getLogger(ResourceStringFormatImpl.class);



    /*
     * (non-Javadoc)
     */
    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        String output = null;
        try {
            if (UrlUtils.isUrl(orginal)) {
                LinkNode linkNode = new LinkNode(orginal);
                Request newRequest = new Request();
                RequestUtil.setProcessorContext(newRequest, RequestUtil.getProcessorContext(req));
                RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
                Response newResponse = new Response();
                try {
                    RequestUtil.setCurrentUrl(newRequest, linkNode);
                    ServiceBase serviceProcessor = ProcessorFactory.getService(null);
                    serviceProcessor.invoke(newRequest, newResponse);
                } catch (Exception e) {
                    logger.error("execute request error! " + e.getMessage(), e);
                }
                output = StringUtils.defaultString(RequestUtil.getContent(newRequest));
            } else {// html file
                output = orginal;
            }
            return output;
        } catch (Exception e) {
            logger.error("stream format error", e);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.format.AbstractFormat#isResultType(java.lang.Object)
     */
    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof String) {
            return true;
        } else {
            return false;
        }
    }
}
