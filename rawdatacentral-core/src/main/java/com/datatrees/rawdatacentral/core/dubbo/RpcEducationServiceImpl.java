package com.datatrees.rawdatacentral.core.dubbo;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.CommonPluginService;
import com.datatrees.rawdatacentral.api.RpcEducationService;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2017/12/1.
 */
@Service
public class RpcEducationServiceImpl implements RpcEducationService {

    private static final Logger              logger = LoggerFactory.getLogger(RpcEducationServiceImpl.class);

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
