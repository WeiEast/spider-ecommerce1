/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.parser;

import java.util.*;
import java.util.regex.Matcher;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.Constant;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.domain.config.parser.IndexMapping;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.domain.config.parser.ParserPattern;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.datatrees.crawler.core.processor.extractor.util.TextUrlExtractor;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.google.common.base.Preconditions;
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
public class ParserImpl extends Operation {

    private static final Logger  log               = LoggerFactory.getLogger(ParserImpl.class);
    private              boolean needRequest       = false;
    private              boolean needReturnUrlList = false;
    private Parser parser;

    public ParserImpl(boolean needRequest, Parser parser) {
        super();
        this.needRequest = needRequest;
        this.parser = parser;
    }

    /**
     * @param needRequest2
     * @param parser2
     * @param needReturnUrlList
     */
    public ParserImpl(boolean needRequest, Parser parser, boolean needReturnUrlList) {
        this(needRequest, parser);
        setNeedReturnUrlList(needReturnUrlList);
    }

    protected void preProcess(Request request, Response response) throws Exception {}

    protected void postProcess(Request request, Response response) throws Exception {}

    /*
     * (non-Javadoc)
     */
    @Override
    public void process(Request request, Response response) throws Exception {
        Preconditions.checkNotNull(parser);
        String content = getInput(request, response);
        Preconditions.checkState(StringUtils.isNotEmpty(content), "input for parser should not be empty!");

        String template = parser.getUrlTemplate();
        Preconditions.checkState(StringUtils.isNotEmpty(template), "input for parser template  should not be empty!");
        log.info("parser template: " + template);

        // split by need requset and need ReturnUrlList
        Map<String, FieldExtractorWarpper> fieldResultMap = ResponseUtil.getResponseFieldResult(response);
        List<String> results = new ArrayList<String>(getParserResult(request, fieldResultMap, content));

        log.info("after template combine: " + results.size());
        String result = results.get(0);
        if (needRequest) {
            if (parser.getSleepSecond() != null && parser.getSleepSecond() > 0) {
                try {
                    log.info("do sleep " + parser.getSleepSecond() + "s before parser request.");
                    Thread.sleep(parser.getSleepSecond() * 1000);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            Request newRequest = new Request();
            RequestUtil.setProcessorContext(newRequest, RequestUtil.getProcessorContext(request));

            RequestUtil.setConf(newRequest, RequestUtil.getConf(request));
            RequestUtil.setContext(newRequest, RequestUtil.getContext(request));
            Response newResponse = new Response();
            result = getResponseByWebRequest(newRequest, newResponse, result);
            response.setOutPut(result);
        } else {
            if (needReturnUrlList) {
                // support muti parsers
                response.setOutPut(results);
            } else {
                response.setOutPut(result);
            }
        }

    }

    //
    // private void urlListReturn(Response response, List<String> newResults) {
    // List<String> results = ResponseUtil.getResponseList(response);
    // if (results == null) {
    // ResponseUtil.setResponseList(response, newResults);
    // } else {
    // results.addAll(newResults);
    // }
    // }

    /**
     * first replace from field context second replace by regex third replace by user defined
     * context;
     * @param fieldResultMap
     * @param parser2
     * @return
     */
    private Set<String> getParserResult(Request request, Map<String, FieldExtractorWarpper> fieldResultMap, String source) {

        Set<String> urlList = new HashSet<String>();

        List<ParserPattern> mappings = parser.getPatterns();
        String template = parser.getUrlTemplate();
        String refererTemplate = parser.getLinkUrlTemplate();
        String headers = parser.getHeaders();
        // set empty string
        refererTemplate = StringUtils.defaultIfEmpty(refererTemplate, "");
        headers = StringUtils.defaultIfEmpty(headers, "");

        String complexSource = ParserURLCombiner.encodeUrl(template, refererTemplate, headers);
        // result context
        Map<String, Object> fieldContext = FieldExtractorWarpperUtil.fieldWrapperMapToField(fieldResultMap);

        Map<String, Object> context = RequestUtil.getSourceMap(request);

        Set<String> needReplaced = ReplaceUtils.getReplaceList(complexSource);

        String charset = RequestUtil.getContentCharset(request);

        complexSource = ReplaceUtils.replaceURLMap(needReplaced, fieldContext, context, complexSource, charset);

        // group by index
        List<Map<String, Object>> fieldListResult = new ArrayList<Map<String, Object>>();
        boolean first = true;
        for (ParserPattern parserPattern : mappings) {
            String regex = parserPattern.getRegex();
            Matcher m = PatternUtils.matcher(regex, source);
            List<IndexMapping> indexMappings = parserPattern.getIndexMappings();
            int index = 0;
            while (m.find()) {
                Map<String, Object> indexFieldMap = new HashMap<String, Object>();
                for (IndexMapping indexMapping : indexMappings) {
                    try {
                        indexFieldMap.put(indexMapping.getPlaceholder(), m.group(indexMapping.getGroupIndex()));
                    } catch (Exception e) {
                        log.error("group index error!", e);
                    }
                }
                if (first) {
                    fieldListResult.add(indexFieldMap);
                } else {
                    Map<String, Object> map = fieldListResult.get(index);
                    if (map != null) {
                        map.putAll(indexFieldMap);
                    } else {
                        log.warn("parser pattern size not the same!");
                    }
                }
                index++;
            }
            first = false;
        }

        if (CollectionUtils.isNotEmpty(fieldListResult)) {
            int totalSize = fieldListResult.size();
            needReplaced = ReplaceUtils.getReplaceList(complexSource);
            log.info("total size...." + totalSize);
            for (int i = 0; i < totalSize; i++) {
                Map<String, Object> fieldMap = fieldListResult.get(i);
                String url = replaceFromContext(complexSource, needReplaced, fieldMap, context);
                urlList.add(url);
            }
        } else {
            // fix bug if not find field result using default input context
            needReplaced = ReplaceUtils.getReplaceList(complexSource);
            String url = replaceFromContext(complexSource, needReplaced, new HashMap<String, Object>(), context);
            urlList.add(url);
        }
        return urlList;
    }

    /**
     * @param fieldResultMap
     * @return
     */
    private String replaceFromContext(String source, Set<String> needReplaced, Map<String, Object> fieldResultMap, Map<String, Object> defaultMap) {

        if (StringUtils.isEmpty(source)) {
            return source;
        }
        String result = ReplaceUtils.replaceMap(needReplaced, fieldResultMap, defaultMap, source);
        return result;
    }

    /**
     * @param result
     * @param refererTemplate
     * @param headers
     * @return
     */
    private String getResponseByWebRequest(Request newResquest, Response newResponse, String complexUrl) {
        String datas[] = ParserURLCombiner.decodeParserUrl(complexUrl);
        String url = datas[0];
        String referer = datas[1];
        String headers = datas[2];

        List<String> urls = TextUrlExtractor.extractor(url, Constant.URL_REGEX, 1);
        if (urls.size() != 1) {
            log.info("url is not format in parser request! " + url);
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
            ServiceBase serviceProcessor = ProcessorFactory.getService(null);
            serviceProcessor.invoke(newResquest, newResponse);
        } catch (Exception e) {
            log.error("execute request error! " + e.getMessage(), e);
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

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public boolean isNeedReturnUrlList() {
        return needReturnUrlList;
    }

    public void setNeedReturnUrlList(boolean needReturnUrlList) {
        this.needReturnUrlList = needReturnUrlList;
    }

    @Deprecated
    static class ParserPatternResult {

        List<Map<String, String>> fieldListResultMap = new LinkedList<Map<String, String>>();

        public List<Map<String, String>> getFieldListResultMap() {
            return fieldListResultMap;
        }

        public void addFieldListResultMap(Map<String, String> patternResults) {
            this.fieldListResultMap.add(patternResults);
        }

        public void addAllFieldListResultMap(List<Map<String, String>> patternResults) {
            this.fieldListResultMap.addAll(patternResults);
        }

        public int size() {
            return fieldListResultMap.size();
        }

    }
}
