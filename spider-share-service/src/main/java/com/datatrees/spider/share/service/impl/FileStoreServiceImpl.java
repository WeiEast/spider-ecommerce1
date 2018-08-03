package com.datatrees.spider.share.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.spider.share.service.FileStoreService;
import com.datatrees.spider.share.service.constants.SubmitConstant;
import com.datatrees.spider.share.service.domain.SubmitMessage;
import com.datatrees.spider.share.service.extra.UploadTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileStoreServiceImpl implements FileStoreService {

    private static final Logger          LOGGER     = LoggerFactory.getLogger(FileStoreServiceImpl.class);

    private              ExecutorService threadPool = new ThreadPoolExecutor(5, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            new ThreadFactoryImpl("SubmitProcessor_"));

    private              List<String>    needUploadList;

    public FileStoreServiceImpl() {
        needUploadList = Arrays.asList(SubmitConstant.SUBMITTER_NEEDUPLOAD_KEY.split(" *, *"));
    }

    @Override
    public void storeEviFile(SubmitMessage submitMessage) {
        try {
            LOGGER.info("store envidevce file to oss by key: {}", submitMessage.getResult().getStoragePath());
            UploadTask task = new UploadTask(submitMessage.getExtractMessage(), needUploadList, submitMessage.getResult().getStoragePath());

            threadPool.submit(task);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
