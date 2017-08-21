/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.service;

import java.util.List;

import org.junit.Test;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 10:46:28 AM
 */
public class JSXserviceImplTest extends BaseConfigTest {


    @Test
    public void testJSXservice() {

        Request req = new Request();

        try {
            // config
            Website website = new Website();
            SearchConfig config = getSearchConfig("config.xml");
            website.setSearchConfig(config);
            website.setWebsiteName("xxxxx");
            System.out.println(website.getWebsiteName());
            SearchProcessorContext wrapper = new SearchProcessorContext(website);

            RequestUtil.setProcessorContext(req, wrapper);
            LinkNode url = new LinkNode("http://www.baidu.com",0);
            wrapper.setWebServiceUrl("http://127.0.0.1:8083");
            RequestUtil.setCurrentUrl(req, url);

            List<AbstractService> services = wrapper.getSearchConfig().getServiceList();

            for (AbstractService abstractService : services) {
                ServiceBase base = null;
                try {
                    base = ProcessorFactory.getService(abstractService);
                    Response resp = Response.build();
                    base.invoke(req, resp);
                    // get result
                    String content = ResponseUtil.getResponseContent(resp);
                    // content = RequestUtil.getContent(req);
                    System.out.println("content...." + content);
                    // System.out.println(req.getInput());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

}
