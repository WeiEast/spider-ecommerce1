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

package com.datatrees.spider.bank.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.bank.dao.BankDAO;
import com.datatrees.spider.bank.dao.BankMailDAO;
import com.datatrees.spider.bank.domain.model.Bank;
import com.datatrees.spider.bank.domain.model.BankMail;
import com.datatrees.spider.bank.domain.model.example.BankExample;
import com.datatrees.spider.bank.domain.model.example.BankMailExample;
import com.datatrees.spider.bank.service.BankService;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.service.WebsiteConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl implements BankService {

    private static final Logger               logger = LoggerFactory.getLogger(BankServiceImpl.class);

    @Resource
    private              RedisService         redisService;

    @Resource
    private              BankDAO              bankDAO;

    @Resource
    private              BankMailDAO          bankMailDAO;

    @Resource
    private              WebsiteConfigService websiteConfigService;

    @Override
    public Bank getByWebsiteName(String websiteName) {
        BankExample example = new BankExample();
        example.createCriteria().andWebsiteNameEqualTo(websiteName);
        List<Bank> list = bankDAO.selectByExample(example);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, Integer> getMailBankMap() {
        String key = "spider_mail_bank";
        Map<String, Integer> map = redisService.getCache(key, new TypeReference<Map<String, Integer>>() {});
        if (null == map || map.isEmpty()) {
            List<BankMail> list = bankMailDAO.selectByExample(new BankMailExample());
            map = new HashMap<>();
            for (BankMail bankMail : list) {
                map.put(bankMail.getBankEmailAddr().toLowerCase(), bankMail.getBankId());
            }
            redisService.cache(key, map, 1, TimeUnit.DAYS);
        }
        return map;
    }

    @PostConstruct
    public void init() {
        List<Bank> list = bankDAO.selectByExample(new BankExample());
        Map<Integer, String> map = new HashMap<>();
        list.forEach(m -> {
            map.put(m.getBankId(), m.getWebsiteName());
        });
        websiteConfigService.initBankCache(map);
    }
}
