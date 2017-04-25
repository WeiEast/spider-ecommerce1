
package com.datatrees.rawdatacentral.collector.chain;

import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月29日 上午2:35:30 
 */
public class FilterChain implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(FilterChain.class);

    LinkedBlockingDeque<Filter> filterDeque = new LinkedBlockingDeque<Filter>();

    public void addFilter(Filter filter) {
        filterDeque.add(filter);
    }

    public void doFilter(Context context, FilterChain filterChain) {
        Filter filter = filterDeque.poll();
        if (null != filter) {
            filter.doFilter(context, filterChain);
        } else{
            logger.debug("All FilterChain execute finished ...");
        }
    }
}
