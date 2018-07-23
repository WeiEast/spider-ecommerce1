package com.datatrees.rawdatacentral.api.internal;

/**
 * Created by wangpan on 4/28/18 10:36 AM
 */

import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.spider.share.domain.HttpResult;

/**
 * 学信网注册功能
 */
public interface XueXinPluginService {

    /**
     * 注册初始化
     * @param param
     * @return
     */
    HttpResult<Object> registerInit(EducationParam param);

    /**
     * 注册刷新验证码
     * @param param
     * @return
     */
    HttpResult<Object> registerRefreshPicCode(EducationParam param);

    /**
     * 注册刷新验证码和校验码
     * @param param
     * @return
     */
    HttpResult<Object> registerValidatePicCodeAndSendSmsCode(EducationParam param);

    /**
     * 注册提交
     * @param param
     * @return
     */
    HttpResult<Object> registerSubmit(EducationParam param);

}
