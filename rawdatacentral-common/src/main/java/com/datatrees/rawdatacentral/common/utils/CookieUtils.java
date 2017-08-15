package com.datatrees.rawdatacentral.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxinghai on 2017/7/19.
 */
public class CookieUtils {

    private static final Logger logger       = LoggerFactory.getLogger(CookieUtils.class);

    private static RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);

    public static List<Cookie> getCookies(BasicCookieStore cookieStore) {
        CheckUtils.checkNotNull(cookieStore, "cookieStore is null");
        List<Cookie> list = new ArrayList<>();
        for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
            BasicClientCookie basicClientCookie = (BasicClientCookie) cookie;
            Cookie myCookie = new Cookie();
            myCookie.setDomain(basicClientCookie.getDomain());
            myCookie.setPath(basicClientCookie.getPath());
            myCookie.setName(basicClientCookie.getName());
            myCookie.setValue(basicClientCookie.getValue());
            myCookie.setSecure(basicClientCookie.isSecure());
            myCookie.setVersion(basicClientCookie.getVersion());
            myCookie.setExpiryDate(basicClientCookie.getExpiryDate());
            if (basicClientCookie.containsAttribute("domain")) {
                myCookie.getAttribs().put("domain", basicClientCookie.getAttribute("domain"));
            }
            if (basicClientCookie.containsAttribute("path")) {
                myCookie.getAttribs().put("path", basicClientCookie.getAttribute("path"));
            }
            if (basicClientCookie.containsAttribute("expires")) {
                myCookie.getAttribs().put("expires", basicClientCookie.getAttribute("expires"));
            }
            list.add(myCookie);
        }
        return list;
    }

    public static BasicClientCookie getBasicClientCookie(Cookie myCookie) {
        BasicClientCookie cookie = new BasicClientCookie(myCookie.getName(), myCookie.getValue());
        cookie.setDomain(myCookie.getDomain());
        cookie.setPath(myCookie.getPath());
        cookie.setSecure(myCookie.isSecure());
        cookie.setVersion(myCookie.getVersion());
        cookie.setExpiryDate(myCookie.getExpiryDate());
        for (Map.Entry<String, String> entry : myCookie.getAttribs().entrySet()) {
            cookie.setAttribute(entry.getKey(), entry.getValue());
        }
        return cookie;
    }

    public static String getCookieString(Long taskId) {
        StringBuilder sb = new StringBuilder();
        BasicCookieStore cookieStore = getCookie(taskId);
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static String getReceiveCookieString(String sendCookies, BasicCookieStore cookieStore) {
        if (StringUtils.isBlank(sendCookies)) {
            return getCookieString(cookieStore);
        }
        StringBuilder sb = new StringBuilder();
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
                if (!sendCookies.contains(cookie.getName() + "=")) {
                    sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
                }
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static String getCookieString(BasicCookieStore cookieStore) {
        StringBuilder sb = new StringBuilder();
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static void saveCookie(Long taskId, BasicCookieStore cookieStore) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        CheckUtils.checkNotNull(cookieStore, "cookieStore is null");
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = CookieUtils.getCookies(cookieStore);
        redisService.cache(RedisKeyPrefixEnum.TASK_COOKIE, String.valueOf(taskId), list);
    }

    public static BasicCookieStore getCookie(Long taskId) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        BasicCookieStore cookieStore = new BasicCookieStore();
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies = null;
        String cacheKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId + "");
        String json = redisService.getString(cacheKey);
        if (StringUtils.isNoneBlank(json)) {
            cookies = JSON.parseObject(json, new TypeReference<List<Cookie>>() {
            });
        }
        if (null == cookies || cookies.isEmpty()) {
            return cookieStore;
        }
        for (com.datatrees.rawdatacentral.domain.vo.Cookie myCookie : cookies) {
            cookieStore.addCookie(CookieUtils.getBasicClientCookie(myCookie));
        }
        return cookieStore;
    }

    public static String getCookieValue(Long taskId, String cookieName) {
        BasicCookieStore cookieStore = getCookie(taskId);
        if (null != cookieStore && null != cookieStore.getCookies()) {
            for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
                if (StringUtils.equals(cookieName, cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        logger.warn("not found cookieName={}", cookieName);
        return null;
    }
}
