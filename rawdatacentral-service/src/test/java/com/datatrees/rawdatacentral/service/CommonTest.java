package com.datatrees.rawdatacentral.service;

import com.alibaba.fastjson.JSON;
import com.treefinance.spider.common.util.http.IpUtils;
import com.treefinance.spider.common.util.http.domain.IpLocale;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonTest {

    private static final Logger logger = LoggerFactory.getLogger(CommonTest.class);

    @Test
    public void test1() {
        String ip = "116.62.120.213";
        IpLocale locale = IpUtils.queryIpLocale(ip);
        System.out.println(JSON.toJSONString(locale));
    }

}
