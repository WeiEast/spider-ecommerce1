package com.datatrees.rawdatacentral.service.dubbo.mail.sina;

import com.alibaba.fastjson.JSON;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.crawler.plugin.login.ErrorMessage;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.api.mail.sina.MailServiceApiForSina;
import com.datatrees.rawdatacentral.common.http.ProxyUtils;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.EncryptSinaUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.FormType;
import com.datatrees.rawdatacentral.domain.enums.*;
import com.datatrees.rawdatacentral.domain.model.Task;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zhangyanjia on 2018/1/26.
 */
@Service
public class MailServiceApiForSinaImpl implements MailServiceApiForSina {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceApiForSinaImpl.class);

    @Resource
    private CommonPluginApi commonPluginApi;

    @Override
    public HttpResult<Object> init(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        HttpResult<Object> result=commonPluginApi.init(param);
        return result;

    }

    @Override
    public HttpResult<Object> login(CommonPluginParam param) {
        if (param.getTaskId() == null || param.getUsername() == null || param.getPassword() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        param.setFormType(FormType.LOGIN);
        HttpResult<Object> result= commonPluginApi.submit(param);

        return result;
    }


    @Override
    public HttpResult<Object> refeshPicCode(CommonPluginParam param) {
        if (param.getTaskId() == null) {
            throw new RuntimeException(ErrorCode.PARAM_ERROR.getErrorMsg());
        }
        param.setWebsiteName(GroupEnum.MAIL_SINA_H5.getWebsiteName());
        return commonPluginApi.refeshPicCode(param);
    }
}
