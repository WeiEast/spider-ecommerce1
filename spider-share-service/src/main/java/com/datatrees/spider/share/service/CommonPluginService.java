package com.datatrees.spider.share.service;

import java.util.List;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.LoginMessage;
import com.datatrees.spider.share.domain.http.Cookie;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 通用插件服务
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

    /**
     * 发送登陆成功消息
     * @param loginMessage
     */
    void sendLoginSuccessMsg(LoginMessage loginMessage);

    /**
     * 发送登陆成功消息
     * @param loginMessage
     */
    void sendLoginSuccessMsg(LoginMessage loginMessage, List<Cookie> cookies);

    /**
     * 刷新登陆二维码
     * @param param
     * @return
     */
    HttpResult<Object> refeshQRCode(CommonPluginParam param);

    /**
     * 查询二维码状态
     * @param param
     * @return
     */
    HttpResult<Object> queryQRStatus(CommonPluginParam param);

}
