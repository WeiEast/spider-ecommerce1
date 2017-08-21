package com.datatrees.crawler.core.mail;

/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2016
 */

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月22日 下午5:56:22
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class ImapProtocolImpl extends Authenticator {
    public Session session;
    public PasswordAuthentication authentication;

    public ImapProtocolImpl(String username, String password) throws GeneralSecurityException {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        // props.setProperty("mail.imap.host", "imap.qq.com");
        // props.setProperty("mail.imap.host", "imap-mail.outlook.com");
        props.setProperty("mail.imap.host", "imap.163.com");
        // props.setProperty("mail.imap.port", "993");

        props.setProperty("mail.imap.port", "143");
//        props.setProperty("mail.imap.auth.login.disable", "true");
//        MailSSLSocketFactory sf = new MailSSLSocketFactory();
//        sf.setTrustAllHosts(true);
//        props.put("mail.imap.ssl.enable", "true");
//        props.put("mail.imap.ssl.socketFactory", sf);
        // props.put("mail.smtp.ssl.enable", "true");
        // props.put("mail.smtp.ssl.socketFactory", sf);
        // props.setProperty("mail.transport.protocol", "smtp");
        // props.put("mail.smtp.host", "smtp.qq.com");
        // props.put("mail.smtp.port", "465");
        // props.put("mail.smtp.auth", "true");

        authentication = new PasswordAuthentication(username, password);
        session = Session.getInstance(props, this);
        session.setDebug(true);
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return this.authentication;
    }


    public void sendSMTPemail() throws IOException, AddressException, MessagingException, GeneralSecurityException {
        Message message = new MimeMessage(session, new FileInputStream("/Users/wangcheng/51back/51files/7.16/crawler_lib/招商银行信用卡电子账单-.eml"));
        Transport.send(message);
    }

    public void connect() throws FileNotFoundException {
        Store store = null;
        try {
            store = session.getStore();
            store.connect();
            Folder root = store.getDefaultFolder();
            Folder inbox = root.getFolder("inbox");
            inbox.open(Folder.READ_WRITE);
            System.out.println(inbox.getMessageCount());
            Message message = new MimeMessage(session, new FileInputStream("/Users/wangcheng/51back/51files/7.16/crawler_lib/招商银行信用卡电子账单-.eml"));
            inbox.appendMessages(new Message[] {message});
            inbox.close(true);
        } catch (MessagingException e) {
            try {
                byte[] buf = e.getMessage().getBytes("ISO-8859-1");
                System.out.println(new String(buf, "GBK"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException("登录失败", e);
        }
    }

    /**
     * @throws MessagingException
     * @throws FileNotFoundException
     * 
     */
    public static void main(String[] args) throws Exception, MessagingException {
        // ImapProtocolImpl imapProtocolImpl = new ImapProtocolImpl("3323187495",
        // "mmgcdgyhyjpdcied");
        // ImapProtocolImpl imapProtocolImpl = new ImapProtocolImpl("593237554",
        // "nqglprfhmkpobcba");
        ImapProtocolImpl imapProtocolImpl = new ImapProtocolImpl("m15068820568_2@163.com", "a12345");

        // ImapProtocolImpl imapProtocolImpl = new ImapProtocolImpl("wangchun_27@hotmail.com",
        // "13651621532");
        imapProtocolImpl.connect();

        // imapProtocolImpl.sendSMTPemail();
    }
}
