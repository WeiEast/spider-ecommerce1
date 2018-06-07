/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.service.impl;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.CookieParser;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.domain.config.service.impl.GrabService;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/5/26
 */
public class GrabServiceImpl extends ServiceBase<GrabService> {

    private static final Logger logger = LoggerFactory.getLogger(GrabServiceImpl.class);

    public GrabServiceImpl(@Nonnull GrabService service) {
        super(service);
    }

    @Override
    public void process(Request request, Response response) throws Exception {
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        LinkNode linkNode = RequestUtil.getCurrentUrl(request);
        String url = linkNode.getUrl();

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        if (null == taskId || StringUtils.isBlank(websiteName)) {
            logger.error("invalid param,taskId={},websiteName={},url={}", taskId, websiteName, url);
            return;
        }
        Map<String, Object> config = getConfig(linkNode, request);
        if (null == config || config.isEmpty()) {
            logger.error("config is empty!taskId={},websiteName={},url={}", taskId, websiteName, url);
            return;
        }
        String remark = GsonUtils.toJson(config);
        logger.info("start run GrabServiceImpl!taskId={},websiteName={},url={}", taskId, websiteName, url);

        //发送指令
        String resultKey = getMessageService().sendDirective(taskId, DirectiveEnum.GRAB_URL.getCode(), remark);
        //保存状态到redis
        //        DirectiveResult<String> sendDirective = new DirectiveResult<>(DirectiveType.GRAB_URL, taskId);
        //        sendDirective.fill(DirectiveRedisCode.WAIT_APP_DATA, remark);
        //        getRedisService().saveDirectiveResult(sendDirective);

        //等待APP处理完成,并通过dubbo将数据写入redis
        //        String resultKey = sendDirective.getDirectiveKey(DirectiveRedisCode.WAIT_SERVER_PROCESS);

        DirectiveResult<Map<String, String>> receiveDirective = getRedisService().getDirectiveResult(resultKey, 120, TimeUnit.SECONDS);
        if (null == receiveDirective) {
            logger.error("get grab url result timeout,taskId={},websiteName={},resultKey={},url={}", taskId, websiteName, resultKey, url);
            return;
        }
        String cookes = receiveDirective.getData().get(AttributeKey.COOKIES);
        String content = receiveDirective.getData().get(AttributeKey.HTML);
        if (StringUtils.isBlank(cookes)) {
            logger.error("empty cookie return,taskId={},websiteName={},url={}", taskId, websiteName, url);
        }
        if (StringUtils.isBlank(content)) {
            logger.error("empty html return,taskId={},websiteName={},url={}", taskId, websiteName, url);
        }
        if (StringUtils.isNoneBlank(cookes)) {
            logger.info("get cookes success,taskId={},websiteName={},resultKey={},url={},cookes={}", taskId, websiteName, resultKey, url, cookes);
            //ProcessorContextUtil.setCookieString(context, cookes);
        }

        logger.info("get result success,taskId={},websiteName={},resultKey={},url={}", taskId, websiteName, resultKey, url);
        response.setOutPut(content);

        RequestUtil.setContent(request, content);

        ProcessorContextUtil.addThreadLocalLinkNode(context, linkNode);
        ProcessorContextUtil.addThreadLocalResponse(context, response);
    }

    protected Map<String, Object> getConfig(LinkNode linkNode, Request request) {
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        String url = linkNode.getUrl();
        String endUrl = url;
        Map<String, Object> config = new HashMap<>();
        config.put("css", new ArrayList<>());
        config.put("usePCUA", true);
        config.put("js", new ArrayList<>());
        config.put("startUrl", Arrays.asList(url));
        config.put("endUrl", Arrays.asList(endUrl));

        String cookieString = ProcessorContextUtil.getCookieString(context);
        String domain = getDomain(url);
        List<Cookie> cookies = CookieParser.getCookies(domain, ";", "=", cookieString);

        //        logger.info("cookiesS={}", GsonUtils.toJson(cookies));

        Map<String, Object> httpConfig = new HashMap<>();
        httpConfig.put("cookies", cookies);
        httpConfig.put("proxy", "");
        httpConfig.put("header", "");
        httpConfig.put("responseData", Arrays.asList("html", "cookie"));

        config.put("httpConfig", httpConfig);
        config.put("client", "webview");
        config.put("visible", false);
        config.put("visitType", "url");
        return config;
    }

    private String getDomain(String url) {
        if (url.contains("//")) {
            url = StringUtils.substringAfter(url, "//");
        }
        if (url.contains("/")) {
            url = StringUtils.substringBefore(url, "/");
        }
        if (url.contains(":")) {
            url = StringUtils.substringBefore(url, ":");
        }
        return url;
    }

}
