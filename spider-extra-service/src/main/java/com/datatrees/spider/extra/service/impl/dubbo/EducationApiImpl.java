package com.datatrees.spider.extra.service.impl.dubbo;

import javax.annotation.Resource;

import com.datatrees.spider.extra.api.EducationApi;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2017/12/1.
 */
@Service
public class EducationApiImpl implements EducationApi {

    private static final Logger              logger = LoggerFactory.getLogger(EducationApiImpl.class);

    @Resource
    private              CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        return commonPluginService.init(param);
    }

    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        return commonPluginService.refeshPicCode(param);
    }

    @Override
    public HttpResult<Object> refeshSmsCode(CommonPluginParam param) {
        return commonPluginService.refeshSmsCode(param);
    }

    @Override
    public HttpResult<Object> submit(CommonPluginParam param) {
        return commonPluginService.submit(param);
    }

}
