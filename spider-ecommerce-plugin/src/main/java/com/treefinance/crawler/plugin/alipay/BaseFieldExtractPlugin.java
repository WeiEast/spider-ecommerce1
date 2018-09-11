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
import java.util.List;
import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.treefinance.crawler.framework.context.AbstractProcessorContext;
import com.treefinance.crawler.framework.extension.plugin.AbstractClientPlugin;
import com.treefinance.crawler.framework.extension.plugin.PluginConstants;
import com.treefinance.crawler.framework.util.xpath.XPathUtil;
import com.google.gson.reflect.TypeToken;
import com.treefinance.crawler.framework.extension.plugin.ProcessContextHolder;
import com.treefinance.toolkit.util.json.Jackson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Jerry
 * @since 16:47 03/01/2018
 */
public abstract class BaseFieldExtractPlugin<T extends AbstractProcessorContext> extends AbstractClientPlugin {

    @SuppressWarnings("unchecked")
    @Override
    public String process(String... args) throws Exception {
        if (ArrayUtils.isEmpty(args)) {
            throw new NullPointerException("There is no arguments used for plugin [" + getClass() + "]!");
        }

        Map<String, String> params = GsonUtils.fromJson(args[0], new TypeToken<HashMap<String, String>>() {}.getType());

        String content = params.get(PluginConstants.PAGE_CONTENT);
        if (StringUtils.isNotEmpty(content)) {
            Object value = extract(content, (T) ProcessContextHolder.getProcessorContext());

            if ((value instanceof String && !((String) value).isEmpty()) || value != null) {
                Map<String, Object> result = new HashMap<>();
                result.put(PluginConstants.FIELD, value);
                return Jackson.toJSONString(result);
            }
        }

        return null;
    }

    protected abstract Object extract(String content, T processorContext) throws Exception;

    protected static String getValueByXpath(String content, String xpath) {
        List<String> list = XPathUtil.getXpath(xpath, content);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
}
