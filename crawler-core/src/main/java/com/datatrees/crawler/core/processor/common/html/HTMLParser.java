/**
 * HTMLParser.java 1.0 Jul 19, 2011 Copyright @ 2011 Vobile, Inc. All right reserved.
 */
package com.datatrees.crawler.core.processor.common.html;

import it.unimi.dsi.parser.BulletParser;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;


/**
 * HTMLParser.java 1.0 Jul 19, 2011
 * 
 * The content parser of page html content.
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 1.0
 * 
 */
public class HTMLParser {
    private BulletParser bulletParser;
    private LinkExtractor linkExtractor;

    /**
     * constructor
     */
    public HTMLParser() {
        bulletParser = new BulletParser();
        linkExtractor = new LinkExtractor();
    }

    /**
     * parse method
     * 
     * @param htmlContent
     * @param contextURL
     */
    public void parse(String htmlContent, String contextURL) {
        char[] chars = htmlContent.toCharArray();
        linkExtractor.setContextURL(contextURL);
        bulletParser.setCallback(linkExtractor);
        bulletParser.parse(chars);
    }

    /**
     * get page title
     * 
     * @return title
     */
    public String getTitle() {
        String title = linkExtractor.title.toString().trim();
        if (StringUtils.isNotEmpty(title) && title.length() > 2048) {
            title = title.substring(0, 2048);
        }
        return new String(title);
    }

    /**
     * get found links
     * 
     * @return links
     */
    public HashMap<String, String> getLinks() {
        return linkExtractor.urls;
    }
}
