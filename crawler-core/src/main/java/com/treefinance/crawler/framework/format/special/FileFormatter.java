package com.treefinance.crawler.framework.format.special;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.crawler.core.processor.AbstractProcessorContext;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.FileUtils;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import org.apache.commons.io.IOUtils;

/**
 * @author Jerry
 * @since 00:43 2018/6/2
 */
public class FileFormatter extends CommonFormatter<FileWapper> {

    @Override
    protected FileWapper toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        OutputStream output = null;
        try {
            FileWapper fileWapper = new FileWapper();
            AbstractProcessorContext processorContext = config.getProcessorContext();
            File file = new File(FileUtils.getFileRandomPath(processorContext.getWebsiteName()));
            output = new FileOutputStream(file);
            if (UrlUtils.isUrl(value)) {
                String cookie = ProcessorContextUtil.getCookieString(processorContext);
                ProtocolInput input = new ProtocolInput().setUrl(value).setFollowRedirect(true).setCookie(cookie);
                fileWapper.setInput(input);
                // set input async to get file while needed
            } else {// html file
                IOUtils.write(value.getBytes(CharsetUtil.UTF_8_NAME), output);
                fileWapper.setMimeType("text/html");
                fileWapper.setCharSet("UTF-8");
                fileWapper.setSourceURL(config.getCurrentUrl());
            }
            fileWapper.setSize(file.length());

            Object result = config.getSourceFieldValue();
            fileWapper.setName(result != null ? result.toString() : file.getName());

            fileWapper.setFile(file);
            logger.debug("file format result : {}", fileWapper);

            return fileWapper;
        } catch (IOException e) {
            logger.error("File format error", e);
        } finally {
            IOUtils.closeQuietly(output);
        }

        return null;
    }

}
