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

package com.treefinance.crawler.plugin.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 11:58 13/03/2018
 */
public class TopApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopApi.class);

    /**
     * mtop签名,token 就是cookie里的_m_h5_tk以'_'分割第0个
     */
    public static String sign(String h5Token, String appKey, long timestamp, String data) throws Exception {
        StringBuilder c = new StringBuilder();
        if (StringUtils.isNotBlank(h5Token)) {
            c.append(h5Token);
        }
        c.append("&").append(timestamp).append("&").append(appKey).append("&").append(data);
        String sign = (String) ScriptEngineUtil.evalScript("js/mtop.sign.js", "h", c.toString());
        LOGGER.info("sign is {}", sign);
        return sign;
    }

    public static String getUmData() throws Exception {
        String sign = (String) ScriptEngineUtil.evalScript("js/um_data.js", "getData");
        LOGGER.info("um.data is {}", sign);
        return sign;
    }
}
