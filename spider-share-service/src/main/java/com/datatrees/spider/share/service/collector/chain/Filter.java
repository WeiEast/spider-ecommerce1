package com.datatrees.spider.share.service.collector.chain;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午2:35:23
 */
public interface Filter {

    void doFilter(Context context, FilterChain filterChain);
}
