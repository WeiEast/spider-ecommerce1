package com.datatrees.rawdatacentral.common.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
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

    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

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
        if (null != list && !list.isEmpty()) {
            String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
            RedisUtils.set(RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId), json, RedisKeyPrefixEnum.TASK_COOKIE.toSeconds());
        }
    }

    public static BasicCookieStore getCookie(Long taskId) {
        CheckUtils.checkNotNull(taskId, "taskId is null");
        BasicCookieStore cookieStore = new BasicCookieStore();
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies = null;
        String cacheKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId + "");
        String json = null;
        if (RedisUtils.exists(cacheKey)) {
            json = RedisUtils.get(cacheKey);
        }
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
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, String> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(name, value);
            RedisUtils.set(redisKey, JSON.toJSONString(map), RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
            RedisUtils.unLock(redisKey);
        } else {
            RedisUtils.hset(redisKey, name, value, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
        }
        logger.info("addTaskShare success taskId={},name={}", taskId, name);
    }

    /**
     * 获取共享属性
     * @param taskId
     * @param name
     */
    public static String getTaskShare(Long taskId, String name) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            Map<String, String> map = getTaskShares(taskId);
            if (null == map || map.isEmpty()) {
                return null;
            }
            return map.get(name);
        } else {
            return RedisUtils.getJedis().hget(redisKey, name);
        }
    }

    /**
     * 删除共享属性
     * @param taskId
     * @param name
     */
    public static void removeTaskShare(Long taskId, String name) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, String> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null != map && map.containsKey(name)) {
                map.remove(name);
                RedisUtils.set(redisKey, JSON.toJSONString(map), RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
            }
            RedisUtils.unLock(redisKey);
        } else {
            RedisUtils.hdel(redisKey, name);
        }
        logger.info("removeTaskShare success taskId={},name={}", taskId, name);
    }

    /**
     * 获取共享属性
     * @param taskId
     */
    public static Map<String, String> getTaskShares(Long taskId) {
        Map<String, String> map = null;
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            return map;
        } else {
            map = RedisUtils.hgetAll(redisKey);
        }
        return map;
    }

    /**
     * 添加任务结果
     * @param taskId
     * @param name
     * @param value
     */
    public static void addTaskResult(Long taskId, String name, Object value) {
        String redisKey = RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, Object> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, Object>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(name, value);
            RedisUtils.set(redisKey, JSON.toJSONString(map), RedisKeyPrefixEnum.TASK_RESULT.toSeconds());
            RedisUtils.unLock(redisKey);
        } else {
            if (value instanceof String) {
                RedisUtils.hset(redisKey, name, value.toString());
            } else {
                RedisUtils.hset(redisKey, name, JSON.toJSONString(value));
            }
            RedisUtils.getJedis().expire(redisKey, RedisKeyPrefixEnum.TASK_RESULT.toSeconds());
        }
        logger.info("addTaskResult success taskId={},name={}", taskId, name);
    }

    /**
     * 获取任务结果
     * @param taskId
     */
    public static Map<String, String> getTaskResult(Long taskId) {
        Map<String, String> map = null;
        String redisKey = RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, Object>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
        } else {
            map = RedisUtils.getJedis().hgetAll(redisKey);
        }
        return map;
    }

    /**
     * 初始化共享信息
     * @param taskId
     * @param websiteName
     */
    public static void initTaskShare(Long taskId, String websiteName) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals("string", type)) {
            redisService.lockFailThrowException(redisKey);
            Map<String, Object> map = redisService.getCache(redisKey, new TypeReference<Map<String, Object>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(AttributeKey.FIRST_VISIT_WEBSITENAME, websiteName);

            boolean isNewOperator = isNewOperator(websiteName);
            map.put(AttributeKey.IS_NEW_OPERATOR, isNewOperator);

            String realWebsiteName = getRealWebsiteName(websiteName);
            map.put(AttributeKey.WEBSITE_NAME, realWebsiteName);

            redisService.cache(RedisKeyPrefixEnum.TASK_SHARE, taskId, map);
            redisService.unLock(redisKey);
        } else {
            RedisUtils.hset(redisKey, AttributeKey.FIRST_VISIT_WEBSITENAME, websiteName);

            boolean isNewOperator = isNewOperator(websiteName);
            RedisUtils.hset(redisKey, AttributeKey.IS_NEW_OPERATOR, String.valueOf(isNewOperator));

            String realWebsiteName = getRealWebsiteName(websiteName);
            RedisUtils.hset(redisKey, AttributeKey.WEBSITE_NAME, realWebsiteName);

            RedisUtils.getJedis().expire(redisKey, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
        }

        logger.info("initTaskShare success taskId={},websiteName={}", taskId, websiteName);

        redisService.saveString(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME, taskId, websiteName);
        redisService.saveString(RedisKeyPrefixEnum.TASK_FIRST_VISIT_WEBSITENAME, taskId, websiteName);

    }

    /**
     * 获取真实的websiteName
     * @param websiteName 可能是伪装的websiteName
     * @return
     */
    public static String getRealWebsiteName(String websiteName) {
        if (StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix())) {
            return RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.parsePostfix(websiteName);
        }
        return websiteName;
    }

    /**
     * 是否独立运营商
     * @param websiteName
     * @return
     */
    public static boolean isNewOperator(String websiteName) {
        return StringUtils.startsWith(websiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix());
    }

    /**
     * 是否独立运营商
     * @param taskId
     * @return
     */
    public static boolean isNewOperator(Long taskId) {
        //获取第一次消息用的websiteName
        String firstVisitWebsiteName = getFirstVisitWebsiteName(taskId);
        //是否是独立运营商
        Boolean isNewOperator = StringUtils.startsWith(firstVisitWebsiteName, RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getPrefix());
        return isNewOperator;
    }

    /**
     * 获取第一次访问websiteName
     * @param taskId
     * @return
     */
    public static String getFirstVisitWebsiteName(Long taskId) {
        String firstVisitWebsiteName = redisService
                .getString(RedisKeyPrefixEnum.TASK_FIRST_VISIT_WEBSITENAME.getRedisKey(taskId), 3, TimeUnit.SECONDS);
        //兼容老的
        if (StringUtils.isBlank(firstVisitWebsiteName)) {
            firstVisitWebsiteName = redisService.getString(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getRedisKey(taskId));
        }
        return firstVisitWebsiteName;
    }

}
