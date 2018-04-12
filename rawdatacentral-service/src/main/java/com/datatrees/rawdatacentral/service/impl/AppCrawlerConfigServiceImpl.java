package com.datatrees.rawdatacentral.service.impl;

import javax.annotation.Resource;
import java.util.List;

import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.dao.AppCrawlerConfigDao;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.model.AppCrawlerConfig;
import com.datatrees.rawdatacentral.domain.model.example.AppCrawlerConfigExample;
import com.datatrees.rawdatacentral.service.AppCrawlerConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * User: yand
 * Date: 2018/4/10
 */
@Service
public class AppCrawlerConfigServiceImpl implements AppCrawlerConfigService, InitializingBean {

    private static final Logger logger    = LoggerFactory.getLogger(AppCrawlerConfig.class);
    private static final String separator = "_";
    @Resource
    private AppCrawlerConfigDao appCrawlerConfigDao;
    @Resource
    private RedisService        redisService;

    @Override
    public void afterPropertiesSet() throws Exception {
        AppCrawlerConfigExample appCrawlerConfigExample = new AppCrawlerConfigExample();
        List<AppCrawlerConfig> list = appCrawlerConfigDao.selectByExample(appCrawlerConfigExample);
        if (list != null && list.size() > 0) {
            for (AppCrawlerConfig appCrawlerConfig : list) {
                String redisKey = appCrawlerConfig.getAppId() + separator + appCrawlerConfig.getProject();
                redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, String.valueOf(appCrawlerConfig.getCrawlerStatus()));
            }
        }
    }

    @Override
    public String getFromRedis(String appId, String project) {
        String redisKey = appId + separator + project;
        logger.info("redisKey : {}", redisKey);
        String result = redisService.getString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey);
        logger.info("result from redis : {}", result);
        if (StringUtils.isBlank(result)) {
            AppCrawlerConfigExample example = new AppCrawlerConfigExample();
            AppCrawlerConfigExample.Criteria criteria = example.createCriteria();
            criteria.andAppIdEqualTo(appId).andProjectEqualTo(project);
            List<AppCrawlerConfig> list = appCrawlerConfigDao.selectByExample(example);
            logger.info("list from db : {}", list);
            if (list != null && list.size() > 0) {
                result = String.valueOf(list.get(0).getCrawlerStatus());
                redisService.saveString(RedisKeyPrefixEnum.APP_CRAWLER_CONFIG, redisKey, result);
            }
        }
        return result;
    }

}
