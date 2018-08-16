/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.spider.operator.service.normalizer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.operator.dao.OperatorDAO;
import com.datatrees.spider.operator.domain.OperatorData;
import com.datatrees.spider.operator.domain.model.Operator;
import com.datatrees.spider.operator.domain.model.example.OperatorExample;
import com.datatrees.spider.share.common.share.service.RedisService;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.normalizers.MessageNormalizer;
import com.treefinance.crawler.framework.process.domain.ExtractObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月31日 上午11:50:36
 */
@Service
public class OperatorMessageNormalizer implements MessageNormalizer {

    private static final Logger       logger = LoggerFactory.getLogger(OperatorMessageNormalizer.class);

    @Resource
    private              RedisService redisService;

    @Resource
    private              OperatorDAO  operatorDAO;

    @Override
    public boolean normalize(ExtractMessage message) {
        Object object = message.getMessageObject();
        if (object instanceof OperatorData) {
            message.setResultType(ResultType.OPERATOR);
            message.setTypeId(this.getOperatorId(message));
            ((OperatorData) object).setOperatorId(message.getTypeId());
            ((OperatorData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if (object instanceof ExtractObject && OperatorData.class.getSimpleName().equals(((ExtractObject) object).getResultClass())) {
            OperatorData operatorData = new OperatorData();
            operatorData.putAll((Map) object);

            message.setMessageObject(operatorData);
            message.setResultType(ResultType.OPERATOR);
            message.setTypeId(this.getOperatorId(message));

            operatorData.setOperatorId(message.getTypeId());
            operatorData.setResultType(message.getResultType().getValue());
            return true;
        } else {
            return false;
        }
    }

    private int getOperatorId(ExtractMessage message) {
        Operator operator = getByWebsiteId(message.getWebsiteId());
        if (operator == null) {
            logger.warn("operator not found websiteId={}", message.getWebsiteId());
            return 0;
        }
        return operator.getId();
    }

    private Operator getByWebsiteId(Integer websiteId) {
        if (null == websiteId) {
            logger.warn("invalid param websiteId is null");
            return null;
        }
        String key = "rawdatacentral_operator_websiteid_" + websiteId;
        Operator operator = redisService.getCache(key, new TypeReference<Operator>() {});
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
