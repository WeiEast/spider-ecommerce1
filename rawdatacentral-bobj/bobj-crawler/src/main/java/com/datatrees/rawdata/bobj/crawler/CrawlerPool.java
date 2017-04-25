/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdata.bobj.crawler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.datatrees.rawdata.bobj.common.CrawlerConstant;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 下午5:04:18
 */
public class CrawlerPool {
    
    private ThreadPoolExecutor messageCrawlerPool = new ThreadPoolExecutor(CrawlerConstant.CRAWLER_CORE_THREAD_NUM,
            CrawlerConstant.CRAWLER_MAX_THREAD_NUM, 1L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(CrawlerConstant.CRAWLER_MAX_TASK_NUM), new ThreadPoolExecutor.CallerRunsPolicy());
    

}
