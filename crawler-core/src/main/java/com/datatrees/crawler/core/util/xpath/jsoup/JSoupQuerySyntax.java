package com.datatrees.crawler.core.util.xpath.jsoup;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月6日 上午12:13:35 
 */
public class JSoupQuerySyntax {

    private static final Logger log = LoggerFactory.getLogger(JSoupQuerySyntax.class);

    public static boolean isValid(String query) {
        // Check for null
        if (StringUtils.isEmpty(query)) {
            return false;
        }
        boolean flag;
        try {
            QueryParser.parse(query);
            flag = true;
        } catch (Exception e) // these exceptions are thrown if something is not ok
        {
            // ignore
            flag = false; // If something is not ok, the query is invalid
        }

        return flag; // All ok, query is valid
    }
}
