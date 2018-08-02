package com.datatrees.spider.bank.service.impl;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.dao.BankDAO;
import com.datatrees.rawdatacentral.dao.BankMailDAO;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.BankMail;
import com.datatrees.rawdatacentral.domain.model.example.BankExample;
import com.datatrees.rawdatacentral.domain.model.example.BankMailExample;
import com.datatrees.spider.bank.service.BankService;
import com.datatrees.spider.share.common.share.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BankServiceImpl implements BankService {

    private static final Logger       logger = LoggerFactory.getLogger(BankServiceImpl.class);

    @Resource
    private              RedisService redisService;

    @Resource
    private              BankDAO      bankDAO;

    @Resource
    private              BankMailDAO  bankMailDAO;

    @Override
    public Bank getByWebsiteName(String websiteName) {
        BankExample example = new BankExample();
        example.createCriteria().andWebsiteNameEqualTo(websiteName);
        List<Bank> list = bankDAO.selectByExample(example);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public Map<String, Integer> getMailBankMap() {
        String key = "rawdatacentral_mail_bank";
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
}
