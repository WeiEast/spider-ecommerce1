/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.*;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.extractor.FieldVisibleType;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.operation.AbstractOperation;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.*;
import com.datatrees.crawler.core.processor.common.exception.ExtractorException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.operation.Operation;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import com.treefinance.crawler.framework.format.Formatter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * field extractor should be parallel
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 1:45:17 PM
 */
public class FieldExtractorImpl extends Processor {

    private final FieldExtractor fieldExtractor;

    public FieldExtractorImpl(@Nonnull FieldExtractor fieldExtractor) {
        this.fieldExtractor = Objects.requireNonNull(fieldExtractor);
    }

    public FieldExtractor getFieldExtractor() {
        return fieldExtractor;
    }

    private Object extractWithPlugin(Request request, String content, AbstractPlugin pluginDesc) throws Exception {
        AbstractProcessorContext context = RequestUtil.getProcessorContext(request);

        Object fieldResult = PluginCaller.call(pluginDesc, context, () -> {
            Map<String, String> params = new LinkedHashMap<>();

            params.put(PluginConstants.PAGE_CONTENT, content);
            LinkNode requestLinkNode = RequestUtil.getCurrentUrl(request);
            if (requestLinkNode != null) {
                params.put(PluginConstants.CURRENT_URL, requestLinkNode.getUrl());
                params.put(PluginConstants.REDIRECT_URL, requestLinkNode.getRedirectUrl());
            }
            params.put(PluginConstants.FIELD, fieldExtractor.getField());

            return params;
        });

        // get pluginDesc json result
        Map<String, Object> pluginResultMap = PluginUtil.checkPluginResult((String) fieldResult);

        return pluginResultMap.get(PluginConstants.FIELD);
    }

    private Object extractWithOperation(Request request, String content, Map<String, FieldExtractorWarpper> resultMap) throws Exception {
        Object fieldResult = null;
        List<AbstractOperation> operations = fieldExtractor.getOperationList();
        List<Operation> operationsList = new ArrayList<>(operations.size());
        if (CollectionUtils.isNotEmpty(operations)) {
            Operation op;
            for (AbstractOperation operation : operations) {
                if (operation == null) continue;
                op = ProcessorFactory.getOperation(operation, fieldExtractor);
                operationsList.add(op);
            }
            ProcessorRunner runner = new ProcessorRunner(new ArrayList<>(operationsList));
            Response resp = new Response();
            ResponseUtil.setResponseFieldResult(resp, resultMap);
            String orignal = RequestUtil.getContent(request);
            try {
                request.setInput(content);
                runner.run(request, resp);
                fieldResult = resp.getOutPut();
            } catch (Exception e) {
                throw e;
            } finally {
                request.setInput(orignal);
            }

        } else {
            logger.warn("operation list is empty for field: {}", getFieldExtractor().getField());
            fieldResult = content;
        }
        return fieldResult;
    }

    private boolean isValid(Object obj) {
        // Check whether the data is valid
        if (obj instanceof FieldExtractorWarpper) {
            Object result = ((FieldExtractorWarpper) (obj)).getResult();
            return result != null && isResultValid(result);
        } else {
            return isResultValid(obj);
        }
    }

    private boolean isResultValid(Object obj) {
        // Check whether the data is valid
        if (obj instanceof String && StringUtils.isEmpty((String) obj)) {
            return false;
        }
        return true;
    }

    /**
     * process field extractor field extractor can have multi operation the order of operation is
     * serial field extractor it's self is parallel need PLUGIN_RESULT_MAP for plugin implement and
     * FIELDS_RESULT_MAP for field result map
     */
    @SuppressWarnings("unchecked")
    @Override
    public void process(Request request, Response response) throws Exception {
        Object fieldResult = null;
        Map<String, FieldExtractorWarpper> resultMap = initMap(response);
        String content = "";
        try {
            // precheck
            Preconditions.checkNotNull(fieldExtractor, "field extractor should not be null");
            if (BooleanUtils.isTrue(fieldExtractor.getStandBy()) && resultMap.get(fieldExtractor.getId()) != null && isValid(resultMap.get(fieldExtractor.getId()))) {
                logger.debug("no need use stand by fieldExtractor: {}", fieldExtractor);
                return;
            }

            content = RequestUtil.getContent(request);
            String sourceId = fieldExtractor.getSourceId();
            printExtractorInfo(fieldExtractor);
            if (StringUtils.isNotEmpty(sourceId)) {
                Object result = SourceUtil.getSourceMap(sourceId, request, response);
                if (result != null) {
                    content = result.toString();
                }
            }
            if (StringUtils.isEmpty(content)) {
                logger.warn("stop due to input content is empty,fieldExtractor: {}", fieldExtractor);
            } else {
                // encoding
                String encoding = fieldExtractor.getEncoding();
                if (StringUtils.isNotEmpty(encoding)) {
                    logger.debug("field encoding: {}", encoding);
                    content = encodeContent(content, encoding);
                }
                AbstractPlugin plugin = fieldExtractor.getPlugin();
                if (plugin != null) {
                    fieldResult = this.extractWithPlugin(request, content, plugin);
                } else {
                    fieldResult = this.extractWithOperation(request, content, resultMap);
                }
                // format
                fieldResult = this.format(request, response, fieldResult, fieldExtractor.getResultType());
            }
        } catch (Exception e) {
            logger.error("Error processing field extractor: {}", fieldExtractor, e);

            if (e instanceof ResultEmptyException) {
                throw new ResultEmptyException(e.getMessage());
            }
            fieldResult = null;
        }
        fieldResult = this.fieldDefaultValue(request, response, resultMap, fieldResult, fieldExtractor.getResultType());
        try {
            if (fieldResult != null && needResolveUrl() && fieldResult instanceof String) {
                fieldResult = resolveUrl((String) fieldResult, request);
            }
        } catch (Exception e) {
            logger.warn("error resolving url for field result: {} ", fieldResult, e);
            fieldResult = null;
        }
        if (BooleanUtils.isTrue(fieldExtractor.getNotEmpty()) && (fieldResult == null || StringUtils.isEmpty(fieldResult.toString()))) {
            throw new ResultEmptyException(fieldExtractor + " >> result should not be Empty!");
        }

        String id = fieldExtractor.getId();
        FieldExtractorWarpper warpper = new FieldExtractorWarpper(fieldExtractor, fieldResult);

        if (BooleanUtils.isTrue(fieldExtractor.getNotEmpty())) {
            logger.info("end not-empty field extractor result: {}", warpper);
        } else {
            logger.debug("end field extractor result: {}", warpper);
        }
        resultMap.put(id, warpper);

        // set field result visible
        FieldVisibleType fieldVisibleType = fieldExtractor.getFieldVisibleType();
        if (fieldVisibleType != null) {
            if (FieldVisibleType.REQUEST.equals(fieldVisibleType)) {
                RequestUtil.getRequestVisibleFields(request).put(id, fieldResult);
            } else if (FieldVisibleType.CONTEXT.equals(fieldVisibleType)) {
                RequestUtil.getContext(request).put(id, fieldResult);
                RequestUtil.getProcessorContext(request).getContext().put(id, fieldResult);
            } else if (FieldVisibleType.PROCESSOR_RESULT.equals(fieldVisibleType)) {
                RequestUtil.getContext(request).put(id, fieldResult);
                RequestUtil.getProcessorContext(request).getContext().put(id, fieldResult);
                RequestUtil.getProcessorContext(request).getProcessorResult().put(id, fieldResult);
            }
        }
    }

    private Object format(Request request, Response response, Object fieldResult, ResultType type) throws ExtractorException {
        if (type != null && fieldResult instanceof String) {
            Configuration conf = RequestUtil.getConf(request);
            String input = "";
            try {
                input = (String) fieldResult;
                if (StringUtils.isNotEmpty(input)) {
                    Formatter formatter = ProcessorFactory.getFormatter(type, conf);
                    fieldResult = formatter.format(input, fieldExtractor.getFormat(), request, response);
                } else if (!type.equals(ResultType.String)) {
                    fieldResult = null;
                }
            } catch (Exception e) {
                throw new ExtractorException("format " + input + " error", e);
            }
        }
        return fieldResult;
    }

    private Object fieldDefaultValue(Request request, Response response, Map<String, FieldExtractorWarpper> resultMap, Object fieldResult, ResultType type) {
        String defaultValue = fieldExtractor.getDefaultValue();
        if (defaultValue != null && (fieldResult == null || (fieldResult instanceof String && StringUtils.isEmpty((String) fieldResult)))) {
            Object result;
            if (ResultType.String.equals(type)) {
                result = StandardExpression.eval(defaultValue, ImmutableList.of(FieldExtractorWarpperUtil.fieldWrapperMapToField(resultMap), RequestUtil.getSourceMap(request)));
            } else {
                String val;
                if (type != null) {
                    val = StringUtils.trim(defaultValue);
                } else {
                    val = defaultValue;
                }
                result = StandardExpression.evalWithObject(val, ImmutableList.of(FieldExtractorWarpperUtil.fieldWrapperMapToField(resultMap), RequestUtil.getSourceMap(request)));
            }
            try {
                return this.format(request, response, result, type);
            } catch (Exception e) {
                logger.warn("Error formatting default value for field: {}", fieldExtractor, e);
                return null;
            }
        }
        return fieldResult;
    }

    /**
     * @param url
     * @param request
     * @return
     */
    private String resolveUrl(String url, Request request) {
        String res = url;
        LinkNode current = RequestUtil.getCurrentUrl(request);
        if (current != null) {
            logger.debug("resolve url: {}", current.getUrl());
            String baseURL = (StringUtils.isNotEmpty(current.getBaseUrl()) ? current.getBaseUrl() : current.getUrl());
            res = UrlUtils.resolveUrl(baseURL, url);
        }
        return res;
    }

    /**
     * @return
     */
    private boolean needResolveUrl() {
        return fieldExtractor.getField().contains("url");
    }

    /**
     * @param content
     * @param encoding
     * @return
     */
    private String encodeContent(String content, String encoding) {

        Charset charset = CharsetUtil.getCharset(encoding, null);
        if (charset != null) {
            return new String(content.getBytes(), charset);
        }
        return content;
    }

    protected void postProcess(Request request, Response response) throws ResultEmptyException {
        try {
            if (getNext() != null) {
                getNext().invoke(request, response);
            }
        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            logger.error("invoke next error!", e);
        }

    }

    /**
     * @param response
     * @return
     */
    private Map<String, FieldExtractorWarpper> initMap(Response response) {

        @SuppressWarnings("unchecked") Map<String, FieldExtractorWarpper> resultMap = ResponseUtil.getResponseFieldResult(response);
        if (resultMap == null) {
            resultMap = new HashMap<>();
            ResponseUtil.setResponseFieldResult(response, resultMap);
        }
        return resultMap;
    }

    private void printExtractorInfo(FieldExtractor ex) {
        logger.debug("start field extractor >> field: {}, id: {}", ex.getField(), ex.getId());
    }

}
