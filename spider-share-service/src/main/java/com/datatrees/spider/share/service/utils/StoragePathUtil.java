/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.share.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.domain.ResultType;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月30日 下午8:24:48
 */
public class StoragePathUtil {

    private static boolean useOriginalPathFlag = PropertiesConfiguration.getInstance().getBoolean("use.original.path.flag", false);

    public static String genStoragePath(int taskId, String uuid) {
        StringBuffer buffer = new StringBuffer();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return buffer.append(date).append("_").append(taskId).append("_").append(uuid).toString();
    }

    public static String genStoragePath(ExtractMessage extractMessage, String uniqueMd5) {
        String bucket;
        if (useOriginalPathFlag) {
            bucket = extractMessage.getTaskLogId() + "/" + extractMessage.getWebsiteId();
        } else {
            String date = new SimpleDateFormat("yyyyMM").format(new Date());
            ResultType resultType = extractMessage.getResultType();
            String resultTypeStr = null;
            if (resultType != null) {
                resultTypeStr = resultType.getValue();
            } else {
                resultTypeStr = "UnKnown";
            }
            bucket = resultTypeStr + "/" + date + "/" + extractMessage.getTaskLogId() + "/" + extractMessage.getWebsiteId();
        }
        return extractMessage.getMessageIndex() == null ? bucket + "/" + uniqueMd5 :
                bucket + "/" + uniqueMd5 + "_" + extractMessage.getMessageIndex();

    }
}
