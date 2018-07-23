/**
 * This document and its contents are protected by copyright 2005 and owned by Treefinance.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.bean;

import java.io.*;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.protocol.ProtocolInput;
import com.datatrees.common.protocol.ProtocolOutput;
import com.datatrees.common.protocol.WebClientUtil;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月9日 下午4:10:25
 */
public class FileWapper {

    private static final Logger        logger                    = LoggerFactory.getLogger(FileWapper.class);

    private              int           sleepSecond               = PropertiesConfiguration.getInstance().getInt("default.sleep.seconds", 2000);

    private              int           retryCount                = PropertiesConfiguration.getInstance()
            .getInt("default.file.download.retry.count", 3);

    private              String        textFileNameSuffixPattern = PropertiesConfiguration.getInstance()
            .get("text.filename.suffix.pattern", "htm$|html$|txt$");

    private              String        textMimeTypePattern       = PropertiesConfiguration.getInstance().get("text.mimeType.pattern", "^text/");

    private              String        name;

    private              String        mimeType;

    private              String        charSet;

    private              long          size;

    private              File          file;

    private              String        sourceURL;

    private              ProtocolInput input;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * @return the file
     * @exception FileNotFoundException
     * @exception InterruptedException
     */
    public FileInputStream getFileInputStream() throws FileNotFoundException, InterruptedException {
        for (int i = 0; i < retryCount; i++) {
            if (file.length() == 0 && input != null) {
                logger.info("do file download input url:" + input.getUrl());
                OutputStream output = new FileOutputStream(file);
                try {
                    ProtocolOutput out = WebClientUtil.getFileClient().getProtocolOutput(input);
                    this.setMimeType(out.getContent().getMimeType());
                    this.setSourceURL(input.getUrl());
                    this.setSize(file.length());
                    if (this.needDetectContent()) {
                        IOUtils.write(out.getContent().detectContentAsString().getBytes("UTF-8"), output);
                    } else {
                        IOUtils.write(out.getContent().getContent(), output);
                    }
                    break;
                } catch (Exception e) {
                    logger.error("async get file input stream error " + e.getMessage());
                    long sleepMillis = sleepSecond + (int) (Math.random() * sleepSecond) * (i + 1);
                    logger.info("download failed do retry " + (i + 1) + ",sleep " + sleepMillis + "ms...");
                    Thread.sleep(sleepMillis);
                } finally {
                    IOUtils.closeQuietly(output);
                }
            } else {
                break;
            }
        }
        return file.exists() ? new FileInputStream(file) : null;
    }

    public boolean needDetectContent() {
        return (this.getMimeType() != null && RegExp.find(this.getMimeType(), textMimeTypePattern)) ||
                (name != null && RegExp.find(name, textFileNameSuffixPattern));
    }

    /**
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the sourceURL
     */
    public String getSourceURL() {
        return sourceURL;
    }

    /**
     * @param sourceURL the sourceURL to set
     */
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    /**
     * @param charSet the charSet to set
     */
    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }

    /**
     * @return the input
     */
    public ProtocolInput getInput() {
        return input;
    }

    /**
     * @param input the input to set
     */
    public void setInput(ProtocolInput input) {
        this.input = input;
    }

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public void remove() {
        FileUtils.deleteQuietly(file);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FileWapper [name=" + name + ", mimeType=" + mimeType + ", charSet=" + charSet + ", size=" + size + ", file=" + file + ", sourceURL=" +
                sourceURL + "]";
    }

}
