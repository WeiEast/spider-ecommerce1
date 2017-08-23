package com.datatrees.crawler.core.processor.format;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.crawler.core.processor.decode.impl.HexDecoder;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Jerry
 * @since 00:06 22/05/2017
 */
public class DecodeUtilsTest {

    public static void main(String[] args) throws Exception {
        String data = "&#29238;&#20146;&#30340;&#21517;&#23383;&#27597;&#20146;&#30340;&#21517;&#23383;&#25105;&#30340;&#21517;&#23383;&#22969;&#22969;&#30340;&#21517;&#23383;";

        data = "\u041A\u0430\u0441\u0442\u0438\u043B\u0438\u044F - \u041B\u0430 \u041C\u0430\u043D\u0447\u0430";

        data = "xx\\xE9\\xBB\\x84\\xE8\\x8A\\xB1\\xE6\\xA2\\xA8\\xE5\\xAE\\xB6\\xE5\\x85\\xB7\\xE8\\xBD\\xAC\\xE8\\xAE\\xA9";
        System.out.println(StringEscapeUtils.unescapeJava(data.replace("\\x", "\\u00")));
        System.out.println(StringEscapeUtils.unescapeHtml(data.replace("\\x", "\\u00")));
        System.out.println(data);

        System.out.println(new HexDecoder().decode(data, "UTF-8"));
        // int i = 100000;
        //
        // String binStr = Integer.toBinaryString(i);
        //
        // String otcStr = Integer.toOctalString(i);
        //
        // String hexStr = Integer.toHexString(i);
        //
        // System.out.println(binStr);
        // System.out.println(otcStr);
        //
        // System.out.println(hexStr);

        String string = "中国\u6211\u7231\u5317\u4EAC";
        byte[] utf8 = string.getBytes("UTF-8");
        string = new String(utf8, "UTF-8");
        System.out.println(string);
        // i = Integer.valueOf("E9", 16);
        // System.out.println(Integer.valueOf("E9", 16).toString());

        String source = "xx\\xE9\\xBB\\x84\\xE8\\x8A\\xB1\\xE6\\xA2\\xA8\\xE5\\xAE\\xB6\\xE5\\x85\\xB7\\xE8\\xBD\\xAC\\xE8\\xAE\\xA9xxxx\\xE9\\xBB\\x84";
        String sourceArr[] = source.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
        System.out.println("length.." + sourceArr.length);
        // byte[] byteArr = new byte[sourceArr.length - 1];
        // for (int i = 1; i < sourceArr.length; i++) {
        // Integer hexInt = Integer.decode("0" + sourceArr[i]);
        // byteArr[i - 1] = hexInt.byteValue();
        // }
        //
        // System.out.println(new String(byteArr));

        String regex = "\\\\x([\\w]{2})\\\\";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            System.out.println("start..." + matcher.start());
            System.out.println("end..." + matcher.end());
            System.out.println(matcher.group(1));
        }

        // String source =
        // "\\xE9\\xBB\\x84\\xE8\\x8A\\xB1\\xE6\\xA2\\xA8\\xE5\\xAE\\xB6\\xE5\\x85\\xB7\\xE8\\xBD\\xAC\\xE8\\xAE\\xA9";
        // String sourceArr[] = source.split("\\\\"); // 分割拿到形如 xE9 的16进制数据
        // byte[] byteArr = new byte[sourceArr.length - 1];
        // for (int i = 1; i < sourceArr.length; i++) {
        // Integer hexInt = Integer.decode("0" + sourceArr[i]);
        // byteArr[i - 1] = hexInt.byteValue();
        // }
        //
        // System.out.println(new String(byteArr, "UTF-8"));

    }
}