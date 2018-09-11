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

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.crawler.exception.UnexpectedException;
import org.apache.commons.lang3.StringUtils;

/**
 * User: yand
 * Date: 2018/3/6
 */
public class ZmxyPointXianYuSpider extends ZmxyTopSpider {

    private static final String USER_API         = "com.taobao.idle.user.info.get";

    private static final String USER_API_VERSION = "3.0";

    private static final String ZM_API           = "com.taobao.idle.zhima.user.pair.get";

    private static final String ZM_API_VERSION   = "1.0";

    @Override
    public void doProcess() throws Exception {
        Info info = sendTopRequest(APP_KEY, USER_API, USER_API_VERSION, "{}", null, true);

        JSONObject json = JSON.parseObject(info.getData());
        JSONObject data = json.getJSONObject("data");

        String userId = null;
        if (data != null) {
            userId = data.getString("userId");
        }

        if (StringUtils.isEmpty(userId)) {
            throw new UnexpectedException("The useId used for zmxy top api must not be empty.");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("ownerUserId", userId);
        params.put("ignore", false);

        Info result = sendTopRequest(APP_KEY, ZM_API, ZM_API_VERSION, JSON.toJSONString(params), null, false);

        extractPageContent(result.getUrl(), StringUtils.defaultString(result.getData()));
    }

}
