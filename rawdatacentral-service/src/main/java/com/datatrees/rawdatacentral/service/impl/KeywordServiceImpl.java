package com.datatrees.rawdatacentral.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.dao.KeywordDAO;
import com.datatrees.rawdatacentral.domain.model.Keyword;
import com.datatrees.rawdatacentral.domain.model.example.KeywordExample;
import com.datatrees.rawdatacentral.service.KeywordService;
import com.datatrees.rawdatacentral.share.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
@Service
public class KeywordServiceImpl implements KeywordService {

    private static final Logger logger = LoggerFactory.getLogger(KeywordServiceImpl.class);

    @Resource
    private RedisService        redisService;

    @Resource
    private KeywordDAO          keywordDAO;

    @Override
    public List<Keyword> queryByWebsiteType(Integer websiteType) {
        if (null == websiteType || websiteType <= 0) {
            logger.warn("invalid param websiteType={}", websiteType);
            return null;
        }
        String key = "rawdatacentral_keyword_websitetype_" + websiteType;
        List<Keyword> list = redisService.getCache(key, new TypeReference<List<Keyword>>(){});
        if (null == list || list.isEmpty()) {
            KeywordExample example = new KeywordExample();
            example.createCriteria().andWebsiteTypeEqualTo(websiteType).andIsenabledEqualTo(true);
            list = keywordDAO.selectByExample(example);
            if (null == list || list.isEmpty()) {
                redisService.cache(key, list, 1, TimeUnit.DAYS);
            }

        }
        return list;
    }
}
