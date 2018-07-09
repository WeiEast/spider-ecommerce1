/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.datatrees.common.pipeline.ProcessorInvokerAdapter;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.BeanResourceFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.treefinance.crawler.framework.util.UrlExtractor;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 7, 2014 7:43:14 PM
 */
public abstract class ServiceBase<S extends AbstractService> extends ProcessorInvokerAdapter {

    protected final S service;

    public ServiceBase() {
        this.service = null;
    }

    public ServiceBase(@Nonnull S service) {
        this.service = Objects.requireNonNull(service);
    }

    public S getService() {
        return service;
    }

    // resolve base url
    @Override
    protected void postProcess(@Nonnull Request request, @Nonnull Response response) throws Exception {
        LinkNode current = RequestUtil.getCurrentUrl(request);
        if (current != null) {
            String content = RequestUtil.getContent(request);
            if (StringUtils.isNotEmpty(content)) {
                String baseContent = RegExp.group(content, "<base(.*)>", Pattern.CASE_INSENSITIVE, 1);
                String baseDomainUrl = null;
                if (StringUtils.isNotEmpty(baseContent)) {
                    List<String> urlsInText = UrlExtractor.extract(baseContent);
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
                logger.debug("originUrl: {}, baseDomainUrl: {}", current.getUrl(), baseDomainUrl);
            }
        }
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

}
