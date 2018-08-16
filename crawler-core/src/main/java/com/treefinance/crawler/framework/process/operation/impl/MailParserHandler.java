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

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.protocol.Content;
import com.datatrees.common.protocol.util.CharsetUtil;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.Constants;
import com.datatrees.crawler.core.processor.common.FileUtils;
import com.datatrees.crawler.core.processor.common.IPAddressUtil;
import com.treefinance.crawler.framework.download.WrappedFile;
import com.treefinance.toolkit.util.RegExp;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.james.mime4j.field.FieldName;
import org.apache.james.mime4j.message.*;
import org.apache.james.mime4j.parser.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月8日 下午3:07:06
 */
final class MailParserHandler {
    private static final Logger logger                = LoggerFactory.getLogger(MailParserHandler.class);
    private static final String MAIL_SERVER_IP_REGEX  = PropertiesConfiguration.getInstance().get("mail.server.ip.regex", "\\([^\\]]*\\[([\\d\\.]+)(:\\d+)?\\]\\)");
    private static final String attachmentTypePattern = PropertiesConfiguration.getInstance().get("mail.attachmentType", "attachment");

    public static Map parseMessage(String websiteName, String contentString, boolean bodyParser) throws IOException {
        try (InputStream fis = IOUtils.toInputStream(contentString)) {
            Mail mimeMsg = new Mail(fis);
            try {
                mimeMsg.setWebsiteName(websiteName);
                if (bodyParser) {
                    // If message contains many parts - parse all parts
                    if (mimeMsg.isMultipart()) {
                        Multipart multipart = (Multipart) mimeMsg.getBody();
                        parseBodyParts(mimeMsg, multipart);
                    } else {
                        // If it's single part message, just get text body
                        String text = getTxtPart(mimeMsg);
                        mimeMsg.getTxtBody().append(text);
                    }
                }
                return mailTransform(mimeMsg);
            } finally {
                mimeMsg.dispose();
            }
        }
    }

    private static Map mailTransform(Mail mimeMsg) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : mimeMsg.getHeader().getFields()) {
            List<Field> values = (List<Field>) map.computeIfAbsent(field.getName().toLowerCase(), k -> new LinkedList<>());
            values.add(field);
        }
        map.put(Constants.MAIL_DEFAULT_PREFIX + FieldName.DATE, mimeMsg.getDate());
        try {
            map.put(Constants.MAIL_DEFAULT_PREFIX + FieldName.FROM, mimeMsg.getFrom().get(0).getAddress());
        } catch (Exception e) {
            logger.warn("Error reading mail's from address.", e);
        }
        map.put(Constants.MAIL_DEFAULT_PREFIX + FieldName.TO, mimeMsg.getTo());
        map.put(Constants.MAIL_DEFAULT_PREFIX + FieldName.SUBJECT, mimeMsg.getSubject());
        map.put(Constants.PAGE_TEXT, mimeMsg.getTxtBody().toString());
        if (StringUtils.isNotBlank(mimeMsg.getHtmlBody().toString())) {
            map.put(Constants.PAGE_CONTENT, mimeMsg.getHtmlBody().toString());
        } else {
            map.put(Constants.PAGE_CONTENT, mimeMsg.getTxtBody().toString());
        }
        map.put(Constants.ATTACHMENT, mimeMsg.getAttachments());
        Map<String, String> mailHeader = new HashMap<>();
        for (Field field : mimeMsg.getHeader().getFields()) {
            mailHeader.put(field.getName(), field.getBody());
        }
        map.put("mailHeader", GsonUtils.toJson(mailHeader));
        receivedFormat(map);
        return map;
    }

    private static void receivedFormat(Map<String, Object> result) {
        try {
            List<Field> receivedList = (List<Field>) result.get("received");
            if (CollectionUtils.isNotEmpty(receivedList)) {
                List<String> ipList = RegExp.findAll(receivedList.toString(), MAIL_SERVER_IP_REGEX, 1);
                for (String ip : ipList) {
                    if (StringUtils.isNotBlank(ip)) {
                        logger.debug("extract mail server ip: {}", ip);

                        if (IPAddressUtil.internalIp(ip)) {
                            logger.debug("drop internalIp : {}", ip);
                        } else {
                            result.put(Constants.MAIL_SERVER_IP, ip);
                            break;
                        }
                    } else {
                        logger.warn("extract mail server ip error with receivedList[{}], MAIL_SERVER_IP_REGEX: {}", receivedList, MAIL_SERVER_IP_REGEX);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error formatting mail's received with {}", result, e);
        }
    }

    /**
     * This method classifies bodyPart as text, html or attached file
     * @param multipart
     * @exception IOException
     */
    private static void parseBodyParts(Mail mimeMsg, Multipart multipart) throws IOException {
        for (BodyPart part : multipart.getBodyParts()) {
            if (part.getDispositionType() != null && RegExp.find(part.getDispositionType().toLowerCase(), attachmentTypePattern)) {
                // If DispositionType is null or empty, it means that it's multipart, not attached
                // file
                saveAttachment(mimeMsg, part);
            } else if (part.isMimeType("text/plain")) {
                String txt = getTxtPart(part);
                mimeMsg.getTxtBody().append(txt);
            } else if (part.isMimeType("text/html")) {
                String html = getTxtPart(part);
                mimeMsg.getHtmlBody().append(html);
            } else {
                logger.warn("unsupport part Type: {}, {}, {}, {}", part.getFilename(), part.getMimeType(), part.getCharset(), part.getHeader());
            }

            // If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts(mimeMsg, (Multipart) part.getBody());
            }
        }
    }

    private static String getAttachmentFileName(Entity part) {
        String fileName = part.getFilename();
        if (StringUtils.isBlank(fileName)) {
            Field field = part.getHeader().getField(FieldName.CONTENT_DISPOSITION);
            if (field != null && field.getBody() != null) {
                fileName = RegExp.group(field.getBody().toLowerCase(), PropertiesConfiguration.getInstance().get("attachment.fileName.pattern", "filename\\s*=\\s*\"([^\"]+)\""), 1);
            }
        }
        return fileName;
    }

    private static void saveAttachment(Mail mimeMsg, Entity part) throws IOException {
        File file = new File(FileUtils.getFileRandomPath(mimeMsg.getWebsiteName()));
        WrappedFile wrappedFile = new WrappedFile(file);
        wrappedFile.setName(getAttachmentFileName(part));
        wrappedFile.setMimeType(part.getMimeType());

        // Get attach stream, write it to file
        SingleBody body = (SingleBody) part.getBody();
        try {
            if (wrappedFile.needDetectContent()) {
                String content = readContent(body, part.getHeader());
                wrappedFile.write(content.getBytes(CharsetUtil.UTF_8_NAME));
            } else {
                try (OutputStream fos = new FileOutputStream(wrappedFile.getFile())) {
                    body.writeTo(fos);
                    wrappedFile.setSize(file.length());
                }
            }
        } finally {
            body.dispose();
        }

        mimeMsg.getAttachments().add(wrappedFile);
    }

    private static String getTxtPart(Entity part) throws IOException {
        // Get content from body
        TextBody tb = (TextBody) part.getBody();
        try {
            return readContent(tb, part.getHeader());
        } finally {
            tb.dispose();
        }
    }

    private static String readContent(SingleBody tb, Header header) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            tb.writeTo(output);
            Field field = header.getField(FieldName.CONTENT_TYPE);
            String contentType = field != null ? field.getBody() : StringUtils.EMPTY;
            Content content = new Content(output.toByteArray(), contentType);
            return content.detectContentAsString();
        }
    }
}
