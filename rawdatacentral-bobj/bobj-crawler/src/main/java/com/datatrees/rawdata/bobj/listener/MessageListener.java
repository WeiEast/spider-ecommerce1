/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.bobj.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.executor.impl.AbstractTaskModule;
import com.datatrees.rawdatacentral.bobj.common.CrawlerConstant;
import com.datatrees.rawdatacentral.bobj.domain.CrawlerMessage;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 下午3:35:36
 */
public class MessageListener extends AbstractTaskModule {



    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.common.executor.impl.AbstractTaskModule#process()
     */
    @Override
    public void process() {
        while (true) {
            // get message from interface with size 
            List<CrawlerMessage> messages = new ArrayList<CrawlerMessage>();
            
            //messageCrawlerPool.submit(task)

        }

    }

}
