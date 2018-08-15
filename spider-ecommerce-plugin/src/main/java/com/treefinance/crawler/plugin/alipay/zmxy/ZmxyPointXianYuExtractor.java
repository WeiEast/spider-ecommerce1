/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

package com.treefinance.crawler.plugin.alipay.zmxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.treefinance.crawler.plugin.alipay.BaseFieldExtractPlugin;
import org.apache.commons.lang3.StringUtils;

/**
 * User: yand
 * Date: 2018/3/6
 */
public class ZmxyPointXianYuExtractor extends BaseFieldExtractPlugin<ExtractorProcessorContext> {

    @Override
    protected Object extract(String content, ExtractorProcessorContext processorContext) throws Exception {
        logger.info("zmxy content >>> {}", content);

        if (StringUtils.isBlank(content)) {
            return null;
        }

        String point = null;
        try {
            JSONObject json = JSON.parseObject(content);
            JSONObject data = json.getJSONObject("data");
            if (Boolean.TRUE.equals(data.getBoolean("success"))) {
                point = data.getString("ownerUserInfo");
            }
        } catch (Exception e) {
            logger.error("Something is wrong when requesting zmxy point!", e);
        }

        //JSONObject jsonObject = JSON.parseObject(content);
        //String creditScore = jsonObject.getJSONObject("data").getString("score");
        logger.info("zmxy point  >>> {}", point);
        return point;
    }

}
