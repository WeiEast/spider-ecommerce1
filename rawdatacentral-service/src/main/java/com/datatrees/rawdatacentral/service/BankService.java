package com.datatrees.rawdatacentral.service;

import java.util.Map;

import com.datatrees.rawdatacentral.domain.model.Bank;

/**
 * 银行配置信息
 * Created by zhouxinghai on 2017/6/26.
 */
public interface BankService {

    /**
     * 从缓存获取bank
     * @param bankId
     * @return
     */
    Bank getByBankIdFromCache(Integer bankId);

    /**
     * 从缓存获取bank
     * @param
     * @return
     */
//    Bank getByWebsiteIdFromCache(Integer websiteId);

    Bank getByWebsiteName(String websiteName);

    /**
     * 获取有效的bank
     * @param bankId
     * @return
     */
    Bank getEnabledByBankId(Integer bankId);

    /**
     * 获取bank
     * @param websiteId
     * @return
     */
//    Bank getEnabledByWebsiteId(Integer websiteId);

    /**
     * key:mail
     * value:bankId
     * @return
     */
    Map<String, Integer> getMailBankMap();

}
