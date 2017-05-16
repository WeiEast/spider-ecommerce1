package com.datatrees.rawdatacentral.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/5/16.
 */
public class DateUtils {

    /**
     * 时间格式
     */
    public static final String                                YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String                                YMD    = "yyyy-MM-dd HH:mm:ss";

    private static ThreadLocal<Map<String, SimpleDateFormat>> sfs    = new ThreadLocal<Map<String, SimpleDateFormat>>() {
         @Override
         protected Map<String, SimpleDateFormat> initialValue() {
             Map<String, SimpleDateFormat> map = new HashMap<>();
             map.put(YMDHMS,
                 new SimpleDateFormat(YMDHMS));
             map.put(YMD, new SimpleDateFormat(YMD));
             return map;
         }
     };

    /**
     * 返回一个SimpleDateFormat,每个线程只会new一次pattern对应的sdf 
     *
     * @param pattern
     * @return
     */
    private static SimpleDateFormat getDateFormat(final String pattern) {
        Map<String, SimpleDateFormat> tl = sfs.get();
        SimpleDateFormat sdf = tl.get(pattern);
        if (sdf == null) {
            System.out.println(Thread.currentThread().getName() + " put new sdf of pattern " + pattern + " to map");
            sdf = new SimpleDateFormat(pattern);
            tl.put(pattern, sdf);
        }
        return sdf;
    }

    /**
     * 格式化成yyyy-MM-dd HH:mm:ss
     * @param date 时间
     * @return
     */
    public static String formatYmdhms(Date date) {
        return getDateFormat(YMDHMS).format(date);
    }

    /**
     * 格式化成yyyy-MM-dd HH:mm:ss
     * @param date 时间
     * @return
     */
    public static String formatYmdhms(long date) {
        return getDateFormat(YMDHMS).format(new Date(date));
    }

    /**
     * 格式化成yyyy-MM-dd
     * @param date 时间
     * @return
     */
    public static String formatYmd(Date date) {
        return getDateFormat(YMD).format(date);
    }

}
