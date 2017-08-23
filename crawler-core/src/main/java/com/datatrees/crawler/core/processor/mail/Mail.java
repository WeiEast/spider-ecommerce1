/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.datatrees.crawler.core.processor.bean.FileWapper;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.james.mime4j.storage.StorageProvider;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月8日 上午11:39:58
 */
public class Mail extends Message {

    private StringBuffer     txtBody     = new StringBuffer();
    private StringBuffer     htmlBody    = new StringBuffer();
    private List<FileWapper> attachments = new ArrayList<FileWapper>();
    private String websiteName;

    /**
     *
     */
    public Mail() {
        super();
    }

    /**
     * @param is
     * @param config
     * @param storageProvider
     * @throws IOException
     * @throws MimeIOException
     */
    public Mail(InputStream is, MimeEntityConfig config, StorageProvider storageProvider) throws IOException, MimeIOException {
        super(is, config, storageProvider);
    }

    /**
     * @param is
     * @param config
     * @throws IOException
     * @throws MimeIOException
     */
    public Mail(InputStream is, MimeEntityConfig config) throws IOException, MimeIOException {
        super(is, config);
    }

    /**
     * @param is
     * @throws IOException
     * @throws MimeIOException
     */
    public Mail(InputStream is) throws IOException, MimeIOException {
        super(is);
    }

    /**
     * @param other
     */
    public Mail(Message other) {
        super(other);
    }

    /**
     * @return the txtBody
     */
    public StringBuffer getTxtBody() {
        return txtBody;
    }

    /**
     * @param txtBody the txtBody to set
     */
    public void setTxtBody(StringBuffer txtBody) {
        this.txtBody = txtBody;
    }

    /**
     * @return the htmlBody
     */
    public StringBuffer getHtmlBody() {
        return htmlBody;
    }

    /**
     * @param htmlBody the htmlBody to set
     */
    public void setHtmlBody(StringBuffer htmlBody) {
        this.htmlBody = htmlBody;
    }

    /**
     * @return the attachments
     */
    public List<FileWapper> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<FileWapper> attachments) {
        this.attachments = attachments;
    }

    /**
     * @return the websiteName
     */
    public String getWebsiteName() {
        return websiteName;
    }

    /**
     * @param websiteName the websiteName to set
     */
    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

}
