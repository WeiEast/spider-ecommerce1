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

/**
 * User: yand
 * Date: 2018/3/6
 */
public class ZmxyPointXianYuSpider_old extends ZmxyTopSpider {

    private static final String DATA              = "{}";
    private static final String SCORE_API         = "com.taobao.idle.zhima.user.score.get";
    private static final String SCORE_API_VERSION = "1.0";

    @Override
    protected void doProcess() throws Exception {
        Info result = sendTopRequest(APP_KEY, SCORE_API, SCORE_API_VERSION, DATA, REFERER, false);

        extractPageContent(result.getUrl(), result.getData());
    }

}
