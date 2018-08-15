package com.treefinance.crawler.framework.config.factory;

import com.treefinance.crawler.exception.UnexpectedException;
import com.treefinance.crawler.framework.config.SpiderConfig;
import com.treefinance.crawler.framework.config.factory.xml.XmlConfigParser;
import com.treefinance.crawler.framework.exception.ConfigParseException;

/**
 * @author Jerry
 * @since 15:28 2018/7/13
 */
public class SpiderConfigFactory {

    public static <C extends SpiderConfig> C build(String config, Class<C> configClass) {
        try {
            return XmlConfigParser.newParser().parse(config, configClass);
        } catch (ConfigParseException e) {
            throw new UnexpectedException("Unexpected exception when building crawler config.", e);
        }
    }

    public static <C extends SpiderConfig> C build(String config, Class<C> configClass, ParentConfigHandler<C> parentConfigHandler) {
        try {
            return XmlConfigParser.newParser().parse(config, configClass, parentConfigHandler);
        } catch (ConfigParseException e) {
            throw new UnexpectedException("Unexpected exception when building crawler config.", e);
        }
    }
}
