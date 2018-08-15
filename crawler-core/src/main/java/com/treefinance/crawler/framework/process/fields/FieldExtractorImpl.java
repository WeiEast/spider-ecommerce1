/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.framework.process.fields;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.domain.config.extractor.FieldVisibleType;
import com.datatrees.crawler.core.domain.config.extractor.ResultType;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.common.exception.ExtractorException;
import com.datatrees.crawler.core.processor.common.exception.OperationException;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.treefinance.crawler.framework.process.operation.OperationPipeline;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginUtil;
import com.google.common.collect.ImmutableList;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.pipeline.FailureSkipProcessorValve;
import com.treefinance.crawler.framework.expression.StandardExpression;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import com.treefinance.crawler.framework.format.Formatter;
import com.treefinance.crawler.framework.util.SourceUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

/**
 * field extractor should be parallel
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 18, 2014 1:45:17 PM
 */
public class FieldExtractorImpl extends FailureSkipProcessorValve {

    private final FieldExtractor fieldExtractor;

    public FieldExtractorImpl(@Nonnull FieldExtractor fieldExtractor) {
        this.fieldExtractor = Objects.requireNonNull(fieldExtractor);
    }

    @Override
    protected boolean isSkipped(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) {
        boolean skipped = super.isSkipped(request, response);

        if (!skipped) {
            FieldExtractResultSet fieldExtractResultSet = initMap(response);
            if (Boolean.TRUE.equals(fieldExtractor.getStandBy()) && fieldExtractResultSet.isNotEmptyResult(fieldExtractor.getId())) {
                logger.debug("Skip field extractor with matching the stand-by flag. Field-Extractor: {}", fieldExtractor);
                return true;
            }
        }

        return skipped;
    }

    /**
     * process field extractor field extractor can have multi operation the order of operation is
     * serial field extractor it's self is parallel need PLUGIN_RESULT_MAP for plugin implement and
     * FIELDS_RESULT_MAP for field result map
     */
    @SuppressWarnings("unchecked")
    @Override
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        logger.debug("start field extractor >>  {}, ", fieldExtractor);

        Object fieldResult = null;
        FieldExtractResultSet fieldExtractResultSet = initMap(response);
        String content;
        try {
            content = RequestUtil.getContent(request);
            String sourceId = fieldExtractor.getSourceId();

            if (StringUtils.isNotEmpty(sourceId)) {
                Object result = SourceUtils.getSourceFieldValue(sourceId, request, response);
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
                    fieldResult = this.extractWithPlugin(content, request, plugin);
                } else {
                    fieldResult = this.extractWithOperation(content, request, fieldExtractResultSet);
                }
                // format
                fieldResult = this.format(request, response, fieldResult, fieldExtractor.getResultType());
            }
        } catch (ResultEmptyException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Error processing field extractor: {}", fieldExtractor, e);

            fieldResult = null;
        }
        fieldResult = this.fieldDefaultValue(request, response, fieldExtractResultSet, fieldResult, fieldExtractor.getResultType());
        try {
            if (needResolveUrl() && fieldResult instanceof String) {
                fieldResult = resolveUrl((String) fieldResult, request);
            }
        } catch (Exception e) {
            logger.warn("error resolving url for field result: {} ", fieldResult, e);
            fieldResult = null;
        }

        boolean notEmpty = BooleanUtils.isTrue(fieldExtractor.getNotEmpty());
        if (notEmpty && (fieldResult == null || StringUtils.isEmpty(fieldResult.toString()))) {
            throw new ResultEmptyException(fieldExtractor + " >> result should not be Empty!");
        }

        String id = fieldExtractor.getId();
        FieldExtractResult fieldExtractResult = new FieldExtractResult(fieldExtractor, fieldResult);
        fieldExtractResultSet.put(id, fieldExtractResult);

        // set field result visible
        FieldVisibleType fieldVisibleType = fieldExtractor.getFieldVisibleType();
        if (fieldVisibleType != null) {
            switch (fieldVisibleType) {
                case PROCESSOR_RESULT:
                    request.addResultScope(id, fieldResult);
                    break;
                case CONTEXT:
                    request.addContextScope(id, fieldResult);
                    break;
                default:
                    request.addVisibleScope(id, fieldResult);
                    break;
            }
        }

        if (notEmpty) {
            logger.info("end not-empty field extractor result: {}", fieldExtractResult);
        } else {
            logger.debug("end field extractor result: {}", fieldExtractResult);
        }
    }

    private Object extractWithOperation(String content, SpiderRequest request, FieldExtractResultSet fieldExtractResultSet) throws ResultEmptyException, OperationException {
        OperationPipeline pipeline = new OperationPipeline(fieldExtractor);
        return pipeline.start(content, request, fieldExtractResultSet);
    }

    private Object extractWithPlugin(String content, SpiderRequest request, AbstractPlugin pluginDesc) {
        AbstractProcessorContext context = request.getProcessorContext();

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

    private Object format(SpiderRequest request, SpiderResponse response, Object fieldResult, ResultType type) throws ExtractorException {
        if (type != null && fieldResult instanceof String) {
            Configuration conf = request.getConfiguration();
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

    private Object fieldDefaultValue(SpiderRequest request, SpiderResponse response, FieldExtractResultSet fieldExtractResultSet, Object fieldResult, ResultType type) {
        String defaultValue = fieldExtractor.getDefaultValue();
        if (defaultValue != null && (fieldResult == null || (fieldResult instanceof String && StringUtils.isEmpty((String) fieldResult)))) {
            Object result;
            if (ResultType.String.equals(type)) {
                result = StandardExpression.eval(defaultValue, ImmutableList.of(fieldExtractResultSet.resultMap(), request.getGlobalScopeAsMap()));
            } else {
                String val;
                if (type != null) {
                    val = StringUtils.trim(defaultValue);
                } else {
                    val = defaultValue;
                }
                result = StandardExpression.evalWithObject(val, ImmutableList.of(fieldExtractResultSet.resultMap(), request.getGlobalScopeAsMap()));
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
    private String resolveUrl(String url, SpiderRequest request) {
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

    private FieldExtractResultSet initMap(SpiderResponse response) {
        FieldExtractResultSet fieldExtractResultSet = ResponseUtil.getFieldExtractResultSet(response);
        if (fieldExtractResultSet == null) {
            fieldExtractResultSet = new FieldExtractResultSet();
            ResponseUtil.setFieldExtractResultSet(response, fieldExtractResultSet);
        }
        return fieldExtractResultSet;
    }

}
