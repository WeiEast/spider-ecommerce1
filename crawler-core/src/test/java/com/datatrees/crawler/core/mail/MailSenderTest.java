/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.crawler.core.mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2016年4月22日 下午4:05:23
 */
public class MailSenderTest {

    /**
     * @throws MessagingException
     *
     */
    public void send() throws MessagingException {
        Properties props = new Properties();

        // 开启debug调试
        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", "smtp.qq.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");

        Session session = Session.getInstance(props);

        // 邮件内容部分
        Message msg = new MimeMessage(session);
        msg.setSubject("seenews 错误");
        StringBuilder builder = new StringBuilder();
        builder.append("url = " + "http://blog.csdn.net/never_cxb/article/details/50524571");
        builder.append("页面爬虫错误");
        builder.append("\n data " + System.currentTimeMillis());
        msg.setText(builder.toString());
        // 邮件发送者
        msg.setFrom(new InternetAddress("**发送人的邮箱地址**"));

        // 发送邮件
        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "**发送人的邮箱地址**", "**你的邮箱密码或者授权码**");

        transport.sendMessage(msg, new Address[]{new InternetAddress("**接收人的邮箱地址**")});
        transport.close();

    }
}
