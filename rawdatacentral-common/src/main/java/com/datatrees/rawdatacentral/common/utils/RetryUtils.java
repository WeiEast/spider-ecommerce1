package com.datatrees.rawdatacentral.common.utils;

import java.util.concurrent.TimeUnit;

import com.datatrees.rawdatacentral.common.retry.RetryHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重试
 */
public class RetryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    /**
     * 自动重试
     * @param retryHandler 重试handler
     * @param maxRetry     最大重试次数
     * @param sleepTime    重试间隔时间
     */
    public static <T> T execute(RetryHandler<T> retryHandler, Integer maxRetry, Long sleepTime) {
        try {
            long startTimes = System.currentTimeMillis();
            T r = retryHandler.execute();
            boolean checkResult = retryHandler.check();
            int retry = 0;
            while (retry <= maxRetry && !checkResult) {
                ++retry;
                logger.debug("check result={},will sleep {}ms", checkResult, sleepTime);
                TimeUnit.MILLISECONDS.sleep(sleepTime);
                r = retryHandler.execute();
                checkResult = retryHandler.check();
            }
            if (!checkResult) {
                logger.warn("execute fail result={},useTimes={},retry={},sleepTime={}ms", checkResult,
                        DateUtils.getUsedTime(startTimes, System.currentTimeMillis()), retry, sleepTime);
                return null;
            }
            logger.info("execute success result={},useTimes={},retry={},sleepTime={}ms", checkResult,
                    DateUtils.getUsedTime(startTimes, System.currentTimeMillis()), retry, sleepTime);
            return r;
        } catch (Throwable e) {
            logger.error("execute error ", e);
            return null;
        }
    }

    public static void main(String[] args) {
        String websiteName = null;

        websiteName = RetryUtils.execute(new RetryHandler<String>() {
            private String websiteName = null;

            private Integer count = 0;

            @Override
            public String execute() {
                count++;
                if (count == 20) {
                    websiteName = "周兴海";
                }
                return websiteName;
            }

            @Override
            public boolean check() {
                return StringUtils.isNotBlank(websiteName);
            }
        }, 100, 500L);
        System.out.println(websiteName);

    }

}
