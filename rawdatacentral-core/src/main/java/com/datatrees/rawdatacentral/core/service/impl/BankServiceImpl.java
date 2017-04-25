/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.datatrees.rawdatacentral.core.common.Constants;
import org.springframework.stereotype.Service;

import com.datatrees.common.util.CacheUtil;
import com.datatrees.rawdatacentral.core.dao.BankDao;
import com.datatrees.rawdatacentral.core.model.Bank;
import com.datatrees.rawdatacentral.core.service.BankService;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午3:29:51
 */
@Service
public class BankServiceImpl implements BankService {

    @Resource
    private BankDao bankDao;


    /*
     * (non-Javadoc)
     * 
     * @see BankService#getCachedBankMap()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<Integer, Bank> getCachedBankMap() {
        Map<Integer, Bank> bankMap = (Map<Integer, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_MAP_KEY);
        if (bankMap == null) {
            List<Bank> bankList = bankDao.getAllBank();
            bankMap = new HashMap<Integer, Bank>();
            Map<String, Bank> bankEmailMap = new HashMap<String, Bank>();
            Map<Integer, Bank> bankWebisteMap = new HashMap<Integer, Bank>();
            for (Bank bank : bankList) {
                bankMap.put(bank.getId(), bank);
                bankEmailMap.put(bank.getBankEmailAddr().toLowerCase(), bank);
                bankWebisteMap.put(bank.getWebsiteId(), bank);
            }
            CacheUtil.INSTANCE.insertObject(Constants.BANK_MAP_KEY, bankMap);
            CacheUtil.INSTANCE.insertObject(Constants.BANK_EMAIL_MAP_KEY, bankEmailMap);
            CacheUtil.INSTANCE.insertObject(Constants.BANK_WEBSIYE_MAP_KEY, bankWebisteMap);
        }
        return bankMap;
    }


    /*
     * (non-Javadoc)
     * 
     * @see BankService#getBank(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Bank getBank(String mailAddress) {
        Bank bank = null;
        Map<String, Bank> bankMap = (Map<String, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_EMAIL_MAP_KEY);
        if (bankMap == null) {
            this.getCachedBankMap();// init bankEmailMap
            bankMap = (Map<String, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_EMAIL_MAP_KEY);
        }
        if (bankMap != null) {
            bank = bankMap.get(mailAddress);
        }
        return bank;
    }


    /*
     * (non-Javadoc)
     * 
     * @see BankService#getbankEmailMap()
     */
    @Override
    public Map<String, Bank> getBankEmailMap() {
        Map<String, Bank> bankMap = (Map<String, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_EMAIL_MAP_KEY);
        if (bankMap == null) {
            this.getCachedBankMap();// init bankEmailMap
            bankMap = (Map<String, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_EMAIL_MAP_KEY);
        }
        return bankMap;
    }


    /*
     * (non-Javadoc)
     * 
     * @see BankService#getBankByWebsiteId(int)
     */
    @Override
    public Bank getBankByWebsiteId(int websiteId) {
        Map<Integer, Bank> bankWebsiteMap = (Map<Integer, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_WEBSIYE_MAP_KEY);
        if (bankWebsiteMap == null) {
            this.getCachedBankMap();// init bankEmailMap
            bankWebsiteMap = (Map<Integer, Bank>) CacheUtil.INSTANCE.getObject(Constants.BANK_WEBSIYE_MAP_KEY);
        }
        return bankWebsiteMap.get(websiteId);
    }
}
