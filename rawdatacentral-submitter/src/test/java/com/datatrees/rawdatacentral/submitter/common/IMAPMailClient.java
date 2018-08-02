/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.submitter.common;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.security.Security;
import java.util.Properties;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月22日 下午4:33:47
 */
public class IMAPMailClient {

    public static Folder getQQInbox(String host, String username, String password) throws MessagingException {
        int addProvider = Security.addProvider(null);
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        Properties props = System.getProperties();
        // props.put("proxySet", "true");
        // props.put("socksProxyPort", "1089");
        // props.put("socksProxyHost", "172.16.128.86"); //sock5代理服务器地址

        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");

        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.socketFactory.port", "993");

        Session session = Session.getDefaultInstance(props, null);

        // session.setDebug(true);

        Store store = session.getStore("imap");// 郵件服務器

        store.connect(host, username, password);

        Folder folder = store.getFolder("inbox");
        folder.open(Folder.READ_WRITE);
        return folder;
    }

}
