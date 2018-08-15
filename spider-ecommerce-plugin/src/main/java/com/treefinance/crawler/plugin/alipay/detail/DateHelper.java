/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.crawler.plugin.alipay.detail;

import java.util.HashMap;
import java.util.Map;

import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 18:00 03/01/2018
 */
public final class DateHelper {

    private static final Logger              LOGGER       = LoggerFactory.getLogger(DateHelper.class);
    private static final String              DATE_PATTERN = "yyyyMMdd";
    private static final Map<String, String> DATE_WEEK    = new HashMap<>();

    static {
        DATE_WEEK.put("周日", "Sunday");
        DATE_WEEK.put("周一", "Monday");
        DATE_WEEK.put("周二", "Tuesday");
        DATE_WEEK.put("周三", "Wednesday");
        DATE_WEEK.put("周四", "Thursday");
        DATE_WEEK.put("周五", "Friday");
        DATE_WEEK.put("周六", "Saturday");
    }

    private DateHelper() {
    }

    /**
     * 由于余额明细中的时间只有月份日期，按一定规则进行适配出年份。
     * <p>
     * 规则：
     * 1.MM-dd: 余额明细是只查询最近6个月的。按当前年份计算日期，如果超过当前时间，那么减一年，否则按当前年份返回。
     * 2.昨天: 当前时间减一天。
     * 3.前天: 当前时间减二天。
     * 4.周几：
     * </p>
     * @param dateStr 格式：MM-dd|昨天|前天|周[一,二...]
     * @return 包含年份的时间，格式：yyyyMMdd
     */
    public static String adapt(final String dateStr) {
        String value = StringUtils.trim(dateStr);
        if (StringUtils.isNotEmpty(value)) {
            LocalDate now = LocalDate.now();

            if ("昨天".equals(value)) {
                return now.minusDays(1).toString(DATE_PATTERN);
            } else if ("前天".equals(value)) {
                return now.minusDays(2).toString(DATE_PATTERN);
            } else {
                String week = DATE_WEEK.get(value);
                if (week != null) {
                    LocalDate time = now.dayOfWeek().setCopy(week);
                    if (time.isAfter(now)) {
                        return time.minusWeeks(1).toString(DATE_PATTERN);
                    }

                    return time.toString(DATE_PATTERN);
                } else {
                    String[] items = dateStr.split("-");
                    if (items.length == 2) {
                        String month = StringUtils.stripStart(items[0].trim(), "0");
                        //String date = StringUtils.stripStart(items[1], "0");
                        String date = items[1].trim();
                        LocalDate time = now.monthOfYear().setCopy(month).dayOfMonth().setCopy(date);
                        if (time.isAfter(now)) {
                            return time.minusYears(1).toString(DATE_PATTERN);
                        }

                        return time.toString(DATE_PATTERN);
                    }
                }
            }
        }

        return value;
    }

    public static String getFromTradeNo(final String tradeNumber) {
        String tradeNo = StringUtils.trim(tradeNumber);
        if (StringUtils.isNotEmpty(tradeNo) && RegExp.matches(tradeNo, "(^19|20)[\\w*-]+$")) {
            try {
                String dateString = tradeNo.substring(0, 8);
                LocalDate date = DateTimeFormat.forPattern(DATE_PATTERN).parseLocalDate(dateString);
                if (!date.isAfter(LocalDate.now())) {
                    return dateString;
                }
            } catch (Exception e) {
                LOGGER.warn("Error parsing trade date from tradeNo", e.getMessage());
            }
        }
        return null;
    }

    public static String defaultTime(final String timeString) {
        return StringUtils.isBlank(timeString) ? "00:00" : timeString.trim();
    }

}
