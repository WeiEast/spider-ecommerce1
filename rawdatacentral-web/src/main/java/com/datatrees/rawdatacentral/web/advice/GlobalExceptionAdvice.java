package com.datatrees.rawdatacentral.web.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.treefinance.saas.knife.common.CommonStateCode;
import com.treefinance.saas.knife.common.StateCode;
import com.treefinance.saas.knife.result.Results;
import com.treefinance.toolkit.util.http.servlet.ServletResponses;
import com.treefinance.toolkit.util.json.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 对http接口抛出异常的捕获处理
 * Created by guimeichao on 2018/4/16.
 */
@ControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionAdvice.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleHttpMessageNotReadableException(HttpServletRequest request, Exception ex, HttpServletResponse response) {
        responseSystemException(request, ex, HttpStatus.BAD_REQUEST, response);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void handleAllException(HttpServletRequest request, Exception ex, HttpServletResponse response) {
        responseSystemException(request, ex, HttpStatus.INTERNAL_SERVER_ERROR, response);
    }

    private void responseException(HttpServletRequest request, StateCode stateCode, Exception ex, HttpStatus httpStatus,
            HttpServletResponse response) {
        handleLog(request, ex);
        String responseBody = Jackson.toJSONString(Results.newFailedResult(stateCode, ex.getMessage()));
        ServletResponses.responseJson(response, httpStatus.value(), responseBody);
    }

    /**
     * 友好处理未知异常信息
     * @param request
     * @param ex
     * @param httpStatus
     * @param response
     */
    private void responseSystemException(HttpServletRequest request, Exception ex, HttpStatus httpStatus, HttpServletResponse response) {
        handleLog(request, ex);
        String responseBody = Jackson.toJSONString(Results.newFailedResult(CommonStateCode.FAILURE));
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
