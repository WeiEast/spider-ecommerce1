package com.treefinance.crawler.framework.format.special;

import javax.annotation.Nonnull;

import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.domain.config.service.AbstractService;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import com.treefinance.crawler.framework.util.ServiceUtils;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class ResourceStringFormatter extends CommonFormatter<String> {

    @Override
    protected String toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String output;
        if (UrlUtils.isUrl(value)) {
            LinkNode linkNode = new LinkNode(value);
            AbstractProcessorContext processorContext = config.getProcessorContext();
            AbstractService service = processorContext.getDefaultService();
            output = ServiceUtils.invokeAsString(service, linkNode, processorContext, null, null);
        } else {// html file
            output = value;
        }
        return output;
    }
}
