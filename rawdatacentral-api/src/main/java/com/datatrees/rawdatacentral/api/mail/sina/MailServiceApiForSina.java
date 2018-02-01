package com.datatrees.rawdatacentral.api.mail.sina;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
public interface MailServiceApiForSina {

    HttpResult<Map<String, Object>> loginInit(CommonPluginParam param);

    HttpResult<Map<String, Object>> login(CommonPluginParam param);

    HttpResult<Map<String, Object>> refeshPicCode(CommonPluginParam param);
}
