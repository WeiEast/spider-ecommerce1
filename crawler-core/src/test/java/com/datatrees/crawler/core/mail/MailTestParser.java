/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.mail;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月19日 下午3:38:12
 */

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

import com.datatrees.common.util.ResourceUtil;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.mail.MailParserImpl;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.message.*;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.CharsetUtil;
import org.apache.james.mime4j.util.ContentUtil;
import org.apache.tika.io.IOUtils;
import org.junit.Test;

/**
 *
 * @author Denis Lunev <den@mozgoweb.com>
 */
public class MailTestParser extends BaseConfigTest {

    private StringBuffer txtBody;
    private StringBuffer htmlBody;
    private ArrayList<BodyPart> attachments = new ArrayList<BodyPart>();

    /**
     *
     * @param args
     * @throws UnsupportedEncodingException
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        MailTestParser parser = new MailTestParser();
        // parser.parseMessage("mail/qqfujianHeader");
        parser.parseMessage("mail/cmb.eml");

    }

    public void parseMessage(String fileName) throws UnsupportedEncodingException {

        String contentString = new String(getContent(fileName).getBytes(), "iso-8859-1");
        System.out.println(contentString);
        InputStream fis = IOUtils.toInputStream(contentString);
        // InputStream fis = ClassLoader.getSystemResourceAsStream(fileName);

        txtBody = new StringBuffer();
        htmlBody = new StringBuffer();
        try {
            // Get stream from file
            // System.out.println(new String(IOUtils.toCharArray(fis, "UTF-8")));

            // Create message with stream from file
            // If you want to parse String, you can use:
            // Message mimeMsg = new Message(new ByteArrayInputStream(mimeSource.getBytes()));
            Message mimeMsg = new Message(fis);

            // Get some standard headers
            System.out.println("To: " + mimeMsg.getTo().toString());
            System.out.println("From: " + mimeMsg.getFrom().toString());
            System.out.println("From: " + mimeMsg.getFrom().get(0).getAddress());
            System.out.println("From: " + mimeMsg.getSender());

            System.out.println("Subject: " + mimeMsg.getSubject());

            System.out.println("date: " + mimeMsg.getDate());

            // Get custom header by name
            Field priorityFld = mimeMsg.getHeader().getField("X-Priority");
            // If header doesn't found it returns null
            if (priorityFld != null) {
                // Print header value
                System.out.println("Priority: " + priorityFld.getBody());
            }
            // If message contains many parts - parse all parts
            if (mimeMsg.isMultipart()) {
                Multipart multipart = (Multipart) mimeMsg.getBody();
                parseBodyParts(multipart);
            } else {
                // If it's single part message, just get text body
                String text = getTxtPart(mimeMsg);
                txtBody.append(text);
            }

            // Print text and HTML bodies
            // System.out.println("Text body: " + txtBody.toString());
            // System.out.println("Html body: " + htmlBody.toString());

            for (BodyPart attach : attachments) {
                String attName = attach.getFilename();

                System.out.println(DecoderUtil.decodeEncodedWords(DecoderUtil.decodeEncodedWords(attName)));

                // Create file with specified name
                FileOutputStream fos = new FileOutputStream("/tmp/" + attName);
                System.out.println("attName file: " + attName);

                try {
                    // Get attach stream, write it to file
                    BinaryBody bb = (BinaryBody) attach.getBody();
                    bb.writeTo(fos);
                } finally {
                    fos.close();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * This method classifies bodyPart as text, html or attached file
     *
     * @param multipart
     * @throws IOException
     */
    private void parseBodyParts(Multipart multipart) throws IOException {
        for (BodyPart part : multipart.getBodyParts()) {
            if (part.isMimeType("text/plain")) {
                String txt = getTxtPart(part);
                txtBody.append(txt);
            } else if (part.isMimeType("text/html")) {
                String html = getTxtPart(part);
                htmlBody.append(html);
            } else if (part.getDispositionType() != null && !part.getDispositionType().equals("")) {
                // If DispositionType is null or empty, it means that it's multipart, not attached
                // file
                System.out.println(DecoderUtil.decodeEncodedWords(new String(part.getFilename().getBytes("iso-8859-1"))));
                System.out.println(part.getFilename() + "," + part.getMimeType() + "," + part.getCharset() + "," + part.getHeader());
                attachments.add(part);
            } else {
                System.out.println(part.getFilename() + "," + part.getMimeType() + "," + part.getCharset() + "," + part.getHeader());
            }

            // If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts((Multipart) part.getBody());
            }
        }
    }

    private String getTxtPart(Entity part) throws IOException {
        // Get content from body
        TextBody tb = (TextBody) part.getBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        tb.writeTo(baos);
        return new String(baos.toByteArray(), tb.getMimeCharset());
    }

    /**
     *
     */
    @Test
    public void testcha() {
        String eml = "￦ﾏﾭ￧ﾧﾘ￤﾿ﾡ￧ﾔﾨ￥ﾍﾡ￦ﾏﾐ￩ﾢﾝ￩ﾪﾗ￥ﾱﾀ.htm";
        System.out.println(eml);
        char[] strChar = eml.toCharArray();

        byte[] buffer = new byte[strChar.length];
        for (int i = 0; i < strChar.length; i++) {
            buffer[i] = (byte) strChar[i];
        }

        System.err.println(new String(buffer));
    }

    @Test
    public void testencode() {
        String eml = "���������������������������.htm";
        System.out.println(eml);
        ByteBuffer TT = CharsetUtil.US_ASCII.encode(eml);
        System.out.println(ContentUtil.encode(eml));

        char[] strChar = eml.toCharArray();

        byte[] buffer = new byte[strChar.length];
        for (int i = 0; i < strChar.length; i++) {
            buffer[i] = (byte) strChar[i];
        }

        System.err.println(new String(buffer));
    }

    @Test
    public void mailMimeParserTest() {
        String content = ResourceUtil.getContent("mailmime/emltest", null);
        try {
            Map<String, Object> map = MailParserImpl.INSTANCE.parseMessage("qq.com", content, true);

            for (String key : map.keySet()) {
                System.out.println(key);
                System.out.println(map.get(key));
                System.out.println();
                System.out.println();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
