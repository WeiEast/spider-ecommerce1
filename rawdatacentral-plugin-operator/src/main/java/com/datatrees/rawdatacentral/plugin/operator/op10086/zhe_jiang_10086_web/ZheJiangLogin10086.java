package com.datatrees.rawdatacentral.plugin.operator.op10086.zhe_jiang_10086_web;

import com.datatrees.crawler.plugin.operator.OperatorLoginPlugin;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

import java.util.Map;

/**
 * 浙江10086登陆
 * Created by zhouxinghai on 2017/7/13.
 */
public class ZheJiangLogin10086 implements OperatorLoginPlugin {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ZheJiangLogin10086.class);

    @Override
    public HttpResult<String> refeshPicCode(Long taskId, String websiteName, OperatorParam param) {
        return null;
    }

    @Override
    public HttpResult<Boolean> refeshSmsCode(Long taskId, String websiteName, OperatorParam param) {
        return null;
    }

    @Override
    public HttpResult<Map<String, Object>> login(Long taskId, String websiteName, OperatorParam param) {
        return null;
    }
}
