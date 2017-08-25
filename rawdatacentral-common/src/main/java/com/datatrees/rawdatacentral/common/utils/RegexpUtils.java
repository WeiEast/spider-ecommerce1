package com.datatrees.rawdatacentral.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexpUtils {

    public static String select(String source, String regex, int groupIndex) {
        CheckUtils.checkNotBlank(source, "source is blank");
        CheckUtils.checkNotBlank(regex, "regex is blank");
        Matcher matcher = Pattern.compile(regex).matcher(source);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return null;
    }
}
