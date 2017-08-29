package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.dao.OperatorGroupDAO;
import com.datatrees.rawdatacentral.dao.WebsiteConfigDAO;
import com.datatrees.rawdatacentral.dao.WebsiteOperatorDAO;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.model.OperatorGroup;
import com.datatrees.rawdatacentral.domain.model.WebsiteOperator;
import com.datatrees.rawdatacentral.domain.model.example.OperatorGroupExample;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteOperatorExample;
import com.datatrees.rawdatacentral.domain.vo.WebsiteConfig;
import com.datatrees.rawdatacentral.service.WebsiteOperatorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteOperatorServiceImpl implements WebsiteOperatorService {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteOperatorServiceImpl.class);
    @Resource
    private WebsiteOperatorDAO websiteOperatorDAO;
    @Resource
    private WebsiteConfigDAO   websiteConfigDAO;
    @Resource
    private OperatorGroupDAO   operatorGroupDAO;

    @Override
    public WebsiteOperator getByWebsiteName(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteOperatorExample example = new WebsiteOperatorExample();
        example.createCriteria().andWebsiteNameEqualTo(websiteName);
        List<WebsiteOperator> list = websiteOperatorDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public WebsiteOperator queryMaxWeightWebsite(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        return websiteOperatorDAO.queryMaxWeightWebsite(websiteName);
    }

    @Override
    public List<OperatorGroup> queryByGroupCode(String groupCode) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is blank");
        OperatorGroupExample example = new OperatorGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        return operatorGroupDAO.selectByExample(example);
    }

    @Override
    public void importWebsite(WebsiteOperator config) {
        CheckUtils.checkNotNull(config, "config is null");
        CheckUtils.checkNotBlank(config.getWebsiteName(), ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteConfig source = websiteConfigDAO.getWebsiteConfig(null, config.getWebsiteName());
        if (null == source) {
            throw new CommonException("websiteName config not found");
        }
        config.setWebsiteId(source.getWebsiteId());
        config.setWebsiteName(source.getWebsiteName());
        config.setSearchConfig(source.getSearchConfig());
        config.setExtractorConfig(source.getExtractorConfig());
        config.setLoginTip(source.getLoginTip());
        config.setVerifyTip(source.getVerifyTip());
        config.setResetType(source.getResetType());
        config.setResetTip(source.getResetTip());
        if (!StringUtils.equals("null", source.getSmsTemplate())) {
            config.setSmsTemplate(source.getSmsTemplate());
        }
        if (!StringUtils.equals("null", source.getResetURL())) {
            config.setResetUrl(source.getResetURL());
        }
        config.setSmsReceiver(source.getSmsReceiver());
        config.setSimulate(source.getSimulate());
        if (StringUtils.isBlank(config.getLoginConfig())) {
            config.setLoginConfig(source.getInitSetting().trim().replaceAll(" ", "").replaceAll("\\n", ""));
        }
        if (StringUtils.isBlank(config.getPluginClass())) {
            String pluginClass = PropertiesConfiguration.getInstance().get(RedisKeyPrefixEnum.PLUGIN_CLASS.getRedisKey(config.getWebsiteName()));
            config.setPluginClass(pluginClass);
        }
        websiteOperatorDAO.insertSelective(config);
    }

    @Override
    public List<OperatorGroup> configOperatorGroup(String groupCode, Map<String, Integer> config) {
        CheckUtils.checkNotBlank(groupCode, "groupCode is null");
        if (null == config || config.isEmpty()) {
            throw new CommonException("config is empty");
        }
        OperatorGroupExample example = new OperatorGroupExample();
        example.createCriteria().andGroupCodeEqualTo(groupCode);
        for (Map.Entry<String, Integer> entry : config.entrySet()) {
            OperatorGroup operatorGroup = new OperatorGroup();
            operatorGroup.setGroupCode(groupCode);
            operatorGroup.setWebsiteName(entry.getKey());
            operatorGroup.setWeight(entry.getValue());
            operatorGroup.setWebsiteTitle(getByWebsiteName(entry.getKey()).getWebsiteTitle());
            operatorGroupDAO.insertSelective(operatorGroup);
        }
        return queryByGroupCode(groupCode);
    }
}
