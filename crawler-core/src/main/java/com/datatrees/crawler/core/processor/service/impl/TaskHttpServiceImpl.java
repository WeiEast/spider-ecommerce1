package com.datatrees.crawler.core.processor.service.impl;

import javax.annotation.Nonnull;

import com.datatrees.crawler.core.domain.config.properties.Properties;
import com.datatrees.crawler.core.domain.config.service.impl.TaskHttpService;
import com.datatrees.crawler.core.processor.SearchProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.bean.Status;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.datatrees.spider.share.common.http.TaskHttpClient;
import com.datatrees.spider.share.common.utils.CollectionUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.RequestType;
import com.treefinance.crawler.framework.context.function.SpiderRequest;
import com.treefinance.crawler.framework.context.function.SpiderResponse;
import org.apache.commons.lang3.StringUtils;

public class TaskHttpServiceImpl extends ServiceBase<TaskHttpService> {

    public TaskHttpServiceImpl(@Nonnull TaskHttpService service) {
        super(service);
    }

    @Override
    public void process(@Nonnull SpiderRequest request, @Nonnull SpiderResponse response) throws Exception {
        SearchProcessorContext context = (SearchProcessorContext) request.getProcessorContext();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        String websiteName = context.getWebsiteName();
        Properties properties = context.getSearchConfig().getProperties();
        String encoding = null;
        if (null != properties) {
            encoding = properties.getEncoding();
        }

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

        TaskHttpClient client = TaskHttpClient.create(taskId, websiteName, requestType, true).setReferer(referer).setUrl(url);
        if (CollectionUtils.isNotEmpty(context.getDefaultHeader())) {
            client.addHeaders(context.getDefaultHeader());
        }
        if (CollectionUtils.isNotEmpty(linkNode.getHeaders())) {
            client.addHeaders(linkNode.getHeaders());
        }
        if (StringUtils.isNotBlank(requestBody)) {
            client.setRequestBody(requestBody);
        }
        if (StringUtils.isNotBlank(encoding)) {
            client.setDefaultResponseCharset(encoding);
        }
        com.datatrees.spider.share.domain.http.Response invoke = client.invoke();
        logger.info("task http service do taskId={},websiteName={},linkUrl={},status={}", taskId, websiteName, linkUrl, invoke.getStatusCode());

        String content = invoke.getPageContent();
        response.setOutPut(content);

        response.setStatus(Status.VISIT_SUCCESS);
        RequestUtil.setContent(request, content);

        ProcessorContextUtil.addThreadLocalLinkNode(context, linkNode);
        ProcessorContextUtil.addThreadLocalResponse(context, response);

        String cookieString = TaskUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(context, cookieString);

    }

}
