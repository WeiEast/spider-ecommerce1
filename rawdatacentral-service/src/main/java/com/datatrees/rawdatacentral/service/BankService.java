package com.datatrees.rawdatacentral.service;

import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.BankMail;

import java.util.List;
import java.util.Map;

/**
 * 银行配置信息
 * Created by zhouxinghai on 2017/6/26.
 */
public interface BankService {

    /**
     * 查询所有银行信息并缓存
     * key:bankId
     * value:bank
     * @return
     */
    public Map<Integer, Bank> getCachedBankMap();

    /**
     *
     * @return
     */
    public Map<String, Bank> getBankEmailMap();

    /**
     * 根据邮箱获取银行信息
     * @param mailAddress 邮箱
     * @return
     */
    public Bank getBank(String mailAddress);

    /**
     * 根据websiteId获取银行信息
     * @param websiteId
     * @return
     */
    public Bank getBankByWebsiteId(int websiteId);

    /**
     * 获取所有的银行信息
     * @return
     */
    public List<Bank> queryAllBank();

    /**
     * 获取所有的银行邮箱信息
     * @return
     */
    public List<BankMail> queryAllBankMail();
}
