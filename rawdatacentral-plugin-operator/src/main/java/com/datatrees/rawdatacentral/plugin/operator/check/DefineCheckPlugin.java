package com.datatrees.rawdatacentral.plugin.operator.check;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.common.util.ThreadInterruptedUtil;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.exception.ResultEmptyException;
import com.datatrees.crawler.core.processor.plugin.AbstractClientPlugin;
import com.datatrees.crawler.core.processor.plugin.PluginConstants;
import com.datatrees.crawler.core.processor.plugin.PluginFactory;
import com.datatrees.rawdatacentral.api.CrawlerOperatorService;
import com.datatrees.rawdatacentral.api.MessageService;
import com.datatrees.rawdatacentral.api.RedisService;
import com.datatrees.rawdatacentral.common.utils.BeanFactoryUtils;
import com.datatrees.rawdatacentral.common.utils.CheckUtils;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.common.utils.TemplateUtils;
import com.datatrees.rawdatacentral.domain.constant.AttributeKey;
import com.datatrees.rawdatacentral.domain.enums.DirectiveEnum;
import com.datatrees.rawdatacentral.domain.enums.ErrorCode;
import com.datatrees.rawdatacentral.domain.exception.CommonException;
import com.datatrees.rawdatacentral.domain.operator.OperatorParam;
import com.datatrees.rawdatacentral.domain.result.DirectiveResult;
import com.datatrees.rawdatacentral.domain.result.HttpResult;

/**
 * 爬取过程中校验
 * Created by zhouxinghai on 2017/7/31
 */
public class DefineCheckPlugin extends AbstractClientPlugin {

    private static final Logger      logger        = LoggerFactory.getLogger(DefineCheckPlugin.class);

    private CrawlerOperatorService   pluginService = BeanFactoryUtils.getBean(CrawlerOperatorService.class);

    private RedisService             redisService  = BeanFactoryUtils.getBean(RedisService.class);

    private AbstractProcessorContext context       = PluginFactory.getProcessorContext();

    private String                   fromType;

    private Map<String, String>      pluginResult  = new HashMap<>();

    @Override
    public String process(String... args) throws Exception {
        String websiteName = context.getWebsiteName();
        Long taskId = context.getLong(AttributeKey.TASK_ID);
        Map<String, String> map = JSON.parseObject(args[args.length - 1], new TypeReference<Map<String, String>>() {
        });
        fromType = map.get(AttributeKey.FORM_TYPE);
        CheckUtils.checkNotBlank(fromType, "fromType is empty");
        logger.info("自定义插件启动,taskId={},websiteName={},fromType={}", taskId, websiteName, fromType);

        OperatorParam param = new OperatorParam(fromType, taskId, websiteName);
        param.setArgs(Arrays.copyOf(args, args.length - 1));
        param.getExtral().putAll(context.getContext());

        HttpResult<Object> result = pluginService.defineProcess(param);
        if (result.getStatus()) {
            pluginResult.put(PluginConstants.FIELD, JSON.toJSONString(result.getData()));
        }
        String cookieString = CookieUtils.getCookieString(taskId);
        ProcessorContextUtil.setCookieString(context, cookieString);

        Map<String, String> shares = redisService.getTaskShares(taskId);
        for (Map.Entry<String, String> entry : shares.entrySet()) {
            context.setString(entry.getKey(), entry.getValue());
        }

        return JSON.toJSONString(pluginResult);
    }

}
