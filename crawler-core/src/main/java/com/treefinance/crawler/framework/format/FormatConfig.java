package com.treefinance.crawler.framework.format;

import java.io.Serializable;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.extractor.FieldExtractor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Jerry
 * @since 16:55 2018/7/16
 */
public class FormatConfig implements Serializable {

    private Request  request;
    private Response response;
    private String   pattern;

    public FormatConfig(Request request, Response response, FieldExtractor fieldExtractor) {
        this(request, response, fieldExtractor.getFormat());
    }

    public FormatConfig(Request request, Response response, String pattern) {
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("pattern", pattern).toString();
    }
}
