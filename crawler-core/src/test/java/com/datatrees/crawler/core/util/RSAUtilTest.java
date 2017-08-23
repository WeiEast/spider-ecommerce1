/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.util;

import java.util.Map;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.common.RSAUtil;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年11月11日 下午2:33:12
 */
public class RSAUtilTest extends BaseConfigTest {

    @Test
    public void test() throws Exception {
        Map<String, String> kesMap = RSAUtil.generateKeyPair("");
        String encoded = RSAUtil.encrypt(kesMap.get("publicKey"), "wangcheng@datatrees.com.cn");
        System.out.println(encoded);
        System.out.println("公钥加密-私钥解密:" + RSAUtil.decrypt(kesMap.get("privateKey"), encoded));

        encoded = RSAUtil.encrypt(kesMap.get("privateKey"), "wangcheng@datatrees.com.cn", true);
        System.out.println(encoded);
        System.out.println("私钥加密-公钥解密:" + RSAUtil.decrypt(kesMap.get("publicKey"), encoded, true));
    }
}
