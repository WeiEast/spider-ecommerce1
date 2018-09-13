/*
 * Copyright © 2015 - 2018 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datatrees.spider.bank.plugin.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.spider.share.common.utils.BeanFactoryUtils;
import com.datatrees.spider.share.common.utils.CheckUtils;
import com.datatrees.spider.share.common.utils.TaskUtils;
import com.datatrees.spider.share.common.utils.TemplateUtils;
import com.datatrees.spider.share.domain.AttributeKey;
import com.datatrees.spider.share.domain.CommonPluginParam;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.http.HttpResult;
import com.datatrees.spider.share.service.CommonPluginService;
import com.datatrees.spider.share.service.MonitorService;
import com.treefinance.crawler.framework.context.AbstractProcessorContext;
import com.treefinance.crawler.framework.context.ProcessorContextUtil;
import com.treefinance.crawler.framework.extension.plugin.AbstractClientPlugin;
import com.treefinance.crawler.framework.extension.plugin.PluginConstants;
import com.treefinance.crawler.framework.extension.plugin.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 爬取过程中校验
 */
public class DefineCheckPlugin extends AbstractClientPlugin {

    private static final Logger         logger = LoggerFactory.getLogger(DefineCheckPlugin.class);

    private              MonitorService monitorService;

    private              String         fromType;

    @Override
    public String process(String... args) throws Exception {
        monitorService = BeanFactoryUtils.getBean(MonitorService.class);
        CommonPluginService pluginService = BeanFactoryUtils.getBean(CommonPluginService.class);
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        Map<String, Object> pluginResult = new HashMap<>();

        String websiteName = context.getWebsiteName();
        Long taskId = context.getTaskId();

        TaskUtils.updateCookies(taskId, context.getCookiesAsMap());

        TaskUtils.initTaskContext(taskId, context.getContext());
        Map<String, String> map = JSON.parseObject(args[args.length - 1], new TypeReference<Map<String, String>>() {});
        fromType = map.get(AttributeKey.FORM_TYPE);
        CheckUtils.checkNotBlank(fromType, "fromType is empty");
        logger.info("自定义插件-->启动-->成功,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);
        monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->启动-->成功", FormType.getName(fromType)));

        CommonPluginParam param = new CommonPluginParam(fromType, taskId, websiteName);
        param.setArgs(Arrays.copyOf(args, args.length - 1));
        param.getExtral().putAll(context.getContext());

        HttpResult<Object> result = pluginService.defineProcess(param);
        if (result.getStatus()) {
            pluginResult.put(PluginConstants.FIELD, result.getData());
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->处理-->成功", FormType.getName(fromType)));
        } else {
            monitorService.sendTaskLog(taskId, TemplateUtils.format("{}-->处理-->失败", FormType.getName(fromType)));
        }
        String cookieString = TaskUtils.getCookieString(taskId);
        context.setCookies(cookieString);

        Map<String, String> shares = TaskUtils.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            context.setString(entry.getKey(), entry.getValue());
        }
        return JSON.toJSONString(pluginResult);
    }

}
