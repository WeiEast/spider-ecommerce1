package com.datatrees.rawdatacentral.collector.common;

import java.util.Comparator;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:33:40
 */
public class LinkNodeComparator implements Comparator<byte[]> {

    public LinkNodeComparator() {}

    public static long byte2long(byte[] b) {
        long value = 0;
        String strValue = "";
        int len = b.length;
        for (int i = 0; i < len; i++) {
            strValue += (b[i] - 48);
        }
        value = Long.parseLong(strValue);
        return value;
    }

    public int compare(byte[] o1, byte[] o2) {

        long k1 = byte2long(o1);
        long k2 = byte2long(o2);

        if (k1 == k2) {
            return 0;
        }
        return k1 < k2 ? -1 : 1;
    }
}
