
package com.datatrees.rawdatacentral.collector.common;


/**
 *
 * @author  <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since   2015年7月20日 上午12:46:18 
 */
public class LinkNodeSpecialComparator {
    public LinkNodeSpecialComparator() {}

    public static double byte2double(byte[] b) {
        double value = 0.0d;
        try {
            value = Double.parseDouble(new String(b));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    public int compare(byte[] o1, byte[] o2) {

        double k1 = byte2double(o1);
        double k2 = byte2double(o2);

        if (k1 == k2) {
            return 0;
        }
        return k1 < k2 ? -1 : 1;
    }
}
