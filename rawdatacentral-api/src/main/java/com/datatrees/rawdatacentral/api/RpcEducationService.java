package com.datatrees.rawdatacentral.api;

import com.datatrees.rawdatacentral.domain.education.EducationParam;
import com.datatrees.spider.share.domain.HttpResult;

/**
 * 学信网接口
 * Created by zhangyanjia on 2017/12/1.
 */
public interface RpcEducationService {

    /**
     * 学信网登录初始化接口
     * @param param
     * @return
     */
    HttpResult<Object> loginInit(EducationParam param);

    /**
     * 学信网登录提交接口
     * @param param
     * @return
     */
    HttpResult<Object> loginSubmit(EducationParam param);

    /**
     * 注册初始化
     * @param param
     * @return
     */
    HttpResult<Object> registerInit(EducationParam param);

    /**
     * 学信网注册刷新图片接口
     * @param param
     * @return
     */
    HttpResult<Object> registerRefeshPicCode(EducationParam param);

    /**
     * 注册验证图片验证码,成功则直接发送短信验证码
     * @return
     */
    HttpResult<Object> registerValidatePicCodeAndSendSmsCode(EducationParam param);

    /**
     * 注册提交
     * @return
     */
    HttpResult<Object> registerSubmit(EducationParam param);
}
