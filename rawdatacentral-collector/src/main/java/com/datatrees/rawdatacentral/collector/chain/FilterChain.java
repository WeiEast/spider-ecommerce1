package com.datatrees.rawdatacentral.collector.chain;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:35:30
 */
public class FilterChain {

    private static final Logger logger = LoggerFactory.getLogger(FilterChain.class);
    private List<Filter> filters;
    private int pos = 0;

    public FilterChain(List<Filter> filters) {
        if (filters != null) {
            this.filters = filters;
        } else {
            this.filters = Collections.emptyList();
        }
    }

    public void doFilter(Context context) {
        if (this.pos < this.filters.size()) {
            Filter nextFilter = this.filters.get(pos++);

            if (logger.isTraceEnabled()) {
                logger.trace("Call filter: {}", nextFilter.getClass().getName());
            }

            nextFilter.doFilter(context, this);
        }
    }
}
