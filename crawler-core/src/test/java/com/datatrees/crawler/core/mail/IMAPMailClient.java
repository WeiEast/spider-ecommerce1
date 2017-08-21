/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */
package com.datatrees.crawler.core.mail;

import java.io.FileInputStream;
import java.security.Security;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月22日 下午4:33:47
 */
public class IMAPMailClient {
    /*
     * More configurable parameter information can be obtain from
     * http://technology-related.com/products
     * /javamail/javadocs/com/sun/mail/imap/package-summary.html
     */
    public static final String MAIL_CONTENT = "mail.body";
    public static final String MAIL_SUBJECT = "mail.subject";
    public static final String MAIL_CC = "mail.cc";
    public static final String MAIL_TO = "mail.to";

    public static final String MAIL_IMAP_AUTH_PLAIN_DISABLE = "mail.imap.auth.plain.disable";

    public static final String MAIL_IMAP_AUTHENTICATION_REQUIRED = "imap_authentication_required";

    public static final String MAIL_IMAP_PORT = "mail.imap.port";

    public static final String MAIL_FROM = "mail.from";

    public static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";

    public static final String MAIL_IMAP_HOST = "mail.imap.host";

    public static final String IMAP_UPLOAD_FOLDER_NAME = "imap.upload.mail.folder";

    public static final String MAIL_IMAP_DEBUG = "mail.imap.debug";// "mail.imap.debug","true"

    public static final String MAIL_IMAP_SSL_PROTOCOLS = "mail.imap.ssl.protocols";// SSL

    public static final String MAIL_IMAP_SOCKETFACTORY_PORT = "mail.imap.socketFactory.port";// SSL


    public static final String MAIL_IMAP_AUTH_USER = "mail.imap.user";
    public static final String MAIL_IMAP_AUTH_PASSWORD = "mail.imap.password";

    /**
     * sendMailMessage uses javax.mail APIs for sending mails. Sends mail to e-mail address
     * mentioned in mailTo params.
     *
     * @param mapMailInfo
     * @return -1 if mailing fails.
     */
    /**
     * @param mapMailInfo
     * @return
     */
    public static int uploadMailMessage(Map<?, ?> mapMailInfo) {

        int mailSentStatus = 0;

        String strMailHost = (String) mapMailInfo.get(MAIL_IMAP_HOST);

        String strMailPort = (String) mapMailInfo.get(MAIL_IMAP_PORT);

        if (strMailPort == null || "".equals(strMailPort.trim())) {
            strMailPort = "993"; // default port
        }

        String strProtocol = (String) mapMailInfo.get(MAIL_STORE_PROTOCOL);

        if (strProtocol == null || "".equals(strProtocol.trim())) {
            strProtocol = "imap"; // default protocol
        }
        String strAuthenticate = (String) mapMailInfo.get(MAIL_IMAP_AUTH_PLAIN_DISABLE);

        if (strAuthenticate == null || "".equals(strAuthenticate.trim())) {
            strAuthenticate = "true";
        }


        String strImapAuthUser = (String) mapMailInfo.get(MAIL_IMAP_AUTH_USER);

        String strImapAuthPassword = (String) mapMailInfo.get(MAIL_IMAP_AUTH_PASSWORD);


        String strMailFrom = (String) mapMailInfo.get(MAIL_FROM);

        // expect list of recipients instead...

        String strMailTo = (String) mapMailInfo.get(MAIL_TO);

        String strCC = (String) mapMailInfo.get(MAIL_CC);


        String strMailSubject = (String) mapMailInfo.get(MAIL_SUBJECT);

        if (strMailSubject == null) {
            strMailSubject = "";
        }

        String strMailMessage = (String) mapMailInfo.get(MAIL_CONTENT);

        String strImapDebug = (String) mapMailInfo.get(MAIL_IMAP_DEBUG);
        if (strImapDebug == null || "".equals(strImapDebug.trim())) {
            strImapDebug = "true";
        }

        String strImapSSLProtocols = (String) mapMailInfo.get(MAIL_IMAP_SSL_PROTOCOLS);
        if (strImapSSLProtocols == null || "".equals(strImapSSLProtocols.trim())) {
            strImapSSLProtocols = "SSL";
        }

        String strImapSocketFactoryPort = (String) mapMailInfo.get(MAIL_IMAP_SOCKETFACTORY_PORT);
        if (strImapSocketFactoryPort == null || "".equals(strImapSocketFactoryPort.trim())) {
            strImapSocketFactoryPort = "993";
        }

        String isAuthenicationRequired = (String) mapMailInfo.get(MAIL_IMAP_AUTHENTICATION_REQUIRED);
        if (isAuthenicationRequired == null || "".equals(isAuthenicationRequired.trim())) {
            isAuthenicationRequired = "true";
        }

        String strImapUploadFolder = (String) mapMailInfo.get(IMAP_UPLOAD_FOLDER_NAME);
        if (strImapUploadFolder == null || "".equals(strImapUploadFolder.trim())) {
            strImapUploadFolder = "inbox";
        }

        int msgReturn = -1;
        try {
            Properties properties = new Properties();
            properties.put(MAIL_IMAP_HOST, strMailHost);
            properties.put(MAIL_STORE_PROTOCOL, strProtocol);
            properties.put(MAIL_IMAP_PORT, strMailPort);
            properties.put(MAIL_IMAP_AUTH_PLAIN_DISABLE, strAuthenticate);

            properties.setProperty(MAIL_IMAP_DEBUG, strImapDebug);

            properties.setProperty(MAIL_IMAP_SSL_PROTOCOLS, strImapSSLProtocols);
            properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.setProperty("mail.imap.socketFactory.fallback", "false");

            properties.setProperty(MAIL_IMAP_SOCKETFACTORY_PORT, strImapSocketFactoryPort);

            Session session = null;
            if (isAuthenicationRequired.equalsIgnoreCase("true")) {
                Authenticator auth = new IMAPAuthenticator(strImapAuthUser, strImapAuthPassword);
                session = Session.getInstance(properties, auth);
            } else {
                session = Session.getInstance(properties, null);
            }

            Message message = new MimeMessage(session);

            String[] arrMailTo = strMailTo.split(",");

            InternetAddress[] addresses = new InternetAddress[arrMailTo.length];
            for (int i = 0; i < arrMailTo.length; i++) {
                addresses[i] = new InternetAddress(arrMailTo[i].trim());
            }
            message.setRecipients(Message.RecipientType.TO, addresses);

            if (strCC != null) {
                String[] arrMailCc = strCC.split(",");
                InternetAddress[] ccTo = new InternetAddress[arrMailCc.length];

                for (int i = 0; i < arrMailCc.length; i++) {
                    ccTo[i] = new InternetAddress(arrMailCc[i].trim());
                }
                message.setRecipients(Message.RecipientType.CC, ccTo);
            }

            message.setFrom(new InternetAddress(strMailFrom));
            message.setSubject(strMailSubject);
            // following setting required to support UTF-8 //characterset
            message.setContent(strMailMessage, "text/html; charset=utf-8");
            message.setHeader("Content-Type", "text/plain; charset=\"utf-8\"");
            message.setHeader("Content-Transfer-Encoding", "quoted-printable");


            // create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(strMailMessage);

            session.setDebug(true);

            Store store = session.getStore(strProtocol);
            store.connect();

            Folder inbox = store.getFolder(strImapUploadFolder);
            inbox.open(Folder.READ_WRITE);
            inbox.appendMessages(new Message[] {message});
            inbox.close(true);

        } catch (Exception e) {
            mailSentStatus = -1;
            System.out.println("Exception:Error in Uploading Message File");
            e.printStackTrace();
            return msgReturn;
        }
        return mailSentStatus;

    }

    static class IMAPAuthenticator extends Authenticator {
        String username = "";
        String password = "";

        public IMAPAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    }


    @Test
    public void testMailUpload() throws Exception {
        Properties properties = new Properties();
        properties.put(MAIL_IMAP_HOST, "imap.qq.com");
        properties.put(MAIL_STORE_PROTOCOL, "imap");
        properties.put(MAIL_IMAP_PORT, "993");
        properties.put(MAIL_IMAP_AUTH_PLAIN_DISABLE, "true");
        properties.setProperty(MAIL_IMAP_DEBUG, "true");
        properties.setProperty(MAIL_IMAP_SSL_PROTOCOLS, "SSL");
        properties.setProperty(MAIL_IMAP_SOCKETFACTORY_PORT, "993");
        properties.setProperty("mail.imap.auth.login.disable", "true");


        IMAPAuthenticator auth = new IMAPAuthenticator("593237554", "nqglprfhmkpobcba");
        Session session = Session.getInstance(properties, auth);
        Message message = new MimeMessage(session, new FileInputStream("【广发卡01月账单】名品半价抢兑，不止5折！-.eml"));

        session.setDebug(true);
        // properties.setProperty("mail.imap.socketFactory.class",
        // "javax.net.ssl.SSLSocketFactory");
        // properties.setProperty("mail.imap.socketFactory.fallback", "false");
        // properties.setProperty("mail.imap.partialfetch", "false");
        // properties.setProperty("mail.mime.multipart.ignoreexistingboundaryparameter", "true");

        Store store = session.getStore("imap");
        store.connect();

        Folder inbox = store.getFolder("inbox");
        inbox.open(Folder.READ_WRITE);
        inbox.appendMessages(new Message[] {message});
        inbox.close(true);

    }


    @Test
    public void test() throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        String host = "imap.qq.com"; // 主机名/ip

        String username = "593237554"; // 用戶名

        String password = "hmjgtmrdnemtbebc"; // 密碼


        Properties props = System.getProperties();
        // props.put("proxySet", "true");
        // props.put("socksProxyPort", "1089");
        // props.put("socksProxyHost", "172.16.128.86"); //sock5代理服务器地址

        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");

        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.socketFactory.port", "993");

        // props.put("mail.pop3.auth", "true");

        Session session = Session.getDefaultInstance(props, null);

        session.setDebug(true);

        Store store = session.getStore("imap");// 郵件服務器

        store.connect(host, username, password);

        Folder folder = store.getFolder("inbox");

        // folder.open(Folder.READ_ONLY);
        folder.open(Folder.READ_WRITE);

        Message message[] = folder.getMessages(); // 獲得郵件

        System.out.println("Messages's　length:　" + message.length + "  " + message[0].getSubject());

        folder.close(true);
    }
    
    
    
    
    @Test
    public void test2() throws MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        String host = "imap.163.com"; // 主机名/ip

        String username = "15068820568"; // 用戶名

        String password = "abc2730834"; // 密碼


        Properties props = System.getProperties();
        // props.put("proxySet", "true");
        // props.put("socksProxyPort", "1089");
        // props.put("socksProxyHost", "172.16.128.86"); //sock5代理服务器地址

        props.setProperty("mail.imap.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.imap.socketFactory.fallback", "false");

        props.setProperty("mail.imap.port", "993");
        props.setProperty("mail.imap.socketFactory.port", "993");

        // props.put("mail.pop3.auth", "true");

        Session session = Session.getDefaultInstance(props, null);

        session.setDebug(true);

        Store store = session.getStore("imap");// 郵件服務器

        store.connect(host, username, password);

        Folder folder = store.getFolder("INBOX");

        // folder.open(Folder.READ_ONLY);
        folder.open(Folder.READ_WRITE);

        Message message[] = folder.getMessages(); // 獲得郵件

        System.out.println("Messages's　length:　" + message.length + "  " + message[0].getSubject());

        folder.close(true);
    }

}
