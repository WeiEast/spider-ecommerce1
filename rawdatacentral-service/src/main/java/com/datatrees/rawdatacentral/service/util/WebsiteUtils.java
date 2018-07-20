package com.datatrees.rawdatacentral.service.util;

import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.GroupEnum;
import com.datatrees.rawdatacentral.domain.model.WebsiteInfoWithBLOBs;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.spider.operator.domain.model.WebsiteOperator;

public class WebsiteUtils {

    public static WebsiteConfig buildWebsiteConfig(WebsiteOperator operator) {
        CheckUtils.checkNotNull(operator, "operator is null");
        WebsiteConfig config = new WebsiteConfig();
        config.setWebsiteId(operator.getWebsiteId());
        config.setWebsiteName(operator.getWebsiteName());
        config.setWebsiteType("2");
        config.setIsenabled(true);
        config.setLoginTip(operator.getLoginTip());
        config.setVerifyTip(operator.getVerifyTip());
        config.setResetType(operator.getResetType());
        config.setSmsReceiver(operator.getSmsReceiver());
        config.setSmsTemplate(operator.getSmsTemplate());
        config.setResetTip(operator.getResetTip());
        config.setResetURL(operator.getResetUrl());
        config.setInitSetting(operator.getLoginConfig());
        config.setSearchConfig(operator.getSearchConfig());
        config.setExtractorConfig(operator.getExtractorConfig());
        config.setSimulate(operator.getSimulate());
        config.setWebsiteTitle(operator.getWebsiteTitle());
        config.setGroupCode(operator.getGroupCode());
        config.setGroupName(GroupEnum.getByGroupCode(operator.getGroupCode()).getGroupName());
        return config;
    }

    public static WebsiteConfig buildWebsiteConfig(WebsiteInfoWithBLOBs info) {
        CheckUtils.checkNotNull(info, "info is null");
        WebsiteConfig config = new WebsiteConfig();
        config.setWebsiteId(info.getWebsiteId());
        config.setWebsiteName(info.getWebsiteName());
        config.setWebsiteType(info.getWebsiteType().toString());
        config.setIsenabled(true);
        config.setLoginTip(info.getLoginTip());
        config.setVerifyTip(info.getVerifyTip());
        config.setResetType(info.getResetType());
        config.setSmsReceiver(info.getSmsReceiver());
        config.setSmsTemplate(info.getSmsTemplate());
        config.setResetTip(info.getResetTip());
        config.setResetURL(info.getResetUrl());
        config.setInitSetting(info.getLoginConfig());
        config.setSearchConfig(info.getSearchConfig());
        config.setExtractorConfig(info.getExtractorConfig());
        config.setWebsiteTitle(info.getWebsiteTitle());
        config.setGroupCode(info.getGroupCode());
        if (info.getGroupCode() != null && !("".equals(info.getGroupCode()))) {
            config.setGroupName(GroupEnum.getByGroupCode(info.getGroupCode()).getGroupName());
        }
        return config;
    }

}
