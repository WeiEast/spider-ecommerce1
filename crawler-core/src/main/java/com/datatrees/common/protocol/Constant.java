/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol;

import com.datatrees.common.conf.PropertiesConfiguration;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
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
