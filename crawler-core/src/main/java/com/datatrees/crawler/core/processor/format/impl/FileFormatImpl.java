/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.format.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.util.UrlUtils;
import com.datatrees.common.util.DateUtils;
import com.datatrees.crawler.core.domain.Website;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.FileUtils;
import com.datatrees.crawler.core.processor.common.ProcessorContextUtil;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.SourceUtil;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午1:11:32
 */
public class FileFormatImpl extends AbstractFormat {

    private static final Logger logger = LoggerFactory.getLogger(FileFormatImpl.class);

    @Deprecated
    private boolean mkPluginDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    @Deprecated
    private String getAbsolutePath(String siteName) {
        String filePathPrfix = conf.get(Constants.DOWNLOAD_FILE_STORE_PATH, "/tmp/file/");
        String timeString = DateUtils.baseFormatDateTime(new Date(), "yyyyMMdd");
        return filePathPrfix + timeString + "/" + siteName + "/" + UUID.randomUUID();
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        OutputStream output = null;
        try {
            FileWapper fileWapper = new FileWapper();
            Website website = RequestUtil.getProcessorContext(req).getWebsite();
            File file = new File(FileUtils.getFileRandomPath(website.getWebsiteName()));
            output = new FileOutputStream(file);
            if (UrlUtils.isUrl(orginal)) {
                String cookie = ProcessorContextUtil.getCookieString(RequestUtil.getProcessorContext(req));
                ProtocolInput input = new ProtocolInput().setUrl(orginal).setFollowRedirect(true).setCookie(cookie);
                fileWapper.setInput(input);
                // set input async to get file while needed
            } else {// html file
                IOUtils.write(orginal.getBytes("UTF-8"), output);
                fileWapper.setMimeType("text/html");
                fileWapper.setCharSet("UTF-8");
                fileWapper.setSourceURL(RequestUtil.getCurrentUrl(req).getUrl());
            }
            fileWapper.setSize(file.length());

            Object result = SourceUtil.getSourceMap("fileName", req, response);
            fileWapper.setName(result != null ? result.toString() : file.getName());

            fileWapper.setFile(file);
            if (logger.isDebugEnabled()) {
                logger.debug("file format result " + fileWapper);
            }
            return fileWapper;
        } catch (IOException e) {
            logger.error("File format error", e);
        } finally {
            IOUtils.closeQuietly(output);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.datatrees.crawler.core.processor.format.AbstractFormat#isResultType(java.lang.Object)
     */
    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof FileWapper) {
            return true;
        } else {
            return false;
        }
    }
}
