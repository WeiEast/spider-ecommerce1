package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.rawdatacentral.api.WebsiteGroupServiceApi;
import com.datatrees.rawdatacentral.dao.WebsiteGroupDAO;
import com.datatrees.rawdatacentral.domain.model.WebsiteGroup;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteGroupExample;
import com.datatrees.rawdatacentral.service.WebsiteGroupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteGroupServiceApiImpl implements WebsiteGroupServiceApi {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteGroupServiceApiImpl.class);
    @Resource
    private WebsiteGroupDAO     websiteGroupDAO;
    @Resource
    private WebsiteGroupService websiteGroupService;

    @Override
    public Integer enableCount(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true);
        return websiteGroupDAO.countByExample(example);
    }

    @Override
    public List<WebsiteGroup> queryEnable(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true);
        example.setOrderByClause("weight desc");
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public List<WebsiteGroup> queryDisable(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(false);
        example.setOrderByClause("weight desc");
        return websiteGroupDAO.selectByExample(example);
    }

    @Override
    public WebsiteGroup queryWebsiteGroupByWebSiteName(String webSiteName) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andWebsiteNameEqualTo(webSiteName);
        example.setOrderByClause("weight desc");
        List<WebsiteGroup> list = websiteGroupDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    public int updateEnable(String websiteName, Boolean enable) {
        websiteGroupService.updateEnable(websiteName, enable);
        return 1;
    }
}
