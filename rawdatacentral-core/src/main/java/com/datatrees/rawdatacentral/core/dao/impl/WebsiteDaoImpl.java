/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao.impl;

import com.datatrees.rawdatacentral.core.dao.WebsiteDao;
import com.datatrees.rawdatacentral.domain.common.Website;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年6月10日 上午10:19:25
 */
@Component
public class WebsiteDaoImpl extends BaseDao implements WebsiteDao {

    private void setWebsiteConf(Website website) {
        if (website != null) {
            website.setWebsiteConf((WebsiteConf) sqlMapClientTemplate.queryForObject("Website.getWebsiteConfById", website.getId()));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see WebsiteDao#getWebsiteByName(java.lang.String)
     */
    @Override
    public Website getWebsiteByName(String websiteName) {
        Website website = (Website) sqlMapClientTemplate.queryForObject("Website.getWebsiteByName", websiteName);
        this.setWebsiteConf(website);
        return website;
    }

    /*
     * (non-Javadoc)
     * 
     * @see WebsiteDao#getWebsiteById(int)
     */
    @Override
    public Website getWebsiteById(int id) {
        Website website = (Website) sqlMapClientTemplate.queryForObject("Website.getWebsiteById", id);
        this.setWebsiteConf(website);
        return website;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * WebsiteDao#updateWebsiteConfig(com.datatrees.rawdatacentral.api.model
     * .Website)
     */
    @Override
    public int updateWebsiteConfig(Website website) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("websiteId", website.getId());
        parameter.put("searchConfigSource", website.getSearchConfigSource());
        parameter.put("extractConfigSource", website.getExtractorConfigSource());
        return sqlMapClientTemplate.update("Website.updateWebsiteConfig", parameter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see WebsiteDao#getWebsiteByName(java.lang.String, boolean)
     */
    @Override
    public Website getWebsiteNoConfByName(String websiteName) {
        Website website = (Website) sqlMapClientTemplate.queryForObject("Website.getWebsiteByName", websiteName);
        return website;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * WebsiteDao#insertWebsiteConfig(com.datatrees.rawdatacentral.core.
     * model.Website)
     */
    @Override
    public int insertWebsiteConfig(Website website) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("websiteId", website.getId());
        map.put("searchConfigSource", website.getSearchConfigSource());
        map.put("extractConfigSource", website.getExtractorConfigSource());
        return (int) sqlMapClientTemplate.insert("Website.insertWebsiteConfig", map);
    }

    /*
     * (non-Javadoc)
     * 
     * @see WebsiteDao#getWebsiteConfigCountByWebsiteId(int)
     */
    @Override
    public int countWebsiteConfigByWebsiteId(int websiteId) {
        return (int) sqlMapClientTemplate.queryForObject("Website.countWebsiteConfigByWebsiteId", websiteId);
    }

}
