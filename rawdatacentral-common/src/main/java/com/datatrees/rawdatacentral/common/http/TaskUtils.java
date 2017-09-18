package com.datatrees.rawdatacentral.common.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/7/19.
 */
public class TaskUtils {

    private static final Logger       logger       = LoggerFactory.getLogger(TaskUtils.class);
    private static       RedisService redisService = BeanFactoryUtils.getBean(RedisService.class);

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
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = TaskUtils.getCookies(cookieStore);
        redisService.cache(RedisKeyPrefixEnum.TASK_COOKIE, String.valueOf(taskId), list);
    }

    public static BasicCookieStore getCookie(Long taskId) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        BasicCookieStore cookieStore = new BasicCookieStore();
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies = null;
        String cacheKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId + "");
        String json = redisService.getString(cacheKey);
        if (StringUtils.isNoneBlank(json)) {
            cookies = JSON.parseObject(json, new TypeReference<List<Cookie>>() {});
        }
        if (null == cookies || cookies.isEmpty()) {
            return cookieStore;
        }
        for (com.datatrees.rawdatacentral.domain.vo.Cookie myCookie : cookies) {
            cookieStore.addCookie(TaskUtils.getBasicClientCookie(myCookie));
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

    /**
     * 添加共享属性
     * @param taskId
     * @param name
     * @param value
     */
    public static void addTaskShare(Long taskId, String name, String value) {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        boolean lock = redisService.lock(taskId);
        while (!lock && System.currentTimeMillis() < endTime) {
            lock = redisService.lock(taskId);
        }
        if (!lock) {
            throw new RuntimeException("lock error taskId=" + taskId);
        }
        String cacheKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        Map<String, String> map = redisService.getCache(cacheKey, new TypeReference<Map<String, String>>() {});
        if (null == map) {
            map = new HashMap<>();
        }
        map.put(name, value);
        redisService.cache(cacheKey, map, RedisKeyPrefixEnum.TASK_SHARE.getTimeout(), RedisKeyPrefixEnum.TASK_SHARE.getTimeUnit());
        redisService.unLock(taskId);
    }

    /**
     * 获取共享属性
     * @param taskId
     * @param name
     */
    public static String getTaskShare(Long taskId, String name) {
        Map<String, String> map = getTaskShares(taskId);
        if (null == map || map.isEmpty()) {
            return null;
        }
        return map.get(name);
    }

    /**
     * 删除共享属性
     * @param taskId
     * @param name
     */
    public static void removeTaskShare(Long taskId, String name) {
        long endTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5);
        boolean lock = redisService.lock(taskId);
        while (!lock && System.currentTimeMillis() < endTime) {
            lock = redisService.lock(taskId);
        }
        if (!lock) {
            throw new RuntimeException("lock error taskId=" + taskId);
        }
        String cacheKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        Map<String, String> map = redisService.getCache(cacheKey, new TypeReference<Map<String, String>>() {});
        if (null != map && map.containsKey(name)) {
            map.remove(name);
            redisService.cache(cacheKey, map, RedisKeyPrefixEnum.TASK_SHARE.getTimeout(), RedisKeyPrefixEnum.TASK_SHARE.getTimeUnit());
        }
        redisService.unLock(taskId);
    }

    /**
     * 获取共享属性
     * @param taskId
     */
    public static Map<String, String> getTaskShares(Long taskId) {
        String cacheKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        Map<String, String> map = redisService.getCache(cacheKey, new TypeReference<Map<String, String>>() {});
        return map;
    }

    public static void initTaskContext(Long taskId, Map<String, Object> context) {
        redisService.cache(RedisKeyPrefixEnum.TASK_CONTEXT, taskId, context);
    }

    public static String getTaskContext(Long taskId, String name) {
        Map<String, Object> context = redisService.getCache(RedisKeyPrefixEnum.TASK_CONTEXT, taskId, new TypeReference<Map<String, Object>>() {});
        if (null == context || !context.containsKey(name)) {
            return null;
        }
        return context.get(name).toString();
    }

}
