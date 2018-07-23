package com.datatrees.rawdatacentral.plugin.common.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.api.CommonPluginApi;
import com.datatrees.rawdatacentral.api.MonitorService;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.plugin.CommonPluginParam;
import com.datatrees.spider.share.domain.FormType;
import com.datatrees.spider.share.domain.HttpResult;
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
        CommonPluginApi pluginService = BeanFactoryUtils.getBean(CommonPluginApi.class);
        AbstractProcessorContext context = PluginFactory.getProcessorContext();
        Map<String, Object> pluginResult = new HashMap<>();

        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);

        TaskUtils.updateCookies(taskId, ProcessorContextUtil.getCookieMap(context));

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
        ProcessorContextUtil.setCookieString(context, cookieString);

        Map<String, String> shares = TaskUtils.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            context.setString(entry.getKey(), entry.getValue());
        }
        return JSON.toJSONString(pluginResult);
    }

}
