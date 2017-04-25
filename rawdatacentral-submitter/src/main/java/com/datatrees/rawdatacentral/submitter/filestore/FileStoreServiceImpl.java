package com.datatrees.rawdatacentral.submitter.filestore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.rawdatacentral.core.model.SubmitMessage;

@Service
public class FileStoreServiceImpl implements FileStoreService {

    private static final Logger logger = LoggerFactory.getLogger(FileStoreServiceImpl.class);

    // private ThreadPoolExecutor uploadThreadPool = new
    // ThreadPoolExecutor(SubmitConstant.SUBMITTER_UPLOAD_CORE_THREAD_NUM,
    // SubmitConstant.SUBMITTER_UPLOAD_MAX_THREAD_NUM, 30L, TimeUnit.SECONDS, new
    // LinkedBlockingQueue<Runnable>(
    // SubmitConstant.SUBMITTER_UPLOAD_MAX_TASK_NUM), new ThreadPoolExecutor.CallerRunsPolicy());

    ExecutorService SubmitProcessorPool = Executors.newCachedThreadPool(new ThreadFactoryImpl("SubmitProcessor_"));

    private List<String> needUploadList;

    public FileStoreServiceImpl() {
        needUploadList = Arrays.asList(SubmitConstant.SUBMITTER_NEEDUPLOAD_KEY.split(" *, *"));
    }

    @Override
    public void storeEviFile(SubmitMessage submitMessage) {
        try {
            logger.info("store envidevce file to oss by key: " + submitMessage.getResult().getStoragePath());
            UploadTask task = new UploadTask(submitMessage.getExtractMessage(), needUploadList, submitMessage.getResult().getStoragePath());
            SubmitProcessorPool.submit(task);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
