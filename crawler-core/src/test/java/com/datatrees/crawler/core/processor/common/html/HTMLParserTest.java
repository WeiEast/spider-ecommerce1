package com.datatrees.crawler.core.processor.common.html;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * @author Jerry
 * @since 22:38 21/05/2017
 */
public class HTMLParserTest {

    @Test
    public void parse() throws Exception {
        File f = new File("/tmp/a.html");
        String pageContent = FileUtils.readFileToString(f);
        String baseUrl = "https://www.google.com/";
        HTMLParser hp = new HTMLParser();
        hp.parse(pageContent, baseUrl);
        HashMap<String, String> links = hp.getLinks();
        System.out.println("total size..." + links.size());
        for (String key : links.keySet()) {
            System.out.println("key : " + key + " val: " + links.get(key));
        }
    }

}