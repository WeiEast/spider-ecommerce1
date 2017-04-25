package com.datatrees.rawdata.evidence.oss;

import com.datatrees.rawdata.evidence.common.SubmitConstant;

public class OssServiceProvider {
	private static class OssServiceHolder {
		private static OssService service = new OssService(
				SubmitConstant.ALIYUN_OSS_ENDPOINT,
				SubmitConstant.ALIYUN_OSS_ACCESSID,
				SubmitConstant.ALIYUN_OSS_ACCESSSECRET);
	}

	/**
	 * 获取默认的{@link OssService}
	 * 
	 * @return
	 */
	public static OssService getDefaultService() {
		return OssServiceHolder.service;
	}

	/**
	 * 获取{@link OssService}
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint
	 * @param accessKeyId
	 *            阿里云用户accessKeyId
	 * @param accessKeySecret
	 *            阿里云用户accessKeySecret
	 * @return
	 */
	public static OssService getService(String endpoint, String accessKeyId,
			String accessKeySecret) {
		return new OssService(endpoint, accessKeyId, accessKeySecret);
	}

	private OssServiceProvider(){
	}
}
