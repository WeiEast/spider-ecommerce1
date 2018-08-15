/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.source;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datatrees.crawler.core.domain.config.extractor.PageSource;
import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.page.PageHelper;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.context.pipeline.ProcessorInvokerAdapter;
import com.treefinance.crawler.framework.download.WrappedFile;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import com.treefinance.crawler.framework.util.FieldUtils;
import com.treefinance.toolkit.util.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午10:23:47
 */
public class PageSourceImpl extends ProcessorInvokerAdapter {

    private final List<PageSource> pageSources;

    public PageSourceImpl(@Nonnull List<PageSource> pageSources) {
        if (CollectionUtils.isEmpty(pageSources)) {
            throw new IllegalArgumentException("page-source must not be empty!");
        }
        this.pageSources = pageSources;
    }

    @Override
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        Object input = request.getInput();
        Preconditions.notNull("input", input);

        StringBuilder builder = new StringBuilder();
        for (PageSource source : pageSources) {
            logger.debug("Search page source : {}", source);

            String result = getPageContent(source, input, request);

            List<Replacement> replacements = source.getReplacements();
            if (CollectionUtils.isNotEmpty(replacements)) {
                result = PageHelper.replaceText(result, replacements);
            }

            Regexp regexp = source.getRegexp();
            if (regexp != null) {
                result = PageHelper.getTextByRegexp(result, regexp);
            }

            logger.debug("Actual page source content: {}", result);

            // set source field to context
            request.getProcessorContext().addAttribute(source.getField(), result);

            builder.append(result);
        }

        String content = builder.toString();
        logger.debug("Actual extracting content: {}", content);

        RequestUtil.setContent(request, content);
    }

    @SuppressWarnings("unchecked")
    private String getPageContent(PageSource source, Object input, SpiderRequest request) {
        String separator = StringUtils.defaultString(source.getSeparator());
        AbstractPlugin plugin = source.getPlugin();

        logger.debug("Get page content. <<< source-ref: {}, separator: {}, plugin-ref: {}", source.getField(), separator,
                plugin != null ? plugin.getId() : null);

        if (plugin != null) {
            Object value = FieldUtils.getFieldValue(input, source.getField());
            if (value instanceof Collection) {
                Stream<String> stream = ((Collection) value).stream().map(obj -> this.getSourceContent(obj, plugin, request));

                return stream.collect(Collectors.joining(separator));
            }

            return this.getSourceContent(value, plugin, request);
        }

        return FieldUtils.getFieldValueAsString(input, source.getField(), separator);
    }

    private String getSourceContent(Object value, AbstractPlugin pluginDesc, SpiderRequest request) {
        if (value == null) {
            return StringUtils.EMPTY;
        }

        AbstractProcessorContext context = request.getProcessorContext();
        String content = (String) PluginCaller.call(pluginDesc, context, () -> {
            Map<String, String> params = new HashMap<>();
            if (value instanceof WrappedFile) {
                WrappedFile file = (WrappedFile) value;
                file.download();//download attachment to local
                params.put(PluginConstants.FILE_WAPPER_PATH, file.getAbsolutePath());
                params.put(PluginConstants.FILE_MIME_TYPE, file.getMimeType());
                params.put(PluginConstants.FILE_NAME, file.getName());
                params.put(PluginConstants.FILE_SOURCE_URL, file.getSourceURL());
            } else if (value instanceof String) {
                params.put(PluginConstants.FILE_CONTENT, (String) value);
            } else {
                params.put(PluginConstants.FILE_CONTENT, value.toString());
                logger.warn("Process page source by plugin with unexpected input type. input: {}, type: {}", value, value.getClass());
            }

            return params;
        });

        logger.debug("Page-source content : {}", content);

        return content;
    }
}
