package com.datatrees.crawler.core.util.xml;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:03 AM
 */
public interface ConfigParser {

    <T> T parse(String config, Class<T> type) throws Exception;

    <T> T parse(String config, Class<T> type, ParentConfigHandler handler) throws Exception;

}
