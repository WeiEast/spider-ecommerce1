package com.datatrees.crawler.core.processor.service.impl;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskHttpServiceImpl extends ServiceBase {

    private static final Logger logger = LoggerFactory.getLogger(TaskHttpServiceImpl.class);

    public static void main(String[] args) {
        String temp = "http://jl.189.cn/service/bill/queryBillInfoFra.action";

        String url = StringUtils.substringBefore(temp, "\"");
        String body = StringUtils.substringAfter(temp, "\"");
        System.out.println(url);
        System.out.println(body);

    }

    @Override
    public void process(Request request, Response response) throws Exception {
        SearchProcessorContext context = (SearchProcessorContext) RequestUtil.getProcessorContext(request);
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        String websiteName = context.getWebsiteName();

        LinkNode linkNode = RequestUtil.getCurrentUrl(request);
        String linkUrl = linkNode.getUrl();
        String referer = linkNode.getReferer();

        if (null == taskId || StringUtils.isBlank(websiteName)) {
            logger.error("invalid param,taskId={},websiteName={},linkUrl={}", taskId, websiteName, linkUrl);
            return;
        }
        if (StringUtils.isBlank(linkUrl)) {
            logger.error("url is blan,taskId={},websiteName={},linkUrl={}", taskId, websiteName, linkUrl);
            return;
        }
        RequestType requestType = StringUtils.contains(linkUrl, "\"") ? RequestType.POST : RequestType.GET;
        String url = StringUtils.substringBefore(linkUrl, "\"");
        String requestBody = StringUtils.substringAfter(linkUrl, "\"");

        TaskHttpClient client = TaskHttpClient.create(taskId, websiteName, requestType, "", true).setReferer(referer)
                .addHeaders(linkNode.getHeaders()).setUrl(url);
        if (StringUtils.isNotBlank(requestBody)) {
            client.setRequestBody(requestBody);
        }
        com.datatrees.rawdatacentral.domain.vo.Response invoke = client.invoke();
        logger.info("task http service do taskId={},websiteName={},linkUrl={},status={}", taskId, websiteName, linkUrl, invoke.getStatusCode());

        String content = invoke.getPageContent();
        ResponseUtil.setResponseContent(response, content);
        RequestUtil.setContent(request, content);

        ProcessorContextUtil.addThreadLocalLinkNode(context, linkNode);
        ProcessorContextUtil.addThreadLocalResponse(context, response);

        String cookieString = TaskUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(context, cookieString);

    }

}
