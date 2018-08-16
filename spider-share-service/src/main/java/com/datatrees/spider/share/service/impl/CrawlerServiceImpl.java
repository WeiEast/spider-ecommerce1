/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.share.service.impl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.spider.share.api.CrawlerService;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.RedisKeyPrefixEnum;
import com.datatrees.spider.share.domain.directive.DirectiveRedisCode;
import com.datatrees.spider.share.domain.directive.DirectiveResult;
import com.datatrees.spider.share.domain.directive.DirectiveType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.domain.model.WebsiteConf;
import com.datatrees.spider.share.service.WebsiteHolderService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2016年11月5日 上午11:33:19
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static final Logger               logger = LoggerFactory.getLogger(CrawlerServiceImpl.class);

    @Resource
    private              WebsiteHolderService websiteHolderService;

    @Resource
    private              RedisService         redisService;

    @Override
    public WebsiteConf getWebsiteConf(String websiteName) {
        logger.info("getWebsiteConf start websiteName={}", websiteName);
        Map<String, String> map = redisService.getCache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP, new TypeReference<Map<String, String>>() {});
        String newWebsiteName;
        if (null == map || !map.containsKey(websiteName)) {
            String property = PropertiesConfiguration.getInstance().get(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP.getRedisKey());
            map = JSON.parseObject(property, Map.class);
            redisService.cache(RedisKeyPrefixEnum.WEBSITENAME_TRANSFORM_MAP, map);
        }
        newWebsiteName = map.get(websiteName);
        if (StringUtils.isBlank(newWebsiteName)) {
            logger.warn("no this websiteName in properties, websiteName is {}", websiteName);
            return null;
        }
        WebsiteConf conf = websiteHolderService.getWebsiteConf(newWebsiteName);
        if (null != conf) {
            //中文
            conf.setName(websiteName);
        }
        return conf;
    }

    @Override
    public List<WebsiteConf> getWebsiteConf(List<String> websiteNameList) {
        List<WebsiteConf> confList = new ArrayList<>();
        for (String websiteName : websiteNameList) {
            WebsiteConf websiteConf = getWebsiteConf(websiteName);
            if (null != websiteConf) {
                confList.add(websiteConf);
            }
        }
        return confList;
    }

    @Override
    public HttpResult<String> login(long taskId, String username, String password, String code, String randomPassword, Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        if (taskId <= 0 || StringUtils.isBlank(username)) {
            logger.warn("fetchLoginCode invalid param taskId={},username={}", taskId, username);
            return result.failure("invalid params taskId or username");
        }
        TaskUtils.addTaskShare(taskId, AttributeKey.MOBILE, username);
        TaskUtils.addTaskShare(taskId, AttributeKey.USERNAME, username);
        if (StringUtils.isBlank(password)) {
            logger.warn("fetchLoginCode  empty password, taskId={},username={}", taskId, username);
            return result.failure("invalid params,empty password");
        }
        long timeout = 30;
        String directiveId = null;
        try {
            if (null == extra) {
                extra = new HashMap<>();
            }
            DirectiveResult<Map<String, String>> sendDirective = new DirectiveResult<>(DirectiveType.PLUGIN_LOGIN, taskId);
            extra.put(AttributeKey.USERNAME, username);
            extra.put(AttributeKey.PASSWORD, password);
            extra.put(AttributeKey.CODE, code);
            extra.put(AttributeKey.RANDOM_PASSWORD, randomPassword);

            //保存交互指令到redis
            sendDirective.fill(DirectiveRedisCode.START_LOGIN, extra);

            //相同命令枷锁,加锁成功:发送指令,清除结果key,进入等待;加锁失败:进入等待结果
            directiveId = redisService.saveDirectiveResult(sendDirective);

            DirectiveResult<String> receiveResult = redisService.getDirectiveResult(directiveId, timeout, TimeUnit.SECONDS);
            if (null == receiveResult) {
                logger.warn("login get result timeout taskId={},directiveId={},timeout={},timeUnit={},username={}", taskId, directiveId, timeout,
                        TimeUnit.SECONDS, username);
                return result.failure("登陆超时");
            }
            if (StringUtils.equals(DirectiveRedisCode.SERVER_FAIL, receiveResult.getStatus())) {
                logger.error("login failtaskId={},directiveId={},status={},errorMsg={},username={}", taskId, directiveId, receiveResult.getErrorMsg(),
                        username);
                return result.failure(receiveResult.getData());
            }
            logger.info("login success taskId={},directiveId={},username={}", taskId, directiveId, username);
            return result.success("登陆成功!");
        } catch (Exception e) {
            logger.error("login error taskId={},directiveId={},username={}", taskId, directiveId, username, e);
            return result.failure("登陆失败!");
        }

    }

    @Override
    public HttpResult<String> verifyQr(String directiveId, long taskId, Map<String, String> extra) {
        HttpResult<String> result = new HttpResult<>();
        try {
            if (taskId <= 0 || StringUtils.isBlank(directiveId)) {
                logger.warn("verifyQr invalid param taskId={},directiveId={}", taskId, directiveId);
                return result.success(DirectiveRedisCode.FAILED);
            }
            DirectiveResult<String> directiveResult = redisService.getDirectiveResult(directiveId, 2, TimeUnit.SECONDS);
            if (null == directiveResult) {
                logger.warn("verifyQr timeout taskId={},directiveId={}", taskId, directiveId);
                return result.success(DirectiveRedisCode.FAILED);
            }
            TimeUnit.MILLISECONDS.sleep(500);//不能让前端一直轮询
            logger.info("verifyQr result taskId={},directiveId={},qrStatus={}", taskId, directiveId, directiveResult.getStatus());
            return result.success(directiveResult.getStatus());
        } catch (Exception e) {
            logger.error("verifyQr error taskId={},directiveId={}", taskId, directiveId);
            return result.success(DirectiveRedisCode.FAILED);
        }
    }

}
