/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.DateUtils;
import com.datatrees.crawler.core.processor.Constants;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月8日 下午3:44:34
 */
public class FileUtils {

    private static String filePathPrfix = PropertiesConfiguration.getInstance().get(Constants.DOWNLOAD_FILE_STORE_PATH, "/tmp/file/");

    static {
        org.apache.commons.io.FileUtils.deleteQuietly(new File(filePathPrfix));
    }

    private static boolean mkDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    public static String getFileRandomPath(String siteName) {
        String timeString = DateUtils.baseFormatDateTime(new Date(), "yyyyMMdd");
        String parentPath = filePathPrfix + timeString + "/" + siteName;
        FileUtils.mkDir(parentPath);
        return parentPath + "/" + UUID.randomUUID();
    }
}
