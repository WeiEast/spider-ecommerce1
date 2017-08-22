/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor;

import org.junit.Test;

import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.page.impl.Page;
import com.datatrees.crawler.core.domain.config.search.SearchTemplateConfig;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   Mar 7, 2014 4:43:51 PM 
 */
public class SearchProcessorContextTest  extends BaseConfigTest {
    
    @Test
    public void testGetWebsite(){
        try {
            Website website = new Website() ;
            SearchConfig config= getSearchConfig("config.xml");
            website.setSearchConfig(config);
            website.setWebsiteName("xxxxx");
            System.out.println(website.getWebsiteName());
            
            String templdateID = "related_search"; 
            SearchProcessorContext wrapper = new SearchProcessorContext(website);
            SearchTemplateConfig tconfig = wrapper.getSearchTempldateConfig(templdateID);
            System.out.println("total size.."+tconfig.getSearchSequence().size());
            
            
            templdateID = "deep"; 
           Page page = wrapper.getPageDefination(new LinkNode("http://baidu.com"), templdateID);
           System.out.println(page);
            
        } catch (ParseException e) {
            e.printStackTrace();
        } 
        
    }

}
