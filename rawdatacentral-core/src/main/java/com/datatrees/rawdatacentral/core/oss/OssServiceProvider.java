package com.datatrees.rawdatacentral.core.oss;


import com.datatrees.rawdatacentral.core.common.SubmitConstant;

public class OssServiceProvider {

    private OssServiceProvider() {
    }

    /**
     * 获取默认的{@link OssService}
     * @return
     */
    public static OssService getDefaultService() {
        return OssServiceHolder.service;
    }

    /**
     * 获取{@link OssService}
     * @param endpoint        OSS服务的Endpoint
     * @param accessKeyId     阿里云用户accessKeyId
     * @param accessKeySecret 阿里云用户accessKeySecret
     * @return
     */
    public static OssService getService(String endpoint, String accessKeyId, String accessKeySecret) {
        return new OssService(endpoint, accessKeyId, accessKeySecret);
    }

    private static class OssServiceHolder {

        private static OssService service = new OssService(SubmitConstant.ALIYUN_OSS_ENDPOINT, SubmitConstant.ALIYUN_OSS_ACCESSID, SubmitConstant.ALIYUN_OSS_ACCESSSECRET);
    }
}
