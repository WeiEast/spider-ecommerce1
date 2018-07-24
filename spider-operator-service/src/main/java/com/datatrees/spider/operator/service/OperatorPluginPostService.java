package com.datatrees.spider.operator.service;

import java.util.Map;

import com.datatrees.spider.operator.domain.model.OperatorParam;
import com.datatrees.spider.share.domain.HttpResult;

/**
 * 运营商登陆登陆后处理接口
 * Created by zhouxinghai on 2017/7/13.
 */
public interface OperatorPluginPostService extends OperatorPluginService {

    /**
     * 登陆后处理接口
     * @return
     */
    HttpResult<Map<String, Object>> loginPost(OperatorParam param);

}
