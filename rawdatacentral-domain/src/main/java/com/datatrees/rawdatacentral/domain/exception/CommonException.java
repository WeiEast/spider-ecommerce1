package com.datatrees.rawdatacentral.domain.exception;

import com.datatrees.rawdatacentral.domain.enums.ErrorCode;

/**
 * 通用异常
 * Created by zhouxinghai on 2017/8/2
 */
public class CommonException extends RuntimeException {

    /**
     * 错误代码
     */
    private int    errorCode;
    /**
     * 错误信息
     */
    private String errorMsg;

    public CommonException() {
        super();
    }

    public CommonException(int errorCode, String errorMsg) {
        super("{\"errorCode\":" + errorCode + ",\"errorMsg\":" + errorMsg + "}");
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonException(String errorMsg) {
        super("{\"errorMsg\":" + errorMsg + "}");
        this.errorMsg = errorMsg;
    }

    public CommonException(int errorCode, String errorMsg, Throwable cause) {
        super("{\"errorCode\":" + errorCode + ",\"errorMsg\":" + errorMsg + "}", cause);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public CommonException(ErrorCode errorcode, Throwable cause) {
        super("{\"errorCode\":" + errorcode.getErrorCode() + ",\"errorMsg\":" + errorcode.getErrorMsg() + "}", cause);
        this.errorCode = errorcode.getErrorCode();
        this.errorMsg = errorcode.getErrorMsg();
    }

    public CommonException(ErrorCode errorcode) {
        super("{\"errorCode\":" + errorcode.getErrorCode() + ",\"errorMsg\":" + errorcode.getErrorMsg() + "}");
        this.errorCode = errorcode.getErrorCode();
        this.errorMsg = errorcode.getErrorMsg();
    }

}
