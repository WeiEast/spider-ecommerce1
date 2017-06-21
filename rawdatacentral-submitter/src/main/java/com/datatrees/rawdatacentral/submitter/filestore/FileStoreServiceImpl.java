package com.datatrees.rawdatacentral.submitter.filestore;

import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.datatrees.rawdatacentral.core.model.SubmitMessage;
import com.datatrees.rawdatacentral.submitter.common.SubmitConstant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FileStoreServiceImpl implements FileStoreService {

    private static final Logger LOGGER     = LoggerFactory.getLogger(FileStoreServiceImpl.class);

    private ExecutorService     threadPool = new ThreadPoolExecutor(5, 20, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(), new ThreadFactoryImpl("SubmitProcessor_"));

    private List<String>        needUploadList;

    public FileStoreServiceImpl() {
        needUploadList = Arrays.asList(SubmitConstant.SUBMITTER_NEEDUPLOAD_KEY.split(" *, *"));
    }

    @Override
    public void storeEviFile(SubmitMessage submitMessage) {
        try {
            LOGGER.info("store envidevce file to oss by key: " + submitMessage.getResult().getStoragePath());
            UploadTask task = new UploadTask(submitMessage.getExtractMessage(), needUploadList,
                submitMessage.getResult().getStoragePath());

            threadPool.submit(task);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
