package com.datatrees.rawdatacentral.common.http;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求ID生成器
 * Created by zhouxinghai on 2017/8/31
 */
public class RequestIdUtils {

    private static final AtomicLong ID = new AtomicLong(0);

    /**
     * 生产RequestId
     * @return
     */
    public static long createId() {
        if (ID.get() >= Long.MAX_VALUE - 10000) {
            ID.set(1);
        }
        return ID.incrementAndGet();
    }

}
