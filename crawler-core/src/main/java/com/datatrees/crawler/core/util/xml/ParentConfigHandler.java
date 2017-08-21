package com.datatrees.crawler.core.util.xml;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:03 AM
 */
public interface ParentConfigHandler {
    <T> T parse(T type) throws Exception;
}
