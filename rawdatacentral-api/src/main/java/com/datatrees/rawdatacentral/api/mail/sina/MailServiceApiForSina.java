package com.datatrees.rawdatacentral.api.mail.sina;

import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
public interface MailServiceApiForSina {

    /**
     * 登录初始化接口
     * 必填参数：taskId
     * 返回结果
     * 详见:@see com.datatrees.rawdatacentral.domain.result.HttpResult
     * @return
     */
    HttpResult<Object> init(CommonPluginParam param);

    /**
     * 登录提交接口
     * 必填参数：taskId，username，password
     * 选填参数：picCode
     * 返回结果
     * 详见:@see com.datatrees.rawdatacentral.domain.result.HttpResult
     * @return
     */
    HttpResult<Object> login(CommonPluginParam param);

    /**
     * 刷新图片接口
     * 必填参数：taskId
     * 返回结果
     * 详见:@see com.datatrees.rawdatacentral.domain.result.HttpResult
     * @return
     */
    HttpResult<Object> refeshPicCode(CommonPluginParam param);
}
