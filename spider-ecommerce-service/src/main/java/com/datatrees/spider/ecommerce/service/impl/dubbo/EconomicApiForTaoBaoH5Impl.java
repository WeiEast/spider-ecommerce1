package com.datatrees.spider.ecommerce.service.impl.dubbo;

import javax.annotation.Resource;

import com.datatrees.spider.ecommerce.api.EconomicApiForTaoBaoH5;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.*;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.CommonPluginService;
import org.springframework.stereotype.Service;

/**
 * @author guimeichao
 * @date 2019/2/25
 */

@Service
public class EconomicApiForTaoBaoH5Impl implements EconomicApiForTaoBaoH5 {

    @Resource
    private CommonPluginService commonPluginService;

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        if (param.getTaskId() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.PASSWORD_TAOBAO_COM_H5.getWebsiteName());
        TaskUtils.addTaskShare(param.getTaskId(), AttributeKey.WEBSITE_NAME, param.getWebsiteName());

        param.setFormType(FormType.LOGIN);
        HttpResult<Object> result = commonPluginService.submit(param);
        return result;
    }
}
