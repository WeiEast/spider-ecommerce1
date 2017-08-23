package com.datatrees.crawler.core.domain.config.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 9:28:58 PM
 */
@Tag("parser")
public class Parser extends AbstractBeanDefinition implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1249743371163334883L;
    private List<ParserPattern> patterns;
    private String              urlTemplate;
    private String              linkUrlTemplate;
    private String              headers;
    private Integer             sleepSecond;

    public Parser() {
        super();
        patterns = new ArrayList<ParserPattern>();
    }

    @Tag("url-template")
    public String getUrlTemplate() {
        return urlTemplate;
    }

    @Node("url-template/text()")
    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    @Tag("patterns")
    public List<ParserPattern> getPatterns() {
        return Collections.unmodifiableList(patterns);
    }

    @Node(value = "patterns/pattern", types = {ParserPattern.class})
    public void setPatterns(ParserPattern pattern) {
        this.patterns.add(pattern);
    }

    @Tag("link-url-template")
    public String getLinkUrlTemplate() {
        return linkUrlTemplate;
    }

    @Node("link-url-template/text()")
    public void setLinkUrlTemplate(String linkUrlTemplate) {
        this.linkUrlTemplate = linkUrlTemplate;
    }

    @Tag("headers")
    public String getHeaders() {
        return headers;
    }

    @Node("headers/text()")
    public void setHeaders(String headers) {
        this.headers = headers;
    }

    @Tag("sleep-second")
    public Integer getSleepSecond() {
        return sleepSecond;
    }

    @Node("sleep-second/text()")
    public void setSleepSecond(Integer sleepSecond) {
        this.sleepSecond = sleepSecond;
    }

}
