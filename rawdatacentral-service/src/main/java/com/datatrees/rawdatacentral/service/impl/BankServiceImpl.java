package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.rawdatacentral.dao.BankDAO;
import com.datatrees.rawdatacentral.dao.BankMailDAO;
import com.datatrees.rawdatacentral.domain.model.Bank;
import com.datatrees.rawdatacentral.domain.model.BankMail;
import com.datatrees.rawdatacentral.domain.model.example.BankExample;
import com.datatrees.rawdatacentral.domain.model.example.BankMailExample;
import com.datatrees.rawdatacentral.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Service
public class BankServiceImpl implements BankService {

    private static final Logger             LOGGER       = LoggerFactory.getLogger(BankServiceImpl.class);

    @Resource
    private BankDAO                         bankDAO;

    @Resource
    private BankMailDAO                     bankMailDAO;

    /**
     * 缓存所有的bank配置信息:bankId维度
     */
    private static final Map<Integer, Bank> bankIdMap    = new HashMap<>();

    /**
     *
     * 缓存所有的bank配置信息:邮件维度
     */
    private static final Map<Integer, Bank> mailMap      = new HashMap<>();

    /**
     * mail维度
     * 缓存所有的bank配置信息:邮件信息
     */
    private static final Map<Integer, Bank> websiteIdMap = new HashMap<>();

    @Override
    public Map<Integer, Bank> getCachedBankMap() {
        return null;
    }

    @Override
    public Map<String, Bank> getBankEmailMap() {
        return null;
    }

    @Override
    public Bank getBank(String mailAddress) {
        return null;
    }

    @Override
    public Bank getBankByWebsiteId(int websiteId) {
        return null;
    }

    @Override
    public List<Bank> queryAllBank() {
        return bankDAO.selectByExample(new BankExample());
    }

    @Override
    public List<BankMail> queryAllBankMail() {
        return bankMailDAO.selectByExample(new BankMailExample());
    }

}
