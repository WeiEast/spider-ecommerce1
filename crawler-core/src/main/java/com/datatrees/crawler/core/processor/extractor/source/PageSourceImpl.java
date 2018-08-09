/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.source;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datatrees.common.pipeline.ProcessorInvokerAdapter;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.PageSource;
import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.page.PageHelper;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import com.treefinance.crawler.framework.util.SourceFieldUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午10:23:47
 */
public class PageSourceImpl extends ProcessorInvokerAdapter {

    private final List<PageSource> pageSources;

    public PageSourceImpl(@Nonnull List<PageSource> pageSources) {
        this.pageSources = Objects.requireNonNull(pageSources);
    }

    @Override
    public void process(@Nonnull Request request, @Nonnull Response response) throws Exception {
        Object input = request.getInput();
        Preconditions.checkNotNull(input, "input should not be null!");

        StringBuilder builder = new StringBuilder();
        for (PageSource source : pageSources) {
            logger.debug("Search source : {}", source);

            String result = getPageContent(source, input, request);

            List<Replacement> replacements = source.getReplacements();
            if (CollectionUtils.isNotEmpty(replacements)) {
                result = PageHelper.replaceText(result, replacements);
            }

            Regexp regexp = source.getRegexp();
            if (regexp != null) {
                result = PageHelper.getTextByRegexp(result, regexp);
            }

            logger.debug("Actual source content: {}", result);

            // set source field to context
            RequestUtil.getProcessorContext(request).addAttribute(source.getField(), result);

            builder.append(result);
        }

        String content = builder.toString();
        logger.debug("Actual extracting content: {}", content);

        RequestUtil.setContent(request, content);
    }

    private String getPageContent(PageSource source, Object input, Request request) throws Exception {
        String separator = StringUtils.defaultString(source.getSeparator());
        AbstractPlugin plugin = source.getPlugin();

        logger.debug("Get page content. <<< source-ref: {}, separator: {}, plugin-ref: {}", source.getField(), separator,
                plugin != null ? plugin.getId() : null);

        if (plugin != null) {
            Object value = SourceFieldUtils.getFieldValue(input, source.getField());
            if (value instanceof Collection) {
                Stream<String> stream = ((Collection) value).stream().map(obj -> this.getSourceContent(obj, plugin, request));

                return stream.collect(Collectors.joining(separator));
            }

            return this.getSourceContent(value, plugin, request);
        }

        String value = RequestUtil.getAttribute(request, source.getField());
        if (StringUtils.isNotBlank(separator) || value == null) {
            value = SourceFieldUtils.getFieldValueAsString(input, source.getField(), separator);
        }

        return value;
    }

    private String getSourceContent(Object value, AbstractPlugin pluginDesc, Request request) {
        String content;
        if (value instanceof FileWapper) {
            AbstractProcessorContext context = RequestUtil.getProcessorContext(request);
            content = (String) PluginCaller.call(pluginDesc, context, () -> {
                Map<String, String> params = new HashMap<>();
                FileWapper file = (FileWapper) value;
                file.getFileInputStream();//download attachment to local
                params.put(PluginConstants.FILE_WAPPER_PATH, file.getAbsolutePath());
                params.put(PluginConstants.FILE_MIME_TYPE, file.getMimeType());
                params.put(PluginConstants.FILE_NAME, file.getName());
                params.put(PluginConstants.FILE_SOURCE_URL, file.getSourceURL());

                return params;
            });
        } else if (value instanceof String) {
            AbstractProcessorContext context = RequestUtil.getProcessorContext(request);
            content = (String) PluginCaller.call(pluginDesc, context, () -> {
                Map<String, String> params = new HashMap<>();
                params.put(PluginConstants.FILE_CONTENT, (String) value);

                return params;
            });
        } else {
            content = StringUtils.EMPTY;
            logger.warn("incorrect input content to call plugin to get source content. <<< {}", value);
        }

        logger.debug("Source content : {}", content);

        return content;
    }
}
