/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.encode;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.properties.UnicodeMode;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.decode.impl.HexDecoder;
import com.google.gson.reflect.TypeToken;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Mar 14, 2014 11:22:30 AM
 */
public class EncodeTest extends BaseConfigTest {

    @Test
    public void testDD() {
        String fileName = "input";
        String content = getContent(fileName);
        String regex = "((\\\\x([\\w\\d]{2}))+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            System.out.println("start..." + matcher.start());
            System.out.println("end..." + matcher.end());
            System.out.println(matcher.group(1));
            String re = matcher.group(1);

            System.out.println(re);
            String rel = hexToString(re, Charset.forName("utf-8"));
            matcher.appendReplacement(sb, rel);
            System.out.println(rel);
        }
        matcher.appendTail(sb);
        System.out.println(sb.toString());
    }

    private String hexToString(String content, Charset charSet) {
        String result = "";
        String sourceArr[] = content.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
        System.out.println("length.." + sourceArr.length);
        byte[] byteArr = new byte[sourceArr.length - 1];
        for (int i = 1; i < sourceArr.length; i++) {
            Integer hexInt = Integer.decode("0" + sourceArr[i]);
            byteArr[i - 1] = hexInt.byteValue();
        }
        try {
            result = (new String(byteArr, charSet));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Ignore
    @Test
    public void testTT() {
        String unicodeFormat = "{\"STsANDARD\":\"xxxxxx\",\"DECIMAL\":\"xeeeee\"}";
        Map<UnicodeMode, String> decoders = (Map<UnicodeMode, String>) GsonUtils.fromJson(unicodeFormat, new TypeToken<Map<UnicodeMode, String>>() {}.getType());
        System.out.println(decoders.size());
        System.out.println(decoders);
    }

    // unicode cover
    public String convert(String utfString) {
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;

        while ((i = utfString.indexOf("\\u", pos)) != -1) {
            sb.append(utfString.substring(pos, i));
            if (i + 5 < utfString.length()) {
                pos = i + 6;
                // int transfor hex
                sb.append((char) Integer.parseInt(utfString.substring(i + 2, i + 6), 16));
            }
        }
        return sb.toString();
    }

    @Test
    public void testUnicodeCover() {
        String encoding = "http://v.baidu.com/commonapi/tvplay2level/\\u7ea2\\u9ad8\\u7cb1";
        System.out.println(convert(encoding));
    }

    @Test
    public void testHex() {
        String content = getContent("input");
        HexDecoder decoder = new HexDecoder();
        System.out.println(decoder.decode(content, "UTF-8"));
    }

}
