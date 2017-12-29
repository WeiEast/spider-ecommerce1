package com.datatrees.rawdatacentral.api.mail.qq;

import java.util.Map;

import com.datatrees.rawdatacentral.domain.mail.MailParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * qq模拟登陆接口
 * @author zhouxinghai
 * @date 2017/12/29
 */
public interface MailServiceApiForQQ {

    /**
     * 提交登陆请求
     * 必填参数: taskId,username,password
     * @return LOGIN_SUCCESS, LOGIN_PROCESSING, LOGIN_FAILED
     */
    HttpResult<Map<String, String>> login(MailParam param);

    /**
     * 轮训登陆状态
     * 必填参数: taskId,directiveId
     * @return LOGIN_SUCCESS, LOGIN_PROCESSING, LOGIN_FAILED
     */
    HttpResult<Map<String, String>> queryLoginStatus(MailParam param);

}
