package com.datatrees.rawdatacentral.web.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.common.StateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.toolkit.util.http.servlet.ServletResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by guimeichao on 2018/4/17.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public void handle(HttpServletRequest request, IllegalArgumentException ex, HttpServletResponse response){
        responseException(request, CommonStateCode.FAILURE, ex, "websiteName或enable不能为空", HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handle(HttpServletRequest request, HttpMessageNotReadableException ex, HttpServletResponse response){
        responseException(request, CommonStateCode.FAILURE, ex, "请求body异常", HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler(Exception.class)
    public void handle(HttpServletRequest request, Exception ex, HttpServletResponse response){
        responseException(request, CommonStateCode.FAILURE, ex, "系统异常", HttpStatus.FORBIDDEN, response);
    }

    private void responseException(HttpServletRequest request, StateCode stateCode, Exception ex, String statusText,
            HttpStatus httpStatus, HttpServletResponse response) {
        handleLog(request, ex);
        String responseBody = JSON.toJSONString(Results.newFailedResult(stateCode, statusText));
        ServletResponses.responseJson(response, httpStatus.value(), responseBody);
    }

    private void handleLog(HttpServletRequest request, Exception ex) {
        StringBuffer logBuffer = new StringBuffer();
        if (request != null) {
            logBuffer.append("request method=" + request.getMethod());
            logBuffer.append(",url=" + request.getRequestURL());
        }
        if (ex != null) {
            logBuffer.append(",exception:" + ex);
        }
        logger.error(logBuffer.toString(), ex);
    }
}
