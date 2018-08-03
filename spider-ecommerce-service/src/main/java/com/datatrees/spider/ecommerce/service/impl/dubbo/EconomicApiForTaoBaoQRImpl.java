package com.datatrees.rawdatacentral.service.dubbo.economic.taobao;

import javax.annotation.Resource;

import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.rawdatacentral.service.dubbo.EconomicApiForTaoBaoQR;
import com.datatrees.spider.share.domain.GroupEnum;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import org.springframework.stereotype.Service;

/**
 * Created by guimeichao on 18/1/11.
 */
@Service
public class EconomicApiForTaoBaoQRImpl implements EconomicApiForTaoBaoQR {

    @Resource
    private CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> refeshQRCode(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.TAOBAO_COM_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginService.refeshQRCode(param);
    }

    @Override
    public HttpResult<Object> queryQRStatus(CommonPluginParam param) {
        param.setWebsiteName(GroupEnum.TAOBAO_COM_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        param.setAutoSendLoginSuccessMsg(false);
        return commonPluginService.queryQRStatus(param);
    }
}
