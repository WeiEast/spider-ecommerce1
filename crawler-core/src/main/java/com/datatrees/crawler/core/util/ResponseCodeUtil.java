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

package com.datatrees.crawler.core.util;

import com.datatrees.common.protocol.ProtocolStatusCodes;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 10, 2014 4:11:30 PM
 */
@Deprecated
public class ResponseCodeUtil {

    public static boolean isSuccess(int code) {
        return ProtocolStatusCodes.SUCCESS == code;
    }

    public static boolean isRedirector(int code) {
        return code == ProtocolStatusCodes.MOVED || code == ProtocolStatusCodes.TEMP_MOVED;
    }

    public static boolean isFail(int code) {
        return code == ProtocolStatusCodes.EXCEPTION;
    }

}
