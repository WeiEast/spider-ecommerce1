package com.datatrees.rawdatacentral.service.impl;

import com.datatrees.rawdatacentral.dao.EcommerceDAO;
import com.datatrees.rawdatacentral.domain.model.Ecommerce;
import com.datatrees.rawdatacentral.domain.model.example.EcommerceExample;
import com.datatrees.rawdatacentral.service.EcommerceService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxinghai on 2017/6/29.
 */
@Service
public class EcommerceServiceImpl implements EcommerceService {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceServiceImpl.class);

    @Resource
    private RedisService        redisService;

    @Resource
    private EcommerceDAO        ecommerceDAO;

    @Override
    public Ecommerce getByWebsiteId(Integer websiteId) {
        Ecommerce ecommerce = null;
        if (null != websiteId) {
            String key = "rawdatacentral_ecommerce_websiteid_" + websiteId;
            ecommerce = redisService.getCache(key, Ecommerce.class);
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
