/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.search;

import javax.annotation.Nonnull;

import com.datatrees.common.protocol.ProtocolStatusCodes;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.bean.CrawlResponse;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ResponseCheckException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.page.PageImpl;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.treefinance.toolkit.util.Preconditions;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Mar 3, 2014 8:48:55 PM
 */
public class Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

    /**
     * crawler request main route step 1: request via httpclient step 2: parse page content
     * @param request
     * @return
     * @exception ResultEmptyException
     */
    public static CrawlResponse crawl(@Nonnull CrawlRequest request) throws ResultEmptyException {
        Preconditions.notNull("request", request);
        LOGGER.info("request handling ... {}", request);

        CrawlResponse response = CrawlResponse.build();
        try {

            SearchProcessorContext context = (SearchProcessorContext) request.getProcessorContext();
            String templateId = request.getSearchTemplateId();
            LinkNode url = request.getUrl();

            Page page = context.getPageDefinition(url, templateId);
            if (page != null) {
                RequestUtil.setCurrentPage(request, page);

                AbstractService service = page.getService();
                if (null == service) {
                    service = context.getDefaultService();
                }

                ServiceBase serviceProcessor = ProcessorFactory.getService(service);
                try {
                    // fetch page content
                    serviceProcessor.invoke(request, response);

                    // check the page response failed
                    String content = (String) response.getOutPut();
                    if (BooleanUtils.isTrue(page.getResponseCheck()) && (StringUtils.isBlank(content) || StringUtils.isNotBlank(page.getPageFailedPattern()) && RegExp.find(content, page.getPageFailedPattern()))) {
                        throw new ResponseCheckException("page:" + page.getId() + ",url:" + url.getUrl() + " response check failed contains " + page.getPageFailedPattern());
                    }
                } catch (ResponseCheckException e) {
                    throw e;
                } catch (Exception e) {
                    // response code failed
                    if (BooleanUtils.isTrue(page.getResponseCheck()) && RegExp.find(ResponseUtil.getResponseStatus(response).toString(), getFailurePattern(page))) {
                        throw new ResponseCheckException("page:" + page.getId() + ",url:" + request.getUrl() + " response check failed!", e);
                    } else {
                        throw e;
                    }
                }

                // reset content
                response.setOutPut(null);

                if (url.isNeedRequeue()) {
                    LOGGER.info("need requeue linkNode: {}", url);
                } else {
                    // parser
                    PageImpl pageProcessor = new PageImpl(page);
                    pageProcessor.invoke(request, response);
                }
            } else {
                ResponseUtil.setResponseStatus(response, Status.FILTERED);
                LOGGER.info("no available page found for linkNode: {}, template: {},set filtered.", url, templateId);
            }

        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error crawling url : {}", request.getUrl(), e);
            response.setErrorMsg(e.toString()).setStatus(Status.PROCESS_EXCEPTION);
            response.setAttribute(Constants.CRAWLER_EXCEPTION, e);
        }

        return response;
    }

    private static String getFailurePattern(Page page) {
        return StringUtils.defaultString(page.getFailedCodePattern(), "^(" + ProtocolStatusCodes.EXCEPTION + "|" + ProtocolStatusCodes.SERVER_EXCEPTION + ")$");
    }

}
