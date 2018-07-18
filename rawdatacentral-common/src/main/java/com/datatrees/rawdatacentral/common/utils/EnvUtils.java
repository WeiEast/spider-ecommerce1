package com.datatrees.rawdatacentral.common.utils;

import com.datatrees.rawdatacentral.domain.constant.AttributeKey;

public class EnvUtils {

    public static String getSassEnv() {
        return System.getProperty(AttributeKey.SAAS_ENV, "none");
    }
}
