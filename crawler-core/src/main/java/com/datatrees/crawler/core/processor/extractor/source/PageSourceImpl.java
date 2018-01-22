/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor.source;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.PageSource;
import com.datatrees.crawler.core.domain.config.page.Regexp;
import com.datatrees.crawler.core.domain.config.page.Replacement;
import com.datatrees.crawler.core.domain.config.plugin.AbstractPlugin;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.Processor;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.util.SourceFieldUtils;
import com.datatrees.crawler.core.processor.page.PageHelper;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.google.common.base.Preconditions;
import com.treefinance.crawler.framework.extension.plugin.PluginCaller;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月14日 下午10:23:47
 */
public class PageSourceImpl extends Processor {

    private static final Logger log = LoggerFactory.getLogger(PageSourceImpl.class);
    private List<PageSource> pageSourceList;

    /**
     * @param pageSourceList
     */
    public PageSourceImpl(List<PageSource> pageSourceList) {
        super();
        this.pageSourceList = pageSourceList;
    }

    private String getSourceWithPlugin(Request request, AbstractPlugin pluginDesc, Object value) throws Exception {
        AbstractProcessorContext context = RequestUtil.getProcessorContext(request);

        Object respOutput = PluginCaller.call(context, pluginDesc,  () -> {
            Map<String, String> params = new LinkedHashMap<>();
            if (value instanceof FileWapper) {
                FileWapper file = (FileWapper) value;
                file.getFileInputStream();//download attachment to local
                params.put(PluginConstants.FILE_WAPPER_PATH, file.getAbsolutePath());
                params.put(PluginConstants.FILE_MIME_TYPE, file.getMimeType());
                params.put(PluginConstants.FILE_NAME, file.getName());
                params.put(PluginConstants.FILE_SOURCE_URL, file.getSourceURL());
            } else if (value instanceof String) {
                params.put(PluginConstants.FILE_CONTENT, (String) value);
            } else {
                log.warn("error getSourceWithPlugin with " + value);
            }

            return params;
        });

        if (log.isDebugEnabled()) {
            log.debug("getSourceWithPlugin plugin:" + pluginDesc + ", result:" + respOutput);
        }

        return (String) respOutput;
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        StringBuilder builder = new StringBuilder();
        Object input = request.getInput();
        Preconditions.checkNotNull(input, "input should not be null!");
        for (PageSource source : pageSourceList) {
            String split = StringUtils.defaultIfEmpty(source.getSplit(), "");
            StringBuilder sourceValue = new StringBuilder();
            if (source.getPlugin() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("getSource source:" + source + ", plugin:" + source.getPlugin());
                }
                Object value = SourceFieldUtils.getFieldValue(input, source.getField());
                if (value instanceof Collection) {
                    for (Object obj : (Collection) value) {
                        sourceValue.append(this.getSourceWithPlugin(request, source.getPlugin(), obj)).append(split);
                    }
                } else {
                    sourceValue.append(this.getSourceWithPlugin(request, source.getPlugin(), value));
                }
            } else {
                String value = RequestUtil.getAttribute(request, source.getField());
                if (StringUtils.isNotBlank(split) || value == null) {
                    value = SourceFieldUtils.getFieldValueAsString(input, source.getField(), split);
                }
                sourceValue.append(value);
            }

            String result = sourceValue.toString();
            List<Replacement> replacements = source.getReplacements();
            if (CollectionUtils.isNotEmpty(replacements)) {
                result = PageHelper.replaceText(result, replacements);
            }
            Regexp regexp = source.getRegexp();
            if (regexp != null) {
                result = PageHelper.getTextByRegexp(result, regexp);
            }
            if (log.isDebugEnabled()) {
                log.debug("getSource source:" + source + ", result:" + result);
            }
            // set source field to context
            RequestUtil.getProcessorContext(request).getContext().put(source.getField(), result);
            builder.append(result);
        }
        if (log.isDebugEnabled()) {
            log.debug("set request page content:" + builder.toString());
        }
        RequestUtil.setContent(request, builder.toString());
    }

}
