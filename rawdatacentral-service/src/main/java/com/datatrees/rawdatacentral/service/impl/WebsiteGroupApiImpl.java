package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.datatrees.spider.operator.api.WebsiteGroupApi;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import com.datatrees.spider.operator.domain.model.WebsiteGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteGroupApiImpl implements WebsiteGroupApi {

    private static final Logger              logger = LoggerFactory.getLogger(WebsiteGroupApiImpl.class);

    @Resource
    private              WebsiteGroupService websiteGroupService;

    @Override
    public Integer queryEnableCount(String groupCode) {
        return websiteGroupService.queryEnableCount(groupCode);
    }

    @Override
    public List<WebsiteGroup> queryEnable(String groupCode) {
        return websiteGroupService.queryEnable(groupCode);
    }

    @Override
    public List<WebsiteGroup> queryDisable(String groupCode) {
        return websiteGroupService.queryDisable(groupCode);
    }

    @Override
    public int updateEnable(String websiteName, Boolean enable) {
        websiteGroupService.updateEnable(websiteName, enable);
        return 1;
    }

    @Override
    public List<String> getWebsiteNameList(String enable, String operatorType, String groupCode) {
        return websiteGroupService.getWebsiteNameList(enable, operatorType, groupCode);
    }

    @Override
    public List<WebsiteGroup> queryByGroupCode(String groupCode) {
        return websiteGroupService.queryByGroupCode(groupCode);
    }

    @Override
    public List<WebsiteGroup> configGroup(String groupCode, Map<String, Integer> config) {
        return websiteGroupService.configGroup(groupCode, config);
    }
}
