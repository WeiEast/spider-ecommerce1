package com.datatrees.spider.share.service.plugin;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

public interface QRPlugin {

    /**
     * 刷新登陆二维码
     * @param param
     * @return
     */
    HttpResult<Object> refeshQRCode(CommonPluginParam param);

    /**
     * 查询二维码状态
     * @param param
     * @return
     */
    HttpResult<Object> queryQRStatus(CommonPluginParam param);

}
