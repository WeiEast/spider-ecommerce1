/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.ecommerce.service.normalizer;

import javax.annotation.Resource;
import java.util.Map;

import com.datatrees.spider.ecommerce.service.EcommerceService;
import com.datatrees.spider.share.domain.ResultType;
import com.datatrees.spider.share.domain.model.Ecommerce;
import com.datatrees.spider.share.service.domain.ExtractMessage;
import com.datatrees.spider.share.service.domain.data.EcommerceData;
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
public class EcommerceMessageNormalizer implements MessageNormalizer {

    private static final Logger           LOGGER = LoggerFactory.getLogger(EcommerceMessageNormalizer.class);

    @Resource
    private              EcommerceService ecommerceService;

    @Override
    public boolean normalize(ExtractMessage message) {
        Object object = message.getMessageObject();
        if (object instanceof EcommerceData) {
            message.setResultType(ResultType.ECOMMERCE);
            message.setTypeId(this.getEcommerceId(message));
            ((EcommerceData) object).setResultType(message.getResultType().getValue());
            return true;
        } else if ((object instanceof ExtractObject && EcommerceData.class.getSimpleName().equals(((ExtractObject) object).getResultClass()))) {
            EcommerceData ecommerceData = new EcommerceData();
            ecommerceData.putAll((Map) object);

            message.setMessageObject(ecommerceData);
            message.setResultType(ResultType.ECOMMERCE);
            message.setTypeId(this.getEcommerceId(message));
            ecommerceData.setResultType(message.getResultType().getValue());
            return true;
        } else {
            return false;
        }
    }

    private int getEcommerceId(ExtractMessage message) {
        Ecommerce ecommerce = ecommerceService.getByWebsiteId(message.getWebsiteId());
        if (ecommerce == null) {
            LOGGER.warn("get null ecommerce with websiteId={} ", message.getWebsiteId());
            return 0;
        }
        return ecommerce.getId();
    }
}
