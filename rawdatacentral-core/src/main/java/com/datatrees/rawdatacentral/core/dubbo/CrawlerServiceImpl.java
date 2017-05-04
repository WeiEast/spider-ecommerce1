/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * <p>
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.rawdatacentral.core.dubbo;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.util.CacheUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.zookeeper.ZooKeeperClient;
import com.datatrees.common.zookeeper.watcher.AbstractLockerWatcher;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.core.common.ActorLockEventWatcher;
import com.datatrees.rawdatacentral.core.dao.RedisDao;
import com.datatrees.rawdatacentral.core.service.TaskService;
import com.datatrees.rawdatacentral.core.service.WebsiteService;
import com.datatrees.rawdatacentral.domain.common.Website;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月5日 上午11:33:19
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerServiceImpl.class);
    @Resource
    private WebsiteService      websiteService;

    @Resource
    private RedisDao            redisDao;

    @Resource
    private ZooKeeperClient     zooKeeperClient;

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.rawdata.api.CrawlerService#getWebsiteConf(java.lang.String)
     */
    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        Map<String, String> map = (Map<String, String>) CacheUtil.INSTANCE.getObject("WEBSITENAME_TRANSFORM_MAP");
        String newWebsiteName;
        if (map != null) {
            newWebsiteName = map.get(websiteName);
        } else {
            map = (Map<String, String>) GsonUtils.fromJson(
                PropertiesConfiguration.getInstance().get("WEBSITENAME_TRANSFORM_MAP"),
                new TypeToken<HashMap<String, String>>() {
                }.getType());
            CacheUtil.INSTANCE.insertObject("WEBSITENAME_TRANSFORM_MAP", map);
            newWebsiteName = map.get(websiteName);
        }
        if (StringUtils.isNotBlank(newWebsiteName)) {
            Website website = websiteService.getCachedWebsiteByName(newWebsiteName);
            WebsiteConf conf = null;
            if (website != null && (conf = website.getWebsiteConf()) != null) {
                conf.setName(websiteName);
                return conf;
            } else {
                logger.warn("no active website named {}", newWebsiteName);
            }
        } else {
            logger.warn("no this websiteName in properties, websiteName is {}", websiteName);
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.datatrees.rawdata.api.CrawlerService#getWebsiteConf(java.util.List)
     */
    @Override
    public List<WebsiteConf> getWebsiteConf(List<String> websiteNameList) {
        List<WebsiteConf> confList = new ArrayList<>();
        for (String websiteName : websiteNameList) {
            confList.add(getWebsiteConf(websiteName));
        }
        return confList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.rawdata.api.CrawlerService#updateWebsiteConfig(com.datatrees.rawdata.api.model.
     * Website)
     */
    @Override
    public boolean updateWebsiteConfig(String websiteName, String searchConfigSource, String extractConfigSource) {
        logger.info("crawlerService start update webiste:" + websiteName);
        try {
            synchronized (websiteName) {
                Website website = websiteService.getWebsiteNoConfByName(websiteName);
                if (website != null) {
                    website.setSearchConfigSource(searchConfigSource);
                    website.setExtractorConfigSource(extractConfigSource);
                    if (websiteService.countWebsiteConfigByWebsiteId(website.getId()) > 0) {
                        websiteService.updateWebsiteConfig(website);
                        logger.info("update websiteConfig success,webiste:" + websiteName);
                    } else {
                        websiteService.insertWebsiteConfig(website);
                        logger.info("insert websiteConfig success,webiste:" + websiteName);
                    }
                } else {
                    logger.warn("can't find website by websiteName:" + websiteName);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public HttpResult<String> importStatus(long taskId, int type, String attrJson) {
        HttpResult<String> result = new HttpResult<String>();
        String key = "verify_result_" + taskId;
        if (redisDao.saveListString(key, Arrays.asList(GsonUtils.toJson(attrJson)))) {
            return result.success();
        }
        return result.failure();
    }

    @SuppressWarnings("unchecked")
    @Override
    public HttpResult<String> fetchStatus(long taskId, int type, String attrJson) {
        HttpResult<String> result = new HttpResult<String>();
        String key = "verify_result_" + taskId;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dubboType", type);
        if (type == 0) {
            map.put("status", "REFRESH_LOGIN_RANDOMPASSWORD");
        } else if (type == 1) {
            map.put("status", "REFRESH_LOGIN_CODE");
        } else if (type == 2) {
            map.put("status", "REFRESH_LOGIN_QR_CODE");
        }
        String getKey = "plugin_remark_" + type + "_" + taskId;
        redisDao.deleteKey(getKey);
        if (redisDao.saveListString(key, Arrays.asList(GsonUtils.toJson(map)))) {
            try {
                long startTime = System.currentTimeMillis();
                while (!isTimeOut(startTime, "alipay.com")) {

                    String pullResult = redisDao.pullResult(getKey);

                    Map<String, Object> resultMap = (Map<String, Object>) GsonUtils.fromJson(pullResult,
                        new TypeToken<HashMap<String, Object>>() {
                        }.getType());
                    String remark;
                    if (resultMap != null) {
                        remark = StringUtils.defaultIfBlank((String) resultMap.get("remark"), "");
                        result = result.success();
                        result.setData(remark);
                        return result;
                    }
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }

        return result.failure();

    }

    @SuppressWarnings("unchecked")
    @Override
    public HttpResult<String> verifyQr(long taskId, String attrJson) {
        HttpResult<String> result = new HttpResult<String>();
        String key = "verify_result_" + taskId;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", "VERIFY_QR_CODE");
        String getKey = "plugin_remark_" + taskId;
        redisDao.deleteKey(getKey);
        if (redisDao.saveListString(key, Arrays.asList(GsonUtils.toJson(map)))) {
            try {
                long startTime = System.currentTimeMillis();
                while (!isTimeOut(startTime, "alipay.com")) {

                    String pullResult = redisDao.pullResult(getKey);

                    Map<String, Object> resultMap = (Map<String, Object>) GsonUtils.fromJson(pullResult,
                        new TypeToken<HashMap<String, Object>>() {
                        }.getType());
                    String qrStatus;
                    if (resultMap != null) {
                        qrStatus = StringUtils.defaultIfBlank((String) resultMap.get(PluginConstants.FIELD), "");
                        result = result.success();
                        result.setData(qrStatus);
                        return result;
                    }
                    Thread.sleep(3000);
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }

        result = result.failure();
        return result;
    }

    private boolean isTimeOut(long startTime, String websiteName) throws Exception {
        long now = System.currentTimeMillis();
        int maxInterval = PropertiesConfiguration.getInstance().getInt(websiteName + ".default.max.waittime",
            2 * 60 * 1000);
        if (now <= startTime + maxInterval) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.datatrees.rawdatacentral.api.CrawlerService#cancel(long, java.lang.String)
     */
    @Override
    public HttpResult<Boolean> cancel(long taskId, String attrJson) {
        ActorLockEventWatcher watcher = new ActorLockEventWatcher("CollectorActor", taskId + "", null,
            zooKeeperClient);
        HttpResult<Boolean> result = new HttpResult<Boolean>();
        result.setData(false);
        if(watcher.cancel()){
            result.setData(true);
            result.success();
        }
        return result.failure();
    }

}
