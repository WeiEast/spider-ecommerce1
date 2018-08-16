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

package com.treefinance.crawler.plugin.alipay.detail;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.crawler.framework.context.ExtractorProcessorContext;

/**
 * pc端余额收支明细otherSide提取
 * User: yand
 * Date: 2018/1/9
 */
public class WebBankOtherSideExtractor extends OtherSideExtractor {

    @Override
    protected String getTradeNo(String content, ExtractorProcessorContext processorContext) {
        JSONObject jsonObject = null;

        String bizType = (String) processorContext.getAttribute("bizType");
        if (bizType == null) {
            jsonObject = JSON.parseObject(content);
            bizType = jsonObject.getString("c_bizType");
        }

        String tradeNo = null;
        if (BIZ_TYPE.equals(bizType)) {
            tradeNo = (String) processorContext.getAttribute("tradeNumber");
            if (tradeNo == null) {
                if (jsonObject == null) {
                    jsonObject = JSON.parseObject(content);
                }

                tradeNo = jsonObject.getString("c_bizInNum");
            }
        }

        return tradeNo;
    }

}
