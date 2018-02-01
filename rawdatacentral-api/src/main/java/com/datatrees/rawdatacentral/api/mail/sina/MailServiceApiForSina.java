package com.datatrees.rawdatacentral.api.mail.sina;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
public interface MailServiceApiForSina {

    HttpResult<Object> init(CommonPluginParam param);

    HttpResult<Object> login(CommonPluginParam param);

    HttpResult<Object> refeshPicCode(CommonPluginParam param);
}
