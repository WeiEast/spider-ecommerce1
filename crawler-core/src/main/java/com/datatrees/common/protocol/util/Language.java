package com.datatrees.common.protocol.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.datatrees.common.conf.PropertiesConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月20日 下午5:30:33
 */
public enum Language {

    INSTATNCE;

    public final static String               LANGUAGE_JP                  = "JP";

    public final static String               LANGUAGE_CH                  = "CH";

    public final static String               LANGUAGE_RU                  = "RU";

    public final static String               LANGUAGE_KO                  = "KO";

    public final static String               LANGUAGE_AR                  = "AR";

    public final static String               LANGUAGE_DE                  = "DE";

    public final static String               LANGUAGE_TH                  = "TH";

    public final static String               LANGUAGE_EN                  = "EN";

    public final static String               USER_LANGUAGE_CONFIG         = PropertiesConfiguration.getInstance().get("user.language.config", "");

    public final static String               USER_LANGUAGE_PATTERN_PREFIX = "pattern.";

    private             Map<String, Pattern> languagePatternMapping       = new LinkedHashMap<String, Pattern>();

    private Language() {
        init();
    }

    public static Language getInstance() {
        return INSTATNCE;
    }

    public static void main(String[] args) {
        String word = "中国aja";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
        word = "初めまして」（はじめまして";
        System.out.println(word + " " + Language.getInstance().getLanguage(word));
        word = "Форум";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
        word = "위키백과, 우리 모두의 백과사전";
        System.out.println(word + " " + Language.getInstance().getLanguage(word));
        word = "العربية‎ al-ʻarabīyah";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
        word = "par an à un patient handicapé";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
        word = "ervation/roundTripSearchAccess.doแปลหน้านี้";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
        word = "Guten Tag !, wer er ist.";
        System.out.println(word + "--> " + Language.getInstance().getLanguage(word));
    }

    private void init() {
        // add default configuration
        addDefaultConfig();
        // add extended configuration
        addUserConfig();
    }

    private void addDefaultConfig() {
        languagePatternMapping.put(LANGUAGE_JP, Pattern.compile("[\\u3041-\\u30FF\\u3104-\\u312A\\uFF66-\\uFF9E]+"));
        languagePatternMapping.put(LANGUAGE_CH, Pattern.compile("[\\u4E00-\\u9FFF]+"));
        languagePatternMapping.put(LANGUAGE_RU, Pattern.compile("[\\u0400-\\u052F]+"));
        languagePatternMapping.put(LANGUAGE_KO, Pattern.compile("[\\uAC00-\\uD7AC]+"));
        languagePatternMapping.put(LANGUAGE_AR, Pattern.compile("[\\u0600-\\u06FF-\\u0750-\\u077F]+"));
        languagePatternMapping.put(LANGUAGE_DE, Pattern.compile("[\\u00C0-\\u00FF]+"));
        languagePatternMapping.put(LANGUAGE_TH, Pattern.compile("[\\u0E00-\\u0E7F]+"));
        languagePatternMapping.put(LANGUAGE_EN, Pattern.compile("[A-Za-z]+"));
    }

    private void addUserConfig() {
        if (StringUtils.isNotEmpty(USER_LANGUAGE_CONFIG)) {
            Map<String, Pattern> userLanguagePatternMapping = new LinkedHashMap<String, Pattern>();
            for (String lan : USER_LANGUAGE_CONFIG.split(",")) {
                String patternString = PropertiesConfiguration.getInstance().get(USER_LANGUAGE_PATTERN_PREFIX + lan);
                if (StringUtils.isNotEmpty(patternString)) {
                    userLanguagePatternMapping.put(lan.toUpperCase(), Pattern.compile(patternString));
                }
            }
            if (MapUtils.isNotEmpty(userLanguagePatternMapping)) {
                languagePatternMapping.putAll(userLanguagePatternMapping);
            }
        }
    }

    public boolean check(String word, Pattern pattern) {
        Matcher matcher = pattern.matcher(word);
        return matcher.find();
    }

    public String getLanguage(String word) {
        String resultLanguage = LANGUAGE_EN;
        for (Map.Entry<String, Pattern> l : languagePatternMapping.entrySet()) {
            String language = l.getKey();
            if (check(word, l.getValue())) {
                resultLanguage = language;
                break;
            }
        }
        return resultLanguage;
    }
}
