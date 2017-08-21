/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.crawler.core.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月22日 下午4:34:39
 */
public class IMAPStubclass {
    // Holds the configuration details.
    private static Map<String, String> gtConfigMap = new HashMap<String, String>();

    public IMAPStubclass() {
        initializeExternalConfigFile();
    }

    /**
     * This Functions Read The Configuration Information from gt_mail_config.properties file.
     */
    public void initializeExternalConfigFile() {
        try {
            File file = new File("");
            System.out.println(file.getAbsolutePath());
            FileInputStream inputStream = new FileInputStream(file.getAbsolutePath() + "/mail_config.properties");
            ResourceBundle bundle = new PropertyResourceBundle(inputStream);
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String strKey = (String) keys.nextElement();
                String strData = (String) bundle.getString(strKey);
                gtConfigMap.put(strKey, strData);
            }
        } catch (FileNotFoundException e) {
            System.err.println("External Config File Not Found");
            e.printStackTrace();

        } catch (IOException e) {
            System.err.println("Unable to read external config file");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("External Config properties have not been read");
            e.printStackTrace();
        }

    }

    /**
     * This method demonstrate UPLOADING of mail to specified mail folder using IMAP.
     */
    public static void main(String[] args) {
        IMAPStubclass stubclass = new IMAPStubclass();
        stubclass.uploadMail();

    }

    /**
     * Function Upload mail to specified mail box.
     */
    private void uploadMail() {
        Map<String, String> sendParameters = new HashMap<String, String>();
        sendParameters.put(IMAPMailClient.MAIL_TO, gtConfigMap.get(IMAPMailClient.MAIL_TO));
        sendParameters.put(IMAPMailClient.MAIL_FROM, gtConfigMap.get(IMAPMailClient.MAIL_FROM));
        sendParameters.put(IMAPMailClient.MAIL_CONTENT, gtConfigMap.get(IMAPMailClient.MAIL_CONTENT));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_HOST, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_HOST));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_PORT, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_PORT));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_AUTH_USER, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_AUTH_USER));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_AUTH_PASSWORD, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_AUTH_PASSWORD));
        sendParameters.put(IMAPMailClient.IMAP_UPLOAD_FOLDER_NAME, gtConfigMap.get(IMAPMailClient.IMAP_UPLOAD_FOLDER_NAME));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_SOCKETFACTORY_PORT, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_SOCKETFACTORY_PORT));
        sendParameters.put(IMAPMailClient.MAIL_IMAP_SSL_PROTOCOLS, gtConfigMap.get(IMAPMailClient.MAIL_IMAP_SSL_PROTOCOLS));
        sendParameters.put(IMAPMailClient.MAIL_SUBJECT, gtConfigMap.get(IMAPMailClient.MAIL_SUBJECT));
        IMAPMailClient.uploadMailMessage(sendParameters);
    }
}
