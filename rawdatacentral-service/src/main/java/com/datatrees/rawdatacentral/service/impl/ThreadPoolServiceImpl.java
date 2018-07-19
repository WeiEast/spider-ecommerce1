package com.datatrees.rawdatacentral.service.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.api.internal.ThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ThreadPoolServiceImpl implements ThreadPoolService {

    private static final Logger             logger = LoggerFactory.getLogger(ThreadPoolServiceImpl.class);

    private              ThreadPoolExecutor mailLoginExecutors;

    @Override
    public ThreadPoolExecutor getMailLoginExecutors() {
        int corePoolSize = PropertiesConfiguration.getInstance().getInt("mail.login.thread.min", 10);
        int maximumPoolSize = PropertiesConfiguration.getInstance().getInt("mail.login.thread.max", 100);
        if (null == mailLoginExecutors) {
            mailLoginExecutors = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(300),
                    new ThreadFactory() {
                        private AtomicInteger count = new AtomicInteger(0);

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread t = new Thread(r);
                            String threadName = "mail_login_thread_" + count.addAndGet(1);
                            t.setName(threadName);
                            logger.info("create mail login thread :{}", threadName);
                            return t;
                        }
                    });

        }
        return mailLoginExecutors;
    }

}
