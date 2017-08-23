/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.json;

import java.io.*;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.json.impl.JsonParserImpl;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigBuilder;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 17, 2014 3:52:08 PM
 */
public class WebsiteConfigDeserialize {

    @Test
    public void seedConfigTest() {
        try {
            String config = readFile("config.xml");
            SearchConfig websiteConfig = XmlConfigParser.getInstance().parse(config, SearchConfig.class);

            System.out.println(XmlConfigBuilder.getInstance().buildConfig(websiteConfig));
            System.out.println(websiteConfig.toString());

            String json = GsonUtils.toJson(websiteConfig);
            System.out.println(json);
            SearchConfig websiteConfig2 = JsonParserImpl.INSTANCE.parse(json, SearchConfig.class);
            String json2 = GsonUtils.toJson(websiteConfig2);
            System.out.println(json2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFile(String path) {
        String content = "";
        InputStream input = ClassLoader.getSystemResourceAsStream(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String data;
            while ((data = reader.readLine()) != null) content = content + data;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

}
