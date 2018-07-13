package com.treefinance.crawler.framework.config.factory;

import com.datatrees.crawler.core.domain.config.AbstractWebsiteConfig;
import com.treefinance.crawler.framework.config.CrawlerConfig;
import com.treefinance.crawler.framework.exception.ConfigParseException;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:03 AM
 */
public interface ConfigParser {

    <T extends CrawlerConfig> T parse(String config, Class<T> type) throws ConfigParseException;

    <T extends CrawlerConfig> T parse(String config, Class<T> type, ParentConfigHandler<T> handler) throws ConfigParseException;

}
