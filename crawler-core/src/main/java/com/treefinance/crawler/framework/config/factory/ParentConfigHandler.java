package com.treefinance.crawler.framework.config.factory;

import javax.annotation.Nonnull;

import com.treefinance.crawler.framework.config.SpiderConfig;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:03 AM
 */
public interface ParentConfigHandler<T extends SpiderConfig> {

    T handle(@Nonnull T type) throws Exception;
}
