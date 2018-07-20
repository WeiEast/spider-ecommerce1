package com.datatrees.rawdatacentral.domain.exception;

import com.datatrees.spider.share.domain.ErrorCode;

/**
 * 登陆失败
 * Created by zhouxinghai on 2017/6/2.
 */
public class LoginFailException extends TaskRuntimeException {

    public LoginFailException(long taskId, String errorMsg) {
        super(taskId, ErrorCode.LOGIN_FAIL.getErrorCode(), errorMsg);
    }
}
