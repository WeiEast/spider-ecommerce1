package com.treefinance.crawler.framework.format;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import com.treefinance.crawler.framework.format.datetime.DateTimeFormats;
import com.treefinance.crawler.framework.format.number.NumberUnit;
import com.treefinance.crawler.framework.format.number.NumberUnitMapping;
import com.treefinance.crawler.framework.util.SourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Jerry
 * @since 16:55 2018/7/16
 */
public class FormatConfig implements Serializable {

    private final       SpiderRequest  request;
    private final       SpiderResponse response;
    private final       String   pattern;

    public FormatConfig(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response, @Nonnull FieldExtractor fieldExtractor) {
        this(request, response, fieldExtractor.getFormat());
    }

    public FormatConfig(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response, String pattern) {
        this.request = request;
        this.response = response;
        this.pattern = pattern;
    }

    public SpiderRequest getRequest() {
        return request;
    }

    public SpiderResponse getResponse() {
        return response;
    }

    public String getPattern() {
        return pattern;
    }

    public String trimmedPattern() {
        return StringUtils.trimToEmpty(pattern);
    }

    @Nonnull
    public DateTimeFormats getDateTimeFormats() {
        return (DateTimeFormats) request.computeAttributeIfAbsent(Constants.CRAWLER_DATE_FROMAT, k -> new DateTimeFormats());
    }

    public DateTimeFormatter getDateTimeFormatter(String pattern) {
        return getDateTimeFormats().getFormatter(pattern);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public Map<String, NumberUnit> getNumberFormatMap(Configuration conf) {
        return (Map<String, NumberUnit>) request.computeAttributeIfAbsent(Constants.CRAWLER_REQUEST_NUMBER_MAP, key -> NumberUnitMapping.getNumberUnitMap(conf));
    }

    public AbstractProcessorContext getProcessorContext() {
        return request.getProcessorContext();
    }

    public Object getSourceFieldValue(String fieldName) {
        return SourceUtils.getSourceFieldValue(fieldName, request, response);
    }

    public LinkNode getCurrentLinkNode() {
        return RequestUtil.getCurrentUrl(request);
    }

    public String getCurrentUrl() {
        LinkNode linkNode = getCurrentLinkNode();
        return linkNode == null ? null : linkNode.getUrl();
    }

    @Nonnull
    public FormatConfig withPattern(String pattern) {
        return new FormatConfig(request, response, pattern);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("pattern", pattern).toString();
    }
}
