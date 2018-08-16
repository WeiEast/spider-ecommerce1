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

package com.treefinance.crawler.plugin.alipay.zmxy;

import com.treefinance.crawler.framework.context.ExtractorProcessorContext;
import com.treefinance.crawler.plugin.alipay.BaseFieldExtractPlugin;

/**
 * 芝麻信用分的解析器
 * @author Jerry
 * @since 20:57 22/12/2017
 */
public class ZmxyPointExtractor extends BaseFieldExtractPlugin<ExtractorProcessorContext> {

    @Override
    protected Object extract(String content, ExtractorProcessorContext processorContext) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("zmxy extract content >>> {}", content);
        }

        return PointExtractor.extract(content);
    }

}
