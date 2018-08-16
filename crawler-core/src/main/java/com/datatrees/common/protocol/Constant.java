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

package com.datatrees.common.protocol;

import com.datatrees.common.conf.PropertiesConfiguration;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午8:01:00
 */
public interface Constant {

    public static       String HEADER_CHARSET_PATTERN = "charset=([\\w-]+)";
    public static final String REDIRECT_URL           = "REDIRECT_URL";
    public static final String URL_REGEX              = PropertiesConfiguration.getInstance().get("url.extractor.regex",
            "(mms://[^<'\\r\\n\\t#]+|rtsp://[^<'\\r\\n\\t#]+|rtmp://[^<'\\r\\n\\t#]+|pa://[^<'\\r\\n\\t#]+|thunder://[^<'\\r\\n\\t#:]+|bdhd://[^<'\\r\\n\\t#:]+|qvod://[^<'\\r\\n\\t#:]+|qvod://[^<'\\r\\n\\t#:]+|((([-\\w]+\\.)+(com|org|net|edu|gov|info|biz|eu|us|cn|jp|uk|hk|io|de|ru))/([-\\w]+\\.)+[\\w-]+(:\\d+)?|" +
                    "(((www\\.)|(https?(:|&#58;)//))([-\\w]+\\.)+[\\w-]+(:\\d+)?))(/[\\w-\\./?%&=\\*\\+\\[\\]\\(\\) ,:!]*)?)");

}
