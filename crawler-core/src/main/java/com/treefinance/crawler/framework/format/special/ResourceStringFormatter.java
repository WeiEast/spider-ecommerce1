package com.treefinance.crawler.framework.format.special;

import javax.annotation.Nonnull;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.common.ProcessorFactory;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.service.ServiceBase;
import com.treefinance.crawler.framework.format.CommonFormatter;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class ResourceStringFormatter extends CommonFormatter<String> {

    @Override
    protected String toFormat(@Nonnull String value, String pattern, Request request, Response response) throws Exception {
        String output;
        if (UrlUtils.isUrl(value)) {
            Request newRequest = new Request();
            AbstractProcessorContext processorContext = RequestUtil.getProcessorContext(request);
            RequestUtil.setProcessorContext(newRequest, processorContext);
            RequestUtil.setConf(newRequest, PropertiesConfiguration.getInstance());
            RequestUtil.setCurrentUrl(newRequest, new LinkNode(value));
            Response newResponse = new Response();

            AbstractService service = processorContext.getDefaultService();
            ServiceBase serviceProcessor = ProcessorFactory.getService(service);
            serviceProcessor.invoke(newRequest, newResponse);

            output = StringUtils.defaultString(RequestUtil.getContent(newRequest));
        } else {// html file
            output = value;
        }
        return output;
    }
}
