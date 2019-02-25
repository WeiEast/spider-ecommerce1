package com.datatrees.spider.ecommerce.api;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 淘宝账号密码登录接口
 * @author guimeichao
 * @date 2019/2/25
 */
public interface EconomicApiForTaoBaoH5 {

    /**
     * 必填参数: taskId
     * @param param
     * @return
     */
    HttpResult<Object> login(CommonPluginParam param);
}
