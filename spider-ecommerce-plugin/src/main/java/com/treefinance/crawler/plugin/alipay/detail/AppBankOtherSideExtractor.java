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

package com.treefinance.crawler.plugin.alipay.detail;

import com.datatrees.crawler.core.processor.ExtractorProcessorContext;

/**
 * pc端余额收支明细otherSide提取
 * User: yand
 * Date: 2018/1/9
 */
public class AppBankOtherSideExtractor extends OtherSideExtractor {

    @Override
    protected String getTradeNo(String content, ExtractorProcessorContext processorContext) {
        boolean support = false;
        Object tradeType = processorContext.getContext().get("tradeType");
        if (tradeType == null || TRADE_TYPE.equals(String.valueOf(tradeType))) {
            String bizType = getValueByXpath(content, "//li/div[@class='cm-trade-rows']/@data-bizType");
            support = BIZ_TYPE.equals(bizType);
        }

        String tradeNo = null;

        if(support){
            tradeNo = (String) processorContext.getContext().get("tradeNumber");
            if(tradeNo == null){
                tradeNo = getValueByXpath(content, "//li/div[@class='cm-trade-rows']/@data-tradeNo");
            }
        }

        return tradeNo;
    }

}
