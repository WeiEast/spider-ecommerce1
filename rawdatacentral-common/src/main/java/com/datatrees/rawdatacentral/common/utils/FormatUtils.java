package com.datatrees.rawdatacentral.common.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.datatrees.spider.share.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 格式化工具
 */
public class FormatUtils {

    private static final Logger  logger  = LoggerFactory.getLogger(FormatUtils.class);

    /**
     * 匹配${**}
     */
    private static final Pattern COMPILE = Pattern.compile("\\$\\{(\\w*)}");

    /**
     * 查找占位字段
     * @param template 模板
     * @return
     */
    public static List<String> getFields(String template) {
        Matcher matcher = COMPILE.matcher(template);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        return list;
    }

    /**
     * 格式化
     * @param template    模板
     * @param param       填充参数
     * @param datePattern 时间格式化格式,
     * @return
     */
    public static String format(String template, Map<String, Object> param, String datePattern) {
        if (StringUtils.isBlank(template)) {
            logger.error("template is blank");
            return template;
        }
        if (CollectionUtils.isEmpty(param)) {
            logger.error("param is empty");
            return template;
        }
        List<String> fields = getFields(template);
        if (CollectionUtils.isEmpty(fields)) {
            logger.warn("not need to replace template={}", template);
            return template;
        }
        if (CollectionUtils.isEmpty(param)) {
            return template;
        }
        if (StringUtils.isBlank(datePattern)) {
            datePattern = DateUtils.YMDHMS;
        }
        for (String field : fields) {
            if (!param.containsKey(field)) {
                logger.warn("param is not containsKey field={},template={},param={}", field, template,
                        JSON.toJSONStringWithDateFormat(param, DateUtils.YMDHMS));
            }
            String replace = "\\$\\{(" + field + ")}";
            Object value = param.get(field);
            if (null == value) {
                logger.warn("field value is null,field={}", field);
                template = template.replaceAll(replace, "");
                continue;
            }
            if (value instanceof Date) {
                template = template.replaceAll(replace, DateUtils.format((Date) value, datePattern));
                continue;
            }

            template = template.replaceAll(replace, value.toString());
        }
        return template;
    }

    /**
     * 格式化
     * @param template 模板
     * @param param    填充参数
     * @return
     */
    public static String format(String template, Map<String, Object> param) {
        return format(template, param, DateUtils.YMDHMS);
    }

    public static void main(String[] args) {
        String successTemplate = "【${status}-${websiteTitle}-${env}】\n配置:${websiteName}\n成功:${lastSuccessTime}";
        String titleTmeplate = "【${status}-${websiteTitle}-${env}】";

        String smsWarnTemplate = "【${status}-${websiteTitle}-${env}】\n配置:${websiteName}\n阈值:人数${maxUser},间隔${resendTime}分钟" +
                "\n成功:${lastSuccessTime}\n失败:${firstFailTime" + "}至$" + "{lastFailTime" + "}\n任务:${failTaskCount},用户:${failUserCount}";

        String template = "【${status}-${websiteTitle}-${env}】\n配置:${websiteName}成功:${lastSuccessTime}";
        Map<String, Object> map = new HashMap<>();
        map.put("status", "预警");
        map.put("websiteTitle", "四川电信(web)");
        map.put("websiteName", "si_chuan_10000_web");
        map.put("env", "product");
        map.put("lastSuccessTime", new Date());
        map.put("firstFailTime", new Date());
        map.put("lastFailTime", new Date());
        map.put("failTaskCount", 65);
        map.put("failUserCount", 37);
        map.put("maxUser", 5);
        map.put("resendTime", 2);
        String msg = format(smsWarnTemplate, map);
        System.out.println(msg);
        System.out.println(msg.length());

    }
}
