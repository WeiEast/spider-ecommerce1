/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly prohibited.
 * All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.login;

import java.util.HashMap;
import java.util.Map;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.login.LoginUtil;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Aug 29, 2014 7:09:16 PM
 */
public class LoginUtilTest extends BaseConfigTest {

    @Test
    public void test1() throws Exception {
        String path = "link.xml";
        SearchProcessorContext wrapper = (getProcessorContext(path, "tehparadox.com"));
        Map<String, Object> account = new HashMap<String, Object>();
        account.put("username", "xbkaishui");
        account.put("password", "123456");
        String ss = LoginUtil.getInstance().doLogin(wrapper.getLoginConfig(), null);
        System.out.println(ss);
    }
}
