package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.api.WebsiteGroupServiceApi;
import com.datatrees.rawdatacentral.dao.WebsiteGroupDAO;
import com.datatrees.rawdatacentral.domain.model.example.WebsiteGroupExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebsiteGroupServiceApiImpl implements WebsiteGroupServiceApi {

    private static final Logger logger = LoggerFactory.getLogger(WebsiteGroupServiceApiImpl.class);
    @Resource
    private WebsiteGroupDAO websiteGroupDAO;

    @Override
    public Integer enableCount(String groupCode) {
        WebsiteGroupExample example = new WebsiteGroupExample();
        WebsiteGroupExample.Criteria criteria = example.createCriteria();
        criteria.andGroupCodeEqualTo(groupCode).andEnableEqualTo(true);
        return websiteGroupDAO.countByExample(example);
    }
}
