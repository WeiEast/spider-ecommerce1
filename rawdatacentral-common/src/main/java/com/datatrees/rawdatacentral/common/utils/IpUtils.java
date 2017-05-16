package com.datatrees.rawdatacentral.common.utils;

import java.net.InetAddress;

/**
 * ip管理工具
 * Created by zhouxinghai on 2017/5/15.
 */
public class IpUtils {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取本地hostname
     * @return
     */
    public static String getLocalHostName() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getHostName();
        } catch (Exception e) {
            logger.error("getLocalHostName error", e);
        }
        return null;
    }
}
