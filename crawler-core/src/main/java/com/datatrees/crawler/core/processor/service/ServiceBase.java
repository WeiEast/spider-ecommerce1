/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service;

import java.util.List;
import java.util.regex.Pattern;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.Constant;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.BeanResourceFactory;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.extractor.util.TextUrlExtractor;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 7:43:14 PM
 */
public abstract class ServiceBase extends Processor {

    private static final Logger log = LoggerFactory.getLogger(ServiceBase.class);
    protected AbstractService service;

    public AbstractService getService() {
        return service;
    }

    public void setService(AbstractService service) {
        this.service = service;
    }

    @Override
    protected void preProcess(Request request, Response response) throws Exception {
        super.preProcess(request, response);
    }

    // resolve base url
    @Override
    protected void postProcess(Request request, Response response) throws Exception {
        LinkNode current = RequestUtil.getCurrentUrl(request);
        if (current != null) {
            String content = RequestUtil.getContent(request);
            if (StringUtils.isNotEmpty(content)) {
                Pattern pattern = Pattern.compile("<base(.*)>", Pattern.CASE_INSENSITIVE);
                String baseContent = PatternUtils.group(content, pattern, 1);
                getBaseUrl(baseContent, current);
            }
        }
    }

    /**
     *
     * @param baseContent
     * @param current
     * @return
     */
    public String getBaseUrl(String baseContent, LinkNode current) {
        String baseDomainUrl = null;
        if (StringUtils.isNotEmpty(baseContent)) {
            List<String> urlsInText = TextUrlExtractor.extractor(baseContent, Constant.URL_REGEX, 1);
            if (CollectionUtils.isNotEmpty(urlsInText)) {
                baseDomainUrl = urlsInText.get(0);
            }
        }
        if (StringUtils.isEmpty(baseDomainUrl)) {
            if (StringUtils.isNotEmpty(current.getRedirectUrl())) {
                baseDomainUrl = current.getRedirectUrl();
            } else {
                baseDomainUrl = current.getUrl();
            }
        }
        current.setBaseUrl(baseDomainUrl);
        log.debug("originUrl: " + current.getUrl() + ", baseDomainUrl: " + baseDomainUrl);
        return baseDomainUrl;
    }

    /**
     * 获取redis服务
     * @return
     */
    protected RedisService getRedisService() {
        return BeanResourceFactory.getInstance().getBean(RedisService.class);
    }

    /**
     * 获取消息服务
     * @return
     */
    protected MessageService getMessageService() {
        return BeanResourceFactory.getInstance().getBean(MessageService.class);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
    }

    @Override
    public String getInfo() {
        return "service: " + getClass().getCanonicalName();
    }

}
