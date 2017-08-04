package com.datatrees.rawdatacentral.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.datatrees.rawdatacentral.dao.OperatorDAO;
import com.datatrees.rawdatacentral.domain.model.Operator;
import com.datatrees.rawdatacentral.domain.model.example.OperatorExample;
import com.datatrees.rawdatacentral.service.OperatorService;
import com.datatrees.rawdatacentral.share.RedisService;

/**
 * Created by zhouxinghai on 2017/6/27.
 */
@Service
public class OperatorServiceImpl implements OperatorService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorServiceImpl.class);

    @Resource
    private RedisService        redisService;

    @Resource
    private OperatorDAO         operatorDAO;

    @Override
    public Operator getByWebsiteId(Integer websiteId) {
        if (null == websiteId) {
            logger.warn("invalid param websiteId is null");
            return null;
        }
        String key = "rawdatacentral_operator_websiteid_" + websiteId;
        Operator operator = redisService.getCache(key, new TypeReference<Operator>(){});
        if (null == operator) {
            OperatorExample example = new OperatorExample();
            example.createCriteria().andWebsiteidEqualTo(websiteId).andIsenabledEqualTo(true);
            List<Operator> list = operatorDAO.selectByExample(example);
            if (null != list && !list.isEmpty()) {
                operator = list.get(0);
                redisService.cache(key, operator, 1, TimeUnit.HOURS);
            }
        }
        return operator;
    }
}
