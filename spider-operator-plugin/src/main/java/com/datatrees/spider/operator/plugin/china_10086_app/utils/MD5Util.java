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
