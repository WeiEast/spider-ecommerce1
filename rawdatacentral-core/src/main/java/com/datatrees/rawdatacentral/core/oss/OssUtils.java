package com.datatrees.rawdatacentral.core.oss;

import static com.datatrees.rawdatacentral.core.common.SubmitConstant.ALIYUN_OSS_OBJECT_PATH_ROOT;

/**
 * @author Jerry
 * @since 11:19 21/06/2017
 */
public final class OssUtils {

    public static final String SEPARATE = "/";

    private OssUtils() {
    }

    public static String getObjectKey(String key) {
        if (key.startsWith(ALIYUN_OSS_OBJECT_PATH_ROOT)) return key;

        if (key.startsWith(SEPARATE)) {
            return ALIYUN_OSS_OBJECT_PATH_ROOT + key;
        }

        return ALIYUN_OSS_OBJECT_PATH_ROOT + SEPARATE + key;
    }
}
