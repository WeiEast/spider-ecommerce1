/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
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

package com.treefinance.crawler.framework.process.operation.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.treefinance.crawler.framework.download.WrappedFile;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.message.Message;
import org.apache.james.mime4j.parser.MimeEntityConfig;
import org.apache.james.mime4j.storage.StorageProvider;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月8日 上午11:39:58
 */
class Mail extends Message {

    private StringBuffer      txtBody     = new StringBuffer();
    private StringBuffer      htmlBody    = new StringBuffer();
    private List<WrappedFile> attachments = new ArrayList<WrappedFile>();
    private String            websiteName;

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
     * @exception IOException
     * @exception MimeIOException
     */
    public Mail(InputStream is, MimeEntityConfig config, StorageProvider storageProvider) throws IOException, MimeIOException {
        super(is, config, storageProvider);
    }

    /**
     * @param is
     * @param config
     * @exception IOException
     * @exception MimeIOException
     */
    public Mail(InputStream is, MimeEntityConfig config) throws IOException, MimeIOException {
        super(is, config);
    }

    /**
     * @param is
     * @exception IOException
     * @exception MimeIOException
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
    public List<WrappedFile> getAttachments() {
        return attachments;
    }

    /**
     * @param attachments the attachments to set
     */
    public void setAttachments(List<WrappedFile> attachments) {
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
