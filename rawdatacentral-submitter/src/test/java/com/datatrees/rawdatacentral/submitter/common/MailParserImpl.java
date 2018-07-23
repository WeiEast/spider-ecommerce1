/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.rawdatacentral.submitter.common;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import com.datatrees.common.conf.PropertiesConfiguration;
import com.datatrees.common.protocol.Content;
import com.datatrees.common.protocol.metadata.Metadata;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.common.util.StringUtils;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.FileUtils;
import com.datatrees.crawler.core.processor.mail.Mail;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.field.FieldName;
import org.apache.james.mime4j.message.*;
import org.apache.james.mime4j.parser.Field;
import org.apache.james.mime4j.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年10月8日 下午3:07:06
 */
public enum MailParserImpl {
    INSTANCE;

    private static final Logger logger                = LoggerFactory.getLogger(MailParserImpl.class);

    private              String attachmentTypePattern = PropertiesConfiguration.getInstance().get("mail.server.ip.regex", "attachment");

    public Mail parseMessage(String websiteName, InputStream fis, List<OssServiceTest.Replace> list) throws UnsupportedEncodingException {
        Mail mimeMsg = null;
        try {
            mimeMsg = new Mail(fis);
            mimeMsg.setWebsiteName(websiteName);
            // If message contains many parts - parse all parts
            if (mimeMsg.isMultipart()) {
                Multipart multipart = (Multipart) mimeMsg.getBody();
                parseBodyParts(mimeMsg, multipart, list);
            } else {
                // If it's single part message, just get text body
                String text = getTxtPart(mimeMsg, list);
                mimeMsg.getTxtBody().append(text);
            }
            return mimeMsg;
        } catch (IOException ex) {
            logger.error("mail parser error,exception:" + ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }

    /**
     * This method classifies bodyPart as text, html or attached file
     * @param multipart
     * @exception IOException
     */
    private void parseBodyParts(Mail mimeMsg, Multipart multipart, List<OssServiceTest.Replace> list) throws IOException {
        for (BodyPart part : multipart.getBodyParts()) {
            if (part.getDispositionType() != null && PatternUtils.match(attachmentTypePattern, part.getDispositionType().toLowerCase())) {
                // If DispositionType is null or empty, it means that it's multipart, not attached
                // file
                // this.saveAttachment(mimeMsg, part);
            } else if (part.isMimeType("text/plain")) {
                String txt = getTxtPart(part, list);
                mimeMsg.getTxtBody().append(txt);
            } else if (part.isMimeType("text/html")) {
                String html = getTxtPart(part, list);
                mimeMsg.getHtmlBody().append(html);
            } else {
                logger.warn(
                        "unsupport  part Type:" + part.getFilename() + "," + part.getMimeType() + "," + part.getCharset() + "," + part.getHeader());
            }

            // If current part contains other, parse it again by recursion
            if (part.isMultipart()) {
                parseBodyParts(mimeMsg, (Multipart) part.getBody(), list);
            }
        }
    }

    private String getAttachmentFileName(Entity part) {
        String fileName = part.getFilename();
        if (StringUtils.isBlank(fileName)) {
            Field field = part.getHeader().getField(FieldName.CONTENT_DISPOSITION);
            if (field != null && field.getBody() != null) {
                fileName = PatternUtils.group(field.getBody().toLowerCase(),
                        PropertiesConfiguration.getInstance().get("attachment.fileName.pattern", "filename\\s*=\\s*\"([^\"]+)\""), 1);
            }
        }
        return fileName;
    }

    private void saveAttachment(Mail mimeMsg, Entity part) throws IOException {
        FileWapper fileWapper = new FileWapper();
        File file = new File(FileUtils.getFileRandomPath(mimeMsg.getWebsiteName()));
        FileOutputStream fos = new FileOutputStream(file);
        ByteArrayOutputStream baos = null;
        try {
            fileWapper.setName(getAttachmentFileName(part));
            fileWapper.setMimeType(part.getMimeType());
            // Get attach stream, write it to file
            SingleBody body = ((SingleBody) part.getBody());
            if (fileWapper.needDetectContent()) {
                baos = new ByteArrayOutputStream();
                body.writeTo(baos);
                Content content = new Content("", "", baos.toByteArray(), "" + part.getHeader().getField(FieldName.CONTENT_TYPE), new Metadata());
                IOUtils.write(content.detectContentAsString().getBytes("UTF-8"), fos);
            } else {
                body.writeTo(fos);
            }
            // body.dispose();
            fileWapper.setFile(file);
            fileWapper.setSize(file.length());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(fos);
        }
        mimeMsg.getAttachments().add(fileWapper);
    }

    private String getTxtPart(Entity part, List<OssServiceTest.Replace> list) {
        ByteArrayOutputStream baos = null;
        try {
            // Get content from body
            SingleBody tb = ((TextBody) part.getBody());
            baos = new ByteArrayOutputStream();
            tb.writeTo(baos);
            String contentType = part.getHeader().getField(FieldName.CONTENT_TYPE) != null ?
                    part.getHeader().getField(FieldName.CONTENT_TYPE).getBody() : "";
            Content content = new Content("", "", baos.toByteArray(), contentType, new Metadata());
            part.removeBody();
            String result = content.detectContentAsString();
            for (OssServiceTest.Replace replace : list) {
                result = result.replaceAll(replace.from, replace.to);
            }
            part.setBody(new StringTextBody(result, Charset.forName(content.getCharSet())));
            return content.detectContentAsString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
        }
        return null;
    }

    class StringTextBody extends TextBody {

        private final String  text;

        private final Charset charset;

        public StringTextBody(final String text, Charset charset) {
            this.text = text;
            this.charset = charset;
        }

        @Override
        public String getMimeCharset() {
            return CharsetUtil.toMimeCharset(charset.name());
        }

        @Override
        public Reader getReader() throws IOException {
            return new StringReader(text);
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            if (out == null) throw new IllegalArgumentException();

            Reader reader = new StringReader(text);
            Writer writer = new OutputStreamWriter(out, charset);

            char buffer[] = new char[1024];
            while (true) {
                int nChars = reader.read(buffer);
                if (nChars == -1) break;

                writer.write(buffer, 0, nChars);
            }

            reader.close();
            writer.flush();
        }

        @Override
        public StringTextBody copy() {
            return new StringTextBody(text, charset);
        }

    }
}
