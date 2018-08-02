package com.datatrees.spider.bank.service;

import java.util.Map;

import com.datatrees.spider.bank.domain.model.Bank;

/**
 * 银行配置信息
 * Created by zhouxinghai on 2017/6/26.
 */
public interface BankService {

    Bank getByWebsiteName(String websiteName);

    /**
     * key:mail
     * value:bankId
     * @return
     */
    Map<String, Integer> getMailBankMap();

}
