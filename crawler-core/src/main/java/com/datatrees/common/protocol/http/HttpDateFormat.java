/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.common.protocol.http;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * class to handle HTTP dates.
 *
 * Modified from FastHttpDateFormat.java in jakarta-tomcat.
 *
 * @author John Xing
 */
public class HttpDateFormat {

    protected static SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

    /**
     * HTTP date uses TimeZone GMT
     */
    static {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    // HttpDate (long t) {
    // }

    // HttpDate (String s) {
    // }

    // /**
    // * Get the current date in HTTP format.
    // */
    // public static String getCurrentDate() {
    //
    // long now = System.currentTimeMillis();
    // if ((now - currentDateGenerated) > 1000) {
    // synchronized (format) {
    // if ((now - currentDateGenerated) > 1000) {
    // currentDateGenerated = now;
    // currentDate = format.format(new Date(now));
    // }
    // }
    // }
    // return currentDate;
    //
    // }

    /**
     * Get the HTTP format of the specified date.
     */
    public static String toString(Date date) {
        String string;
        synchronized (format) {
            string = format.format(date);
        }
        return string;
    }

    public static String toString(Calendar cal) {
        String string;
        synchronized (format) {
            string = format.format(cal.getTime());
        }
        return string;
    }

    public static String toString(long time) {
        String string;
        synchronized (format) {
            string = format.format(new Date(time));
        }
        return string;
    }

    public static Date toDate(String dateString) throws ParseException {
        Date date;
        synchronized (format) {
            date = format.parse(dateString);
        }
        return date;
    }

    public static long toLong(String dateString) throws ParseException {
        long time;
        synchronized (format) {
            time = format.parse(dateString).getTime();
        }
        return time;
    }

}
