package com.datatrees.rawdatacentral.common.http;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.datatrees.rawdatacentral.common.constants.RedisDataType;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.Cookie;
import com.datatrees.rawdatacentral.domain.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
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

    public static void updateBasicCookieStore(Long taskId, String host, BasicCookieStore cookieStore, CloseableHttpResponse httpResponse) {
        if (null == httpResponse || null == cookieStore) {
            return;
        }
        Header[] headers = httpResponse.getHeaders(HttpHeadKey.SET_COOKIE);
        if (null != headers && headers.length > 0) {
            List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
            for (Header header : headers) {
                String headerValue = header.getValue();
                List<HttpCookie> list = HttpCookie.parse(headerValue);
                HttpCookie httpCookie = list.get(0);
                String domain = httpCookie.getDomain();
                //目前只有非.开头的才会被拒绝(甘肃移动登录时)
                if (StringUtils.isBlank(domain) || domain.startsWith(".") || StringUtils.equals(domain, host)) {
                    continue;
                }
                org.apache.http.cookie.Cookie find = null;
                if (CollectionUtils.isNotEmpty(cookies)) {
                    for (org.apache.http.cookie.Cookie b : cookies) {
                        if (StringUtils.equals(b.getName(), httpCookie.getName())) {
                            find = b;
                            break;
                        }
                    }
                    if (null == find) {
                        //改变domain
                        domain = "." + domain;
                        //被拒绝了,.ResponseProcessCookies][processCookies][123] Cookie rejected
                        BasicClientCookie cookie = new BasicClientCookie(httpCookie.getName(), httpCookie.getValue());
                        cookie.setDomain(domain);
                        cookie.setPath(httpCookie.getPath());
                        cookie.setVersion(httpCookie.getVersion());
                        cookie.setSecure(httpCookie.getSecure());
                        cookie.setAttribute("domain", domain);
                        cookie.setAttribute("path", httpCookie.getPath());
                        cookieStore.addCookie(cookie);
                        logger.info("add rejected cookie taskId={},cookeName={}", taskId, cookie.getName());
                    }
                }
            }
        }
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

    public static Map<String, String> getReceiveCookieMap(Response response, BasicCookieStore cookieStore) {
        Map<String, String> map = getCookieMap(cookieStore);
        if (CollectionUtils.isEmpty(map)) {
            return map;
        }

        Map<String, String> sendCookies = response.getRequest().getRequestCookies();
        if (CollectionUtils.isNotEmpty(sendCookies)) {
            for (Map.Entry<String, String> entry : sendCookies.entrySet()) {
                if (map.containsKey(entry.getKey())) {
                    map.remove(entry.getKey());
                }
            }
        }
        return map;
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

    public static Map<String, String> getCookieMap(BasicCookieStore cookieStore) {
        Map<String, String> map = null;
        if (null != cookieStore && null != cookieStore.getCookies()) {
            map = new HashMap<>();
            for (org.apache.http.cookie.Cookie cookie : cookieStore.getCookies()) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
        return map;
    }

    public static void saveCookie(long taskId, BasicCookieStore cookieStore) {
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = TaskUtils.getCookies(cookieStore);
        String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals(type, RedisDataType.STRING)) {
            if (CollectionUtils.isNotEmpty(list)) {
                String json = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
                RedisUtils.setex(RedisKeyPrefixEnum.TASK_COOKIE, taskId, json);
            }
        } else {
            if (CollectionUtils.isNotEmpty(list)) {
                for (com.datatrees.rawdatacentral.domain.vo.Cookie cookie : list) {
                    String name = cookie.getName() + ":" + cookie.getDomain();
                    RedisUtils.hset(redisKey, name, JSON.toJSONString(cookie, SerializerFeature.DisableCircularReferenceDetect),
                            RedisKeyPrefixEnum.TASK_COOKIE.toSeconds());

                }
            }
        }
    }

    public static BasicCookieStore getCookie(Long taskId) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID);
        BasicCookieStore cookieStore = new BasicCookieStore();
        String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals(type, RedisDataType.STRING)) {
            if (RedisUtils.exists(redisKey)) {
                String json = RedisUtils.get(redisKey);
                List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies = JSON.parseObject(json, new TypeReference<List<Cookie>>() {});
                for (com.datatrees.rawdatacentral.domain.vo.Cookie myCookie : cookies) {
                    cookieStore.addCookie(TaskUtils.getBasicClientCookie(myCookie));
                }
            }
        } else {
            Map<String, String> map = RedisUtils.hgetAll(redisKey);
            if (CollectionUtils.isNotEmpty(map)) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    com.datatrees.rawdatacentral.domain.vo.Cookie myCookie = JSON.parseObject(entry.getValue(), new TypeReference<Cookie>() {});
                    cookieStore.addCookie(TaskUtils.getBasicClientCookie(myCookie));
                }
            }
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
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, String> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(name, value);
            RedisUtils.setex(RedisKeyPrefixEnum.TASK_SHARE, taskId, JSON.toJSONString(map));
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
        if (StringUtils.equals(RedisDataType.NONE, type)) {
            logger.warn("redis key not found redisKey={}", redisKey);
            return null;
        }
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            Map<String, String> map = getTaskShares(taskId);
            if (null == map || map.isEmpty()) {
                logger.warn("redis key is empty, redisKey={}", redisKey);
                return null;
            }
            if (!map.containsKey(name)) {
                logger.warn("property not found, redisKey={},name={}", redisKey, name);
                return null;
            }
            return map.get(name);
        } else {
            if (!RedisUtils.hexists(redisKey, name)) {
                logger.warn("property not found, redisKey={},name={}", redisKey, name);
                return null;
            }
            return RedisUtils.hget(redisKey, name);
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
        if (StringUtils.equals(RedisDataType.NONE, type)) {
            logger.warn("redis key not found redisKey={}", redisKey);
            return;
        }
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, String> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null != map && map.containsKey(name)) {
                map.remove(name);
                RedisUtils.setex(RedisKeyPrefixEnum.TASK_SHARE, taskId, JSON.toJSONString(map));
            }
            if (!map.containsKey(name)) {
                logger.warn("property not found, redisKey={},name={}", redisKey, name);
                return;
            }
            RedisUtils.unLock(redisKey);
        } else {
            if (!RedisUtils.hexists(redisKey, name)) {
                logger.warn("property not found, redisKey={},name={}", redisKey, name);
                return;
            }
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
        if (StringUtils.equals(RedisDataType.NONE, type)) {
            logger.warn("redis key not found redisKey={}", redisKey);
            return null;
        }
        if (StringUtils.equals(RedisDataType.STRING, type)) {
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
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, Object> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, Object>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(name, value);
            RedisUtils.setex(RedisKeyPrefixEnum.TASK_RESULT, taskId, JSON.toJSONString(map));
            RedisUtils.unLock(redisKey);
        } else {
            if (value instanceof String) {
                RedisUtils.hset(redisKey, name, value.toString());
            } else {
                RedisUtils.hset(redisKey, name, JSON.toJSONString(value));
            }
            RedisUtils.expire(redisKey, RedisKeyPrefixEnum.TASK_RESULT.toSeconds());
        }
        logger.info("addTaskResult success taskId={},name={}", taskId, name);
    }

    /**
     * 获取任务结果
     * @param taskId
     */
    public static Map<String, Object> getTaskResult(Long taskId) {
        Map<String, Object> json = new HashMap<>();
        Map<String, String> map = null;
        String redisKey = RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals(RedisDataType.NONE, type)) {
            logger.warn("redis key not found redisKey={}", redisKey);
            return json;
        }
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
        } else {
            map = RedisUtils.hgetAll(redisKey);
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.startsWith(entry.getValue(), "[")) {
                json.put(entry.getKey(), JSON.parseArray(entry.getValue()));
            } else if (StringUtils.startsWith(entry.getValue(), "{")) {
                json.put(entry.getKey(), JSON.parseObject(entry.getValue()));
            } else {
                json.put(entry.getKey(), entry.getValue());
            }
        }
        return json;
    }

    /**
     * 初始化共享信息
     * @param taskId
     * @param websiteName
     */
    public static void initTaskShare(Long taskId, String websiteName) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        String type = RedisUtils.type(redisKey);
        if (StringUtils.equals(RedisDataType.STRING, type)) {
            RedisUtils.lockFailThrowException(redisKey, 5);
            Map<String, String> map = JSON.parseObject(RedisUtils.get(redisKey), new TypeReference<Map<String, String>>() {});
            if (null == map) {
                map = new HashMap<>();
            }
            map.put(AttributeKey.FIRST_VISIT_WEBSITENAME, websiteName);

            boolean isNewOperator = isNewOperator(websiteName);
            map.put(AttributeKey.IS_NEW_OPERATOR, String.valueOf(isNewOperator));

            String realWebsiteName = getRealWebsiteName(websiteName);
            map.put(AttributeKey.WEBSITE_NAME, realWebsiteName);

            RedisUtils.setex(RedisKeyPrefixEnum.TASK_SHARE, taskId, JSON.toJSONString(map));
            RedisUtils.unLock(redisKey);
        } else {
            RedisUtils.hset(redisKey, AttributeKey.FIRST_VISIT_WEBSITENAME, websiteName);

            boolean isNewOperator = isNewOperator(websiteName);
            RedisUtils.hset(redisKey, AttributeKey.IS_NEW_OPERATOR, String.valueOf(isNewOperator));

            String realWebsiteName = getRealWebsiteName(websiteName);
            RedisUtils.hset(redisKey, AttributeKey.WEBSITE_NAME, realWebsiteName);

            RedisUtils.expire(redisKey, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
        }

        logger.info("initTaskShare success taskId={},websiteName={}", taskId, websiteName);

        RedisUtils.setex(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME, taskId, websiteName);
        RedisUtils.setex(RedisKeyPrefixEnum.TASK_FIRST_VISIT_WEBSITENAME, taskId, websiteName);
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
        String firstVisitWebsiteName = RedisUtils.get(RedisKeyPrefixEnum.TASK_FIRST_VISIT_WEBSITENAME.getRedisKey(taskId), 3);
        //兼容老的
        if (StringUtils.isBlank(firstVisitWebsiteName)) {
            firstVisitWebsiteName = RedisUtils.get(RedisKeyPrefixEnum.WEBSITE_OPERATOR_RENAME.getRedisKey(taskId));
        }
        return firstVisitWebsiteName;
    }

    public static void initTaskContext(Long taskId, Map<String, Object> context) {
        String redisKey = RedisKeyPrefixEnum.TASK_CONTEXT.getRedisKey(taskId);
        if (null == context && context.isEmpty()) {
            logger.warn("initTaskContext fail,context is empty,taskId={}", taskId);
            return;
        }
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            if (entry.getValue() instanceof String) {
                RedisUtils.hset(redisKey, entry.getKey(), entry.getValue().toString());
            } else {
                RedisUtils.hset(redisKey, entry.getKey(), JSON.toJSONString(entry.getValue()));
            }
            RedisUtils.expire(redisKey, RedisKeyPrefixEnum.TASK_CONTEXT.toSeconds());
        }
    }

    public static String getTaskContext(Long taskId, String name) {
        String redisKey = RedisKeyPrefixEnum.TASK_CONTEXT.getRedisKey(taskId);
        Map<String, String> map = RedisUtils.hgetAll(redisKey);
        if (null == map || !map.containsKey(name)) {
            return null;
        }
        return map.get(name);
    }

}
