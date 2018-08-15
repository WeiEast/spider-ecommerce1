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
import com.treefinance.crawler.plugin.alipay.BaseFieldExtractPlugin;

/**
 * APP端花呗收支明细的交易时间字段提取
 * @author Jerry
 * @since 16:36 03/01/2018
 */
public class HuabeiTradeDateExtractor extends BaseFieldExtractPlugin<ExtractorProcessorContext> {

    @Override
    protected Object extract(String content, ExtractorProcessorContext processorContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("App huabei detail >>> {}", content);
        }

        String tradeNumber = (String) processorContext.getContext().get("tradeNumber");
        if (tradeNumber == null) {
            tradeNumber = getValueByXpath(content, "//div[@class='am-list-item']/@data-biznum");
            if (tradeNumber != null) {
                tradeNumber = tradeNumber.replaceFirst("tradeNO=", "");
            }
        }

        String dateString = DateHelper.getFromTradeNo(tradeNumber);
        if (dateString == null) {
            String text = getValueByXpath(content, "div.am-list-content > div.time/text()");
            dateString = DateHelper.adapt(text);
        }

        return dateString;
    }

}
