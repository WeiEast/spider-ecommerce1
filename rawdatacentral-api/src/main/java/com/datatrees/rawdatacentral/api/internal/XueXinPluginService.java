package com.datatrees.rawdatacentral.api.internal;

/**
 * Created by wangpan on 4/28/18 10:36 AM
 */

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 学信网注册功能
 */
public interface XueXinPluginService {

    /**
     * 注册初始化
     * @param param
     * @return
     */
    HttpResult<Object> registerInit(CommonPluginParam param);

    /**
     * 注册刷新验证码
     * @param param
     * @return
     */
    HttpResult<Object> registerRefreshPicCode(CommonPluginParam param);

    /**
     * 注册刷新验证码和校验码
     * @param param
     * @return
     */
    HttpResult<Object> registerValidatePicCodeAndSendSmsCode(CommonPluginParam param);

    /**
     * 注册提交
     * @param param
     * @return
     */
    HttpResult<Object> registerSubmit(CommonPluginParam param);

}
