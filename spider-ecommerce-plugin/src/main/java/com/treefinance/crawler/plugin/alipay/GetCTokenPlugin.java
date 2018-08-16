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

package com.treefinance.crawler.plugin.alipay;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.treefinance.crawler.framework.extension.plugin.ProcessContextHolder;
import com.treefinance.toolkit.util.RegExp;

public class GetCTokenPlugin extends AbstractClientPlugin {

    @Override
    public String process(String... args) throws Exception {
        logger.info("获取ctoken插件--启动--成功");
        String cookieString = ProcessorContextUtil.getCookieString(ProcessContextHolder.getProcessorContext());
        String ctoken = RegExp.group(cookieString,"ctoken=([^;]+);",1);

        logger.info("获取ctoken结果：{}", ctoken);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(PluginConstants.FIELD, ctoken);
        return GsonUtils.toJson(resultMap);
    }
}
