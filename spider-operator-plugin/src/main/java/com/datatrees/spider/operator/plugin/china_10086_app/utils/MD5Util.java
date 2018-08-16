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

package com.datatrees.spider.operator.plugin.china_10086_app.utils;

import java.security.MessageDigest;

public class MD5Util {

    public static synchronized String MD5(String sourceStr, int flag) {
        String substring;
        synchronized (MD5Util.class) {
            try {
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                mdInst.update(sourceStr.getBytes());
                byte[] md = mdInst.digest();
                StringBuffer buf = new StringBuffer();
                for (int tmp : md) {
                    int tmp2 = tmp;
                    if (tmp2 < 0) {
                        tmp2 += 256;
                    }
                    if (tmp2 < 16) {
                        buf.append("0");
                    }
                    buf.append(Integer.toHexString(tmp2));
                }
                if (flag == 16) {
                    substring = buf.toString().substring(8, 24);
                } else {
                    substring = buf.toString();
                }
            } catch (Exception e) {
                substring = null;
            }
        }
        return substring;
    }
}
