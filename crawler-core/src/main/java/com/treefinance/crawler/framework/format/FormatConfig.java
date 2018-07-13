package com.treefinance.crawler.framework.format;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

import com.datatrees.common.conf.Configuration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.treefinance.crawler.framework.format.datetime.DateTimeFormats;
import com.treefinance.crawler.framework.format.number.NumberUnit;
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

    private final       Request  request;
    private final       Response response;
    private final       String   pattern;

    public FormatConfig(@Nonnull Request request, @Nonnull Response response, @Nonnull FieldExtractor fieldExtractor) {
        this(request, response, fieldExtractor.getFormat());
    }

    public FormatConfig(@Nonnull Request request, @Nonnull Response response, String pattern) {
        this.request = request;
        this.response = response;
        this.pattern = pattern;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
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
        return RequestUtil.getDateFormat(request);
    }

    public DateTimeFormatter getDateTimeFormatter(String pattern) {
        return getDateTimeFormats().getFormatter(pattern);
    }

    @Nonnull
    public Map<String, NumberUnit> getNumberFormatMap(Configuration conf) {
        return RequestUtil.getNumberFormat(request, conf);
    }

    public AbstractProcessorContext getProcessorContext() {
        return RequestUtil.getProcessorContext(request);
    }

    public Object getSourceFieldValue(String fieldName) {
        return SourceUtils.getSourceValue(fieldName, request, response);
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
