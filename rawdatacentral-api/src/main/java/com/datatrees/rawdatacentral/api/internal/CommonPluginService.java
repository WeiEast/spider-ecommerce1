package com.datatrees.rawdatacentral.api.internal;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 通用插件
 * 使用前自行约定
 * Created by zhouxinghai on 2018/01/04.
 */
public interface CommonPluginService {

    /**
     * 初始化
     * @param param
     * @return
     */
    HttpResult<Object> init(CommonPluginParam param);

    /**
     * 刷新图片验证码
     * @param param
     * @return
     */
    HttpResult<Object> refeshPicCode(CommonPluginParam param);

    /**
     * 刷新短信验证码
     * @param param
     * @return
     */
    HttpResult<Object> refeshSmsCode(CommonPluginParam param);

    /**
     * 验证图片验证码
     * @return
     */
    HttpResult<Object> validatePicCode(CommonPluginParam param);

    /**
     * 登录提交
     * @return
     */
    HttpResult<Object> submit(CommonPluginParam param);

    /**
     * 自定义方法入口
     * @return
     */
    HttpResult<Object> defineProcess(CommonPluginParam param);

}
