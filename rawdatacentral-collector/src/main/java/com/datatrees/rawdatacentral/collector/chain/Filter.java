package com.datatrees.rawdatacentral.collector.chain;

/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月29日 上午2:35:23 
 */
public interface Filter {
    public void doFilter(Context context, FilterChain filterChain);
}
