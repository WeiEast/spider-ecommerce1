package com.treefinance.crawler.framework.format.special;

import javax.annotation.Nonnull;
import java.io.File;

import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.treefinance.crawler.framework.download.WrappedFile;
import com.datatrees.crawler.core.processor.common.FileUtils;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class FileFormatter extends CommonFormatter<WrappedFile> {

    @Override
    protected WrappedFile toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        AbstractProcessorContext processorContext = config.getProcessorContext();
        File file = new File(FileUtils.getFileRandomPath(processorContext.getWebsiteName()));
        WrappedFile wrappedFile = new WrappedFile(file);
        Object result = config.getSourceFieldValue("fileName");
        wrappedFile.setName(result != null ? result.toString() : file.getName());

        if (UrlUtils.isUrl(value)) {
            String cookie = ProcessorContextUtil.getCookieString(processorContext);
            ProtocolInput input = new ProtocolInput().setUrl(value).setFollowRedirect(true).setCookie(cookie);
            wrappedFile.setInput(input);
            // set input async to get file while needed
        } else {
            wrappedFile.write(value.getBytes(CharsetUtil.UTF_8_NAME));
            wrappedFile.setMimeType("text/html");
            wrappedFile.setCharSet("UTF-8");
            wrappedFile.setSourceURL(config.getCurrentUrl());
        }

        return wrappedFile;
    }

}
