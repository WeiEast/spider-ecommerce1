package com.datatrees.rawdatacentral.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.model.BankMail;
import com.datatrees.rawdatacentral.domain.model.example.BankMailExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.dao.BankDAO;
import com.datatrees.rawdatacentral.dao.BankMailDAO;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.example.BankExample;
import com.datatrees.rawdatacentral.service.BankService;

@Service
public class BankServiceImpl implements BankService {

    private static final Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);

    @Resource
    private RedisService        redisService;

    @Resource
    private BankDAO             bankDAO;

    @Resource
    private BankMailDAO         bankMailDAO;

    @Override
    public Bank getByBankIdFromCache(Integer bankId) {
        String key = "rawdatacentral_bank_" + bankId;
        Bank bank = redisService.getCache(key, new TypeReference<Bank>() {
        });
        if (null == bank) {
            bank = getEnabledByBankId(bankId);
            if (null != bank) {
                redisService.cache(key, bank, 1, TimeUnit.DAYS);
            }
        }
        return bank;
    }

    @Override
    public Bank getByWebsiteIdFromCache(Integer websiteId) {
        String key = "rawdatacentral_bank_website_id" + websiteId;
        Bank bank = redisService.getCache(key, new TypeReference<Bank>() {
        });
        if (null == bank) {
            bank = getEnabledByWebsiteId(websiteId);
            if (null != bank) {
                redisService.cache(key, bank, 1, TimeUnit.DAYS);
            }
        }
        return bank;
    }

    @Override
    public Bank getEnabledByBankId(Integer bankId) {
        if (null != bankId) {
            Bank bank = bankDAO.selectByPrimaryKey(bankId);
            if (null != bank && !bank.getIsenabled()) {
                logger.warn("bank is disabled bankId={}", bank.getBankId());
                return null;
            }
            return bank;
        }
        return null;
    }

    @Override
    public Bank getEnabledByWebsiteId(Integer websiteId) {
        if (null != websiteId) {
            BankExample example = new BankExample();
            BankExample.Criteria criteria = example.createCriteria();
            criteria.andWebsiteidEqualTo(websiteId).andIsenabledEqualTo(true);
            List<Bank> list = bankDAO.selectByExample(example);
            if (!list.isEmpty()) {
                Bank bank = list.get(0);
                if (!bank.getIsenabled()) {
                    logger.warn("bank is disabled websiteId={},bankId={}", websiteId, bank.getBankId());
                    return null;
                }
                return bank;
            }
        }
        return null;
    }

    @Override
    public Map<String, Integer> getMailBankMap() {
        String key = "rawdatacentral_mail_bank";
        Map<String, Integer> map = redisService.getCache(key, new TypeReference<Map<String, Integer>>() {
        });
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
}
