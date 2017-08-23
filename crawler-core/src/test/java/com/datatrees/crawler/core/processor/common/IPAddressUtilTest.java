package com.datatrees.crawler.core.processor.common;

import org.junit.Test;

/**
 * @author Jerry
 * @since 22:50 21/05/2017
 */
public class IPAddressUtilTest {

    @Test
    public void internalIp() throws Exception {
        System.out.println(IPAddressUtil.internalIp("58.246.138.171"));
        System.out.println(IPAddressUtil.internalIp("140.206.85.133"));
        System.out.println(IPAddressUtil.internalIp("21.123.76.108"));
        System.out.println(IPAddressUtil.internalIp("192.168.216.137"));
        System.out.println(IPAddressUtil.internalIp("10.55.5.213"));
        System.out.println(IPAddressUtil.internalIp("172.24.32.106"));
    }

}