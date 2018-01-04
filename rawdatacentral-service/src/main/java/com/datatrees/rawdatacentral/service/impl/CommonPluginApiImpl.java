package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.service.ClassLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommonPluginApiImpl implements CommonPluginApi {

    private static final Logger logger = LoggerFactory.getLogger(CommonPluginApiImpl.class);
    @Resource
    private ClassLoaderService classLoaderService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).init(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).refeshPicCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).refeshSmsCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).submit(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> validatePicCode(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).validatePicCode(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }

    @Override
    public HttpResult<Object> defineProcess(CommonPluginParam param) {
        try {
            return classLoaderService.getCommonPluginService(param).defineProcess(param);
        } catch (Throwable e) {
            return new HttpResult<Object>().failure(ErrorCode.SYS_ERROR);
        }
    }
}
