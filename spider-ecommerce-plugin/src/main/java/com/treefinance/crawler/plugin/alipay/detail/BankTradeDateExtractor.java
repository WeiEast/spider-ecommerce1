/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.treefinance.crawler.plugin.alipay.detail;

import com.treefinance.crawler.framework.context.ExtractorProcessorContext;
import com.treefinance.crawler.plugin.alipay.BaseFieldExtractPlugin;

/**
 * APP端银行卡收支明细的交易时间字段提取
 * 
 * @author Jerry
 * @since 16:36 03/01/2018
 */
public class BankTradeDateExtractor extends BaseFieldExtractPlugin<ExtractorProcessorContext> {

    @Override
    protected Object extract(String content, ExtractorProcessorContext processorContext) throws Exception {
        logger.debug("App bank detail >>> {}", content);

        String tradeNumber = (String)processorContext.getAttribute("tradeNumber");
        if (tradeNumber == null) {
            tradeNumber = getValueByXpath(content, "//li/div[@class='cm-trade-rows']/@data-tradeNo");
        }

        String dateString = DateHelper.getFromTradeNo(tradeNumber);
        if (dateString == null) {
            String text = getValueByXpath(content, "div.cm-trade-time > p > span.cm-trade-date/text()");
            dateString = DateHelper.adapt(text);
        }

        String timeString = getValueByXpath(content, "div.cm-trade-time > p > span.cm-trade-hour/text()");

        return dateString + " " + DateHelper.defaultTime(timeString);
    }

}
