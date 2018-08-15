package com.treefinance.crawler.framework.config.factory;

import com.treefinance.crawler.framework.config.SpiderConfig;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 5:48:56 PM
 */
public interface ConfigBuilder {

    <C extends SpiderConfig> String build(C config);

}
