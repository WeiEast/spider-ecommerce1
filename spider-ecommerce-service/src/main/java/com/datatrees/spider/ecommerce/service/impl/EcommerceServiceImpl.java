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

package com.datatrees.spider.ecommerce.service.impl;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.ecommerce.dao.EcommerceDAO;
import com.datatrees.spider.share.domain.model.Ecommerce;
import com.datatrees.spider.share.domain.model.example.EcommerceExample;
import com.datatrees.spider.ecommerce.service.EcommerceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by zhouxinghai on 2017/6/29.
 */
@Service
public class EcommerceServiceImpl implements EcommerceService {

    private static final Logger       logger = LoggerFactory.getLogger(EcommerceServiceImpl.class);

    @Resource
    private              RedisService redisService;

    @Resource
    private              EcommerceDAO ecommerceDAO;

    @Override
    public Ecommerce getByWebsiteId(Integer websiteId) {
        Ecommerce ecommerce = null;
        if (null != websiteId) {
            String key = "spider_ecommerce_websiteid_" + websiteId;
            ecommerce = redisService.getCache(key, new TypeReference<Ecommerce>() {});
            if (null == ecommerce) {
                EcommerceExample example = new EcommerceExample();
                example.createCriteria().andWebsiteidEqualTo(websiteId).andIsenabledEqualTo(true);
                List<Ecommerce> list = ecommerceDAO.selectByExample(example);
                if (!list.isEmpty()) {
                    ecommerce = list.get(0);
                    redisService.cache(key, ecommerce, 1, TimeUnit.DAYS);
                }
            }
        }
        return ecommerce;
    }
}
