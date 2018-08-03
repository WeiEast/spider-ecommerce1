package com.datatrees.spider.share.domain.exception;

import com.datatrees.spider.share.domain.ErrorCode;

/**
 * 登陆超时
 * Created by zhouxinghai on 2017/6/2.
 */
public class LoginTimeOutException extends TaskRuntimeException {

    public LoginTimeOutException(long taskId) {
        super(taskId, ErrorCode.LOGIN_TIMEOUT_ERROR.getErrorCode(), "登陆超时");
    }

    public LoginTimeOutException(String s) {
        super(s);
    }
}
