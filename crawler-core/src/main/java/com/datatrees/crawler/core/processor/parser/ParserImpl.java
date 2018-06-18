/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.parser;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.parser.IndexMapping;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.domain.config.parser.ParserPattern;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.google.common.collect.ImmutableList;
import com.treefinance.crawler.framework.expression.ExpressionParser;
import com.treefinance.crawler.framework.util.UrlExtractor;
import com.treefinance.toolkit.util.Preconditions;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * parser segment content and send request or extract urls
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 20, 2014 8:57:12 PM
 */
public class ParserImpl {

    private static final Logger  logger            = LoggerFactory.getLogger(ParserImpl.class);
    private final        Parser  parser;
    private              boolean needRequest       = false;
    private              boolean needReturnUrlList = false;

    public ParserImpl(Parser parser, boolean needRequest) {
        this(parser, needRequest, false);
    }

    public ParserImpl(Parser parser, boolean needRequest, boolean needReturnUrlList) {
        this.parser = parser;
        this.needRequest = needRequest;
        this.needReturnUrlList = needReturnUrlList;
    }

    public Object parse(@Nonnull String content, @Nonnull Request request, @Nonnull Response response) throws InterruptedException {
        Preconditions.notEmpty("content", content);

        String template = parser.getUrlTemplate();
        Preconditions.notEmpty("url-template", template);

        logger.info("parser's url-template: {}", template);

        String refererTemplate = StringUtils.defaultString(parser.getLinkUrlTemplate());
        logger.info("parser's referer-template: {}", refererTemplate);
        String headers = StringUtils.defaultString(parser.getHeaders());
        logger.info("parser's header: {}", headers);
        String complexSource = ParserURLCombiner.encodeUrl(template, refererTemplate, headers);

        List<String> results = evalExp(complexSource, request, response, content);

        logger.info("after template combine: {}", results.size());
        String result = results.get(0);
        if (isNeedRequest()) {
            if (parser.getSleepSecond() != null && parser.getSleepSecond() > 0) {
                logger.info("sleep {}s before parser request.", parser.getSleepSecond());
                TimeUnit.SECONDS.sleep(parser.getSleepSecond());
            }
            Request newRequest = new Request();
            RequestUtil.setProcessorContext(newRequest, RequestUtil.getProcessorContext(request));
            RequestUtil.setConf(newRequest, RequestUtil.getConf(request));
            RequestUtil.setContext(newRequest, RequestUtil.getContext(request));
            Response newResponse = new Response();
            result = getResponseByWebRequest(newRequest, newResponse, result);
            return result;
        } else if (isNeedReturnUrlList()) {
            // support multi parsers
            return results;
        } else {
            return result;
        }
    }

    /**
     * First: replaced from visible field scope;
     * Second: replaced by the matched string in input content by regexp
     * Third: replaced by global visible field context;
     */
    private List<String> evalExp(String url, Request request, Response response, String content) {
        ExpressionParser parser = ExpressionParser.parse(url);
        String expUrl = parser.evalUrl(request, response, false, true);
        parser.reset(expUrl);

        if (parser.findExp()) {
            List<Map<String, Object>> fieldScopes = findByRegex(content);

            Map<String, Object> context = RequestUtil.getSourceMap(request);
            if (CollectionUtils.isNotEmpty(fieldScopes)) {
                return fieldScopes.stream().map(fieldScope -> parser.evalExp(ImmutableList.of(fieldScope, context), true, false)).distinct().collect(Collectors.toList());
            }

            // if not find field result using default input context
            return Collections.singletonList(parser.evalExp(context, true, false));
        }

        return Collections.singletonList(expUrl);
    }

    private List<Map<String, Object>> findByRegex(String content) {
        List<ParserPattern> mappings = parser.getPatterns();
        if (CollectionUtils.isEmpty(mappings)) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> fieldScopes = new ArrayList<>();
        for (ParserPattern parserPattern : mappings) {
            String regex = parserPattern.getRegex();
            if (regex == null) continue;

            List<IndexMapping> indexMappings = parserPattern.getIndexMappings();
            if (CollectionUtils.isEmpty(indexMappings)) continue;

            Matcher m = RegExp.getMatcher(regex, content);
            int i = 0;
            while (m.find()) {
                Map<String, Object> indexFieldMap;
                if (fieldScopes.size() > i) {
                    indexFieldMap = fieldScopes.get(i);
                } else {
                    indexFieldMap = new HashMap<>();
                    fieldScopes.add(indexFieldMap);
                }

                for (IndexMapping indexMapping : indexMappings) {
                    if (StringUtils.isEmpty(indexMapping.getPlaceholder())) continue;

                    try {
                        indexFieldMap.put(indexMapping.getPlaceholder(), m.group(indexMapping.getGroupIndex()));
                    } catch (Exception e) {
                        logger.error("Error mapping with the parser regex. - regex: {}", regex, e);
                    }
                }
                i++;
            }
        }

        logger.debug("Field scope size: {}", fieldScopes.size());

        return fieldScopes;
    }

    private String getResponseByWebRequest(Request newResquest, Response newResponse, String complexUrl) {
        String datas[] = ParserURLCombiner.decodeParserUrl(complexUrl);
        String url = datas[0];
        String referer = datas[1];
        String headers = datas[2];

        List<String> urls = UrlExtractor.extract(url);
        if (urls.size() != 1) {
            logger.info("url was not formatted in parser request! >> {}", url);
            return url;
        }

        try {
            LinkNode currentLinkNode = new LinkNode(url);
            // add referer
            if (StringUtils.isNotEmpty(referer)) {
                currentLinkNode.getHeaders().put(Constants.HTTP_HEADER_REFERER, referer);
            }
            // // add json headers
            if (StringUtils.isNotEmpty(headers)) {
                Map<String, String> headersMap = (Map<String, String>) GsonUtils.fromJson(headers, Map.class);
                currentLinkNode.addHeaders(headersMap);
            }
            RequestUtil.setCurrentUrl(newResquest, currentLinkNode);
            AbstractProcessorContext context = RequestUtil.getProcessorContext(newResquest);
            AbstractService service = context.getDefaultService();
            ServiceBase serviceProcessor = ProcessorFactory.getService(service);
            serviceProcessor.invoke(newResquest, newResponse);
        } catch (Exception e) {
            logger.warn("error requesting url: {}", url, e);
            return StringUtils.EMPTY;
        }
        return StringUtils.defaultString(RequestUtil.getContent(newResquest));
    }

    public boolean isNeedRequest() {
        return needRequest;
    }

    public void setNeedRequest(boolean needRequest) {
        this.needRequest = needRequest;
    }

    public Parser getParser() {
        return parser;
    }

    public boolean isNeedReturnUrlList() {
        return needReturnUrlList;
    }

    public void setNeedReturnUrlList(boolean needReturnUrlList) {
        this.needReturnUrlList = needReturnUrlList;
    }

}
