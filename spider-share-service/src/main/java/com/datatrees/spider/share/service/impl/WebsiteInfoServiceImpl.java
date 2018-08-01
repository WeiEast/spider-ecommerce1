package com.datatrees.spider.share.service.impl;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.dao.WebsiteInfoDAO;
import com.datatrees.spider.share.domain.ErrorCode;
import com.datatrees.spider.share.domain.model.WebsiteInfo;
import com.datatrees.spider.share.domain.model.example.WebsiteInfoExample;
import com.datatrees.spider.share.service.WebsiteInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhangyanjia on 2018/3/20.
 */
@Service
public class WebsiteInfoServiceImpl implements WebsiteInfoService {

    private static final Logger         logger = LoggerFactory.getLogger(WebsiteInfoServiceImpl.class);

    @Resource
    private              WebsiteInfoDAO websiteInfoDAO;

    @Override
    public WebsiteInfo getByWebsiteName(String websiteName) {
        CheckUtils.checkNotBlank(websiteName, ErrorCode.EMPTY_WEBSITE_NAME);
        WebsiteInfoExample example = new WebsiteInfoExample();
        String env = TaskUtils.getSassEnv();
        example.createCriteria().andWebsiteNameEqualTo(websiteName).andEnvEqualTo(env);
        List<WebsiteInfo> list = websiteInfoDAO.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public void updateWebsiteConf(String websiteName, String searchConfig, String extractConfig) {
        WebsiteInfo websiteDb = getByWebsiteName(websiteName);
        CheckUtils.checkNotNull(websiteDb, "website not found websiteName=" + websiteName);
        WebsiteInfo websiteInfoUpdate = new WebsiteInfo();
        websiteInfoUpdate.setWebsiteId(websiteDb.getWebsiteId());
        websiteInfoUpdate.setSearchConfig(searchConfig);
        websiteInfoUpdate.setExtractorConfig(extractConfig);
        websiteInfoUpdate.setUpdatedAt(new Date());
        websiteInfoDAO.updateByPrimaryKeySelective(websiteInfoUpdate);
        logger.info("updateWebsiteInfo success websiteName={}", websiteName);
    }
}
