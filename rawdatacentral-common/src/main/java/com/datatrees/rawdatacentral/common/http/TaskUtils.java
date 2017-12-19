package com.datatrees.rawdatacentral.common.http;

import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.datatrees.rawdatacentral.common.utils.BackRedisUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CollectionUtils;
import com.datatrees.rawdatacentral.common.utils.RedisUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.constant.HttpHeadKey;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.enums.RedisKeyPrefixEnum;
import com.datatrees.rawdatacentral.domain.enums.StepEnum;
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
                    continue;
                }
                String domain = cookie.getDomain();
                if (domain.length() != host.length()) {
                    domain = "." + domain;
                    cookie.setDomain(domain);
                    cookie.getAttribs().put("domain", domain);
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

    public static Map<String, String> getResponseCookieMap(Request request, List<Cookie> cookies) {
        Map<String, String> requestCookies = request.getRequestCookies();
        Map<String, String> receiveCookieMap = new HashMap<>();
        if (CollectionUtils.isEmpty(cookies)) {
            return receiveCookieMap;
        }
        for (Cookie cookie : cookies) {
            if (!requestCookies.containsKey(cookie.getName())) {
                receiveCookieMap.put(cookie.getName(), cookie.getValue());
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
        if (null == taskId || StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            logger.error("invalid param taskId={},name={},value={}", taskId, name, value);
            return;
        }
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

        RedisUtils.hset(redisKey, AttributeKey.WEBSITE_NAME, websiteName);

        RedisUtils.expire(redisKey, RedisKeyPrefixEnum.TASK_SHARE.toSeconds());
        logger.info("initTaskShare success taskId={},websiteName={}", taskId, websiteName);
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

    public static void updateCookies(Long taskId, Map<String, String> newCookieMap) {
        List<Cookie> cookies = TaskUtils.getCookies(taskId);
        if (CollectionUtils.isNotEmpty(newCookieMap) && CollectionUtils.isNotEmpty(cookies)) {
            for (Map.Entry<String, String> entry : newCookieMap.entrySet()) {
                Cookie find = null;
                for (Cookie cookie : cookies) {
                    if (StringUtils.equals(entry.getKey(), cookie.getName())) {
                        find = cookie;
                        break;
                    }
                }
                if (null == find) {
                    logger.info("新增了cookie,but没有处理,taskId={},name={},value={}", taskId, entry.getKey(), entry.getValue());
                    continue;
                }
                if (!StringUtils.equals(entry.getKey(), find.getValue())) {
                    logger.info("变更了cookie,taskId={},name={},value:{}-->{}", taskId, entry.getKey(), find.getValue(), entry.getValue());
                    find.setValue(entry.getValue());
                    continue;
                }
            }
        }
        TaskUtils.saveCookie(taskId, cookies);
    }

    /**
     * 记录任务阶段
     * @param taskId
     * @param stepEnum
     */
    public static void addStep(Long taskId, StepEnum stepEnum) {
        addTaskShare(taskId, AttributeKey.STEP_CODE, stepEnum.getStepCode() + "");
        addTaskShare(taskId, AttributeKey.STEP_NAME, stepEnum.getStepName() + "");
    }

    /**
     * 获取环境变量
     * @return
     */
    public static String getSassEnv() {
        return System.getProperty(AttributeKey.SAAS_ENV, "none");
    }

    public static String getSassEnv(String postfix) {
        return new StringBuilder(TaskUtils.getSassEnv()).append(".").append(postfix).toString();
    }

    public static boolean stepCheck(Long taskId, StepEnum stepEnum) {
        String stepCode = TaskUtils.getTaskShare(taskId, AttributeKey.STEP_CODE);
        return StringUtils.equals(stepCode, stepEnum.getStepCode() + "");
    }

    public static boolean isSuccess(int crawlerStatus, String checkStatus) {
        return crawlerStatus == 100 && StringUtils.equalsAny(checkStatus, "正常", "没有配置检查项目");
    }

    public static boolean isFail(int crawlerStatus, String checkStatus) {
        return crawlerStatus < 0 || StringUtils.equals(checkStatus, "严重");
    }

    public static long getCoreSize(long taskId) {
        String redisKey = RedisKeyPrefixEnum.TASK_RESULT.getRedisKey(taskId);
        Map<String, String> map = BackRedisUtils.hgetAll(redisKey);
        String coreRedisKey = null;
        if (map.containsKey("callDetails")) {
            coreRedisKey = map.get("callDetails");
        } else if (map.containsKey("trades")) {
            coreRedisKey = map.get("trades");
        } else if (map.containsKey("bankBills")) {
            coreRedisKey = map.get("bankBills");
        }
        if (StringUtils.isNotBlank(coreRedisKey)) {
            return BackRedisUtils.llen(coreRedisKey);
        }
        return 0;

    }
}
