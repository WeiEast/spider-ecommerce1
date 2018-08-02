package com.datatrees.rawdatacentral.service.dubbo;

import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.http.HttpResult;

/**
 * 淘宝二维码登录接口
 * Created by guimeichao on 18/1/11.
 */
public interface EconomicApiForTaoBaoQR {

    /**
     * 必填参数: taskId
     * @param param
     * @return
     */
    HttpResult<Object> refeshQRCode(CommonPluginParam param);

    /**
     * 必填参数: taskId
     * @param param
     * @return
     */
    HttpResult<Object> queryQRStatus(CommonPluginParam param);

}
