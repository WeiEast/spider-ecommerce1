package com.datatrees.rawdatacentral.collector.chain;


import java.util.List;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月29日 上午2:36:37 
 */
public enum FilterExecutor {
    INSTANCE;

    public void execut(Context context, List<Filter> filterList) {
        FilterChain bizChain = new FilterChain();
        for (Filter filter : filterList) {
            bizChain.addFilter(filter);
        }
        bizChain.doFilter(context, bizChain);
    }
}
