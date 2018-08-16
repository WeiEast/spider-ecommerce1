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

package com.treefinance.crawler.plugin.alipay.address;

import java.util.ArrayList;
import java.util.List;

import com.treefinance.crawler.plugin.alipay.TopSpider;
import org.apache.http.message.BasicNameValuePair;

/**
 * User: yand
 * Date: 2018/7/26
 */
public class TaobaoAddressOpenApiSpider extends TopSpider {

    private static final String           JSV         = "2.4.2";
    private static final String           APPKEY      = "12574478";
    private static final String           API         = "mtop.taobao.mbis.getDeliverAddrList";
    private static final String           V           = "1.0";
    private static final String           NEED_LOGIN  = "true";
    private static final String           DATA_TYPE   = "jsoup";
    private static final String           TYPE        = "jsoup";
    private static final String           CALL_BACK   = "mtopjsonp3";
    private static final String           DATA        = "{}";
    private static final String           REFERER     = "https://member1.taobao.com/member/fresh/deliver_address.htm";

    @Override
    public void doProcess() throws Exception {
        Info info = sendTopRequest(API, V, APPKEY, DATA, REFERER, () -> {
            List<BasicNameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("jsv", JSV));
            list.add(new BasicNameValuePair("needLogin", NEED_LOGIN));
            list.add(new BasicNameValuePair("dataType", DATA_TYPE));
            list.add(new BasicNameValuePair("type", TYPE));
            list.add(new BasicNameValuePair("callback", CALL_BACK));
            return list;
        }, false);

        extractPageContent(info.getUrl(), info.getData());
    }

    @Override
    protected boolean needRetry(String content, String h5Token) {
        return content == null || !content.contains("SUCCESS");
    }

}
