package com.treefinance.crawler.framework.config.xml;

import com.treefinance.crawler.framework.config.annotation.Attr;
import com.treefinance.crawler.framework.config.annotation.Node;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 7:19:30 PM
 */
public class AbstractBeanDefinition {

    private String id;

    @Attr("id")
    public String getId() {
        return id;
    }

    @Node(value = "@id", types = {String.class})
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

}
