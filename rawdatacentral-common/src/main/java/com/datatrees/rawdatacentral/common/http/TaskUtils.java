package com.datatrees.rawdatacentral.common.http;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.vo.Cookie;
import com.datatrees.rawdatacentral.domain.vo.Request;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/7/19.
 */
public class TaskUtils {

    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    public static Cookie toCrawlerCookie(ClientCookie from) {
        String domain = from.getDomain();
        if (domain.startsWith(".")) {
            domain = domain.substring(1);
        }
        Cookie to = new Cookie();
        to.setDomain(domain);
        to.setPath(from.getPath());
        to.setName(from.getName());
        to.setValue(from.getValue());
        to.setSecure(from.isSecure());
        to.setVersion(from.getVersion());
        to.setExpiryDate(from.getExpiryDate());
        if (from.containsAttribute("domain")) {
            to.getAttribs().put("domain", domain);
        }
        if (from.containsAttribute("path")) {
            to.getAttribs().put("path", from.getAttribute("path"));
        }
        if (from.containsAttribute("expires")) {
            to.getAttribs().put("expires", from.getAttribute("expires"));
        }
        if (from.containsAttribute("httponly")) {
            to.getAttribs().put("httponly", from.getAttribute("httponly"));
        }
        return to;
    }

    public static List<Cookie> getCookies(Long taskId, String host, BasicCookieStore cookieStore, CloseableHttpResponse httpResponse) {
        CheckUtils.checkNotNull(cookieStore, "cookieStore is null");
        List<Cookie> list = new ArrayList<>();
        List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
        for (org.apache.http.cookie.Cookie cookie : cookies) {
            list.add(toCrawlerCookie((BasicClientCookie) cookie));
        }
        //更新自定义cookie
        Header[] headers = httpResponse.getHeaders(HttpHeadKey.SET_COOKIE);
        if (null != headers && headers.length > 0) {
            for (Header header : headers) {
                String headerValue = header.getValue();
                HttpCookie httpCookie = HttpCookie.parse(headerValue).get(0);
                Cookie orignCookie = findCookie(host, httpCookie, list);
                if (null != orignCookie) {
                    if (!StringUtils.equals(orignCookie.getValue(), httpCookie.getValue())) {
                        logger.info("更新cookie,taskId={},cookeName={},domain={},update value {}-->{}", taskId, orignCookie.getName(),
                                orignCookie.getDomain(), orignCookie.getValue(), httpCookie.getValue());
                        orignCookie.setValue(httpCookie.getValue());
                    }
                } else {
                    String domain = StringUtils.isBlank(httpCookie.getDomain()) ? host : httpCookie.getDomain();
                    if (StringUtils.startsWith(domain, ".")) {
                        domain = domain.substring(1);
                    }
                    Cookie cookie = new Cookie();
                    cookie.setName(httpCookie.getName());
                    cookie.setValue(httpCookie.getValue());
                    cookie.setDomain(domain);
                    cookie.setPath(httpCookie.getPath());
                    cookie.setVersion(httpCookie.getVersion());
                    cookie.setSecure(httpCookie.getSecure());
                    cookie.getAttribs().put("domain", domain);
                    cookie.getAttribs().put("path", httpCookie.getPath());
                    long maxAge = httpCookie.getMaxAge();
                    if (maxAge <= 0) {
                        maxAge = TimeUnit.MINUTES.toSeconds(30);
                    }
                    cookie.setExpiryDate(new Date(System.currentTimeMillis() + maxAge * 1000));
                    list.add(cookie);
                    logger.info("新增 rejected cookie, taskId={},cookeName={},value={},domain={}", taskId, cookie.getName(), cookie.getValue(), domain);
                }
            }
        }
        return list;
    }

    public static List<Cookie> getCookies(Long taskId) {
        List<Cookie> list = new ArrayList<>();
        String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId);
        Map<String, String> map = RedisUtils.hgetAll(redisKey);
        if (CollectionUtils.isNotEmpty(map)) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Cookie cookie = JSON.parseObject(entry.getValue(), new TypeReference<Cookie>() {});
                list.add(cookie);
            }
        }
        return list;
    }

    public static List<Cookie> getCookies(Long taskId, String host) {
        CheckUtils.checkNotPositiveNumber(taskId, ErrorCode.EMPTY_TASK_ID);
        List<Cookie> list = getCookies(taskId);
        if (CollectionUtils.isNotEmpty(list)) {
            Iterator<Cookie> iterator = list.iterator();
            while (iterator.hasNext()) {
                Cookie cookie = iterator.next();
                if (!StringUtils.endsWith(host, cookie.getDomain())) {
                    iterator.remove();
                }
            }
        }
        return list;
    }

    public static List<Cookie> getCookies(Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies) {
        List<Cookie> list = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cookies)) {
            for (com.gargoylesoftware.htmlunit.util.Cookie htmlCookie : cookies) {
                list.add(toCrawlerCookie((ClientCookie) (htmlCookie.toHttpClient())));
            }
        }
        return list;
    }

    public static String getCookieString(Long taskId) {
        StringBuilder sb = new StringBuilder();
        List<Cookie> list = getCookies(taskId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Cookie cookie : list) {
                sb.append(";").append(cookie.getName()).append("=").append(cookie.getValue());
            }
        }
        if (StringUtils.isBlank(sb)) {
            return "";
        }
        return sb.substring(1);
    }

    public static Map<String, String> getResponseCookieMap(Long taskId, String host, Request request, CloseableHttpResponse response,
            BasicCookieStore cookieStore) {
        Map<String, String> requestCookies = request.getRequestCookies();
        Map<String, String> receiveCookieMap = new HashMap<>();
        List<Cookie> cookies = getCookies(taskId, host, cookieStore, response);
        if (CollectionUtils.isEmpty(cookies)) {
            return receiveCookieMap;
        }
        if (CollectionUtils.isNotEmpty(requestCookies)) {

            for (Cookie cookie : cookies) {
                if (!requestCookies.containsKey(cookie.getName())) {
                    receiveCookieMap.put(cookie.getName(), cookie.getValue());
                }
            }
        }
        return receiveCookieMap;
    }

    public static Map<String, String> getCookieMap(List<Cookie> cookies) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
        return map;
    }

    public static void saveCookie(long taskId, Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies) {
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = TaskUtils.getCookies(cookies);
        saveCookie(taskId, list);
    }

    public static void saveCookie(long taskId, String host, BasicCookieStore cookieStore, CloseableHttpResponse httpResponse) {
        List<com.datatrees.rawdatacentral.domain.vo.Cookie> list = TaskUtils.getCookies(taskId, host, cookieStore, httpResponse);
        saveCookie(taskId, list);
    }

    public static void saveCookie(long taskId, List<com.datatrees.rawdatacentral.domain.vo.Cookie> cookies) {
        if (CollectionUtils.isEmpty(cookies)) {
            return;
        }
        String redisKey = RedisKeyPrefixEnum.TASK_COOKIE.getRedisKey(taskId);
        for (com.datatrees.rawdatacentral.domain.vo.Cookie cookie : cookies) {
            String name = "[" + cookie.getName() + "][" + cookie.getDomain() + "]";
            RedisUtils.hset(redisKey, name, JSON.toJSONString(cookie, SerializerFeature.DisableCircularReferenceDetect),
                    RedisKeyPrefixEnum.TASK_COOKIE.toSeconds());

        }
    }

    public static BasicClientCookie getBasicClientCookie(Cookie cookie) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
        basicClientCookie.setDomain(cookie.getDomain());
        basicClientCookie.setPath(cookie.getPath());
        basicClientCookie.setSecure(cookie.isSecure());
        basicClientCookie.setVersion(cookie.getVersion());
        basicClientCookie.setExpiryDate(cookie.getExpiryDate());
        for (Map.Entry<String, String> entry : cookie.getAttribs().entrySet()) {
            basicClientCookie.setAttribute(entry.getKey(), entry.getValue());
        }
        return basicClientCookie;
    }

    public static BasicCookieStore buildBasicCookieStore(List<Cookie> list) {
        BasicCookieStore cookieStore = new BasicCookieStore();
        if (CollectionUtils.isNotEmpty(list)) {
            for (Cookie cookie : list) {
                cookieStore.addCookie(TaskUtils.getBasicClientCookie(cookie));
            }
        }
        return cookieStore;
    }

    public static String getCookieValue(Long taskId, String cookieName) {
        List<Cookie> list = getCookies(taskId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (Cookie cookie : list) {
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
        RedisUtils.hset(redisKey, name, value, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
        logger.info("addTaskShare success taskId={},name={}", taskId, name);
    }

    /**
     * 获取共享属性
     * @param taskId
     * @param name
     */
    public static String getTaskShare(Long taskId, String name) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        if (!RedisUtils.hexists(redisKey, name)) {
            logger.warn("property not found, redisKey={},name={}", redisKey, name);
            return null;
        }
        return RedisUtils.hget(redisKey, name);
    }

    /**
     * 删除共享属性
     * @param taskId
     * @param name
     */
    public static void removeTaskShare(Long taskId, String name) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        if (!RedisUtils.hexists(redisKey, name)) {
            logger.warn("property not found, redisKey={},name={}", redisKey, name);
            return;
        }
        RedisUtils.hdel(redisKey, name);
        logger.info("removeTaskShare success taskId={},name={}", taskId, name);
    }

    /**
     * 获取共享属性
     * @param taskId
     */
    public static Map<String, String> getTaskShares(Long taskId) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);
        Map<String, String> map = RedisUtils.hgetAll(redisKey);
        return map;
    }

    /**
     * 初始化共享信息
     * @param taskId
     * @param websiteName
     */
    public static void initTaskShare(Long taskId, String websiteName) {
        String redisKey = RedisKeyPrefixEnum.TASK_SHARE.getRedisKey(taskId);

        RedisUtils.hset(redisKey, AttributeKey.FIRST_VISIT_WEBSITENAME, websiteName);

        boolean isNewOperator = isNewOperator(websiteName);
        RedisUtils.hset(redisKey, AttributeKey.IS_NEW_OPERATOR, String.valueOf(isNewOperator));

        String realWebsiteName = getRealWebsiteName(websiteName);
        RedisUtils.hset(redisKey, AttributeKey.WEBSITE_NAME, realWebsiteName);

        RedisUtils.expire(redisKey, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());

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

    public static Cookie findCookie(String host, HttpCookie httpCookie, List<Cookie> list) {
        String domain = StringUtils.isBlank(httpCookie.getDomain()) ? host : httpCookie.getDomain();
        if (StringUtils.startsWith(domain, ".")) {
            domain = domain.substring(1);
        }
        for (Cookie cookie : list) {
            if (StringUtils.equals(httpCookie.getName(), cookie.getName()) && StringUtils.endsWith(domain, cookie.getDomain())) {
                return cookie;
            }
        }
        return null;
    }

}
