package com.datatrees.rawdatacentral.submitter.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.datatrees.rawdatacentral.submitter.filestore.oss.OssService;
import com.datatrees.rawdatacentral.submitter.filestore.oss.OssServiceProvider;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.field.address.Address;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.junit.Test;

import com.datatrees.common.util.DateUtils;
import com.datatrees.common.util.ResourceUtil;
import com.datatrees.crawler.core.processor.mail.Mail;


public class OssServiceTest {

    protected static String getContent(String fileName) {
        return ResourceUtil.getContent(fileName, null);
    }

    @Test
    public void testUpload() {
        OssService service = OssServiceProvider.getDefaultService();
        File file = new File("/tmp/abc.zip");
        String result;
        try {
            result = service.putObject(SubmitConstant.ALIYUN_OSS_DEFAULTBUCKET, "20150729_1_1234567", file);
            System.out.println(result);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] returnObject = service.getObjectContent("20150804_63_31bc2254-825e-42bf-93ea-7570e48cc535");
        try {
            FileOutputStream fos = new FileOutputStream("/Users/yaojun-2/Downloads/1.zip");
            fos.write(returnObject);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 20150826_151_af0283711666d7235a0962b362442a0c
    // 20150826_151_c741223df08f7523ceca38d0764cc482
    // 20150826_151_7e9501abcadd22439292f1b886714dd9


    @Test
    public void testGet() {
        OssService service = OssServiceProvider.getDefaultService();

        String[] keys = getContent("keys").split(",");
        for (String key : keys) {
            String date = "";
            if (key.contains("#")) {
                date = org.apache.commons.lang.StringUtils.substringBefore(key, "#");
                key = org.apache.commons.lang.StringUtils.substringAfter(key, "#");
            }

            String path = key.substring(0, key.indexOf("/"));
            File file = new File("fileoutput/" + path);
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                byte[] returnObject = service.getObjectContent(key);

                FileOutputStream fos = new FileOutputStream("fileoutput/" + path + "/" + date + key.replaceAll("/", "-") + ".zip");
                fos.write(returnObject);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class Replace {
        String from;
        String to;

        /**
         * @param from
         * @param to
         */
        public Replace(String from, String to) {
            super();
            this.from = from;
            this.to = to;
        }
    }

    @Test
    public void changeName() throws IOException, MessagingException {
        BufferedReader reader = new BufferedReader(
                new FileReader(new File("/Users/wangcheng/Documents/newworkspace/rawdata1/rawdatacentral/rawdatacentral-submitter/src/test/resource/keys")));
        String tempString = null;
        OssService service = OssServiceProvider.getDefaultService();
        String imap = "imap.139.com";

        String mailFrom = "(QQ.COM|qq.com|139.com|126.com|163.com)";
        String mailTo = "qq.com";
        String accoutFrom = "393017886";
        String accoutTO = "593237554";

        String imapAccount = "593237554";
        String pass = "zuslvselrt";

        String nameFrom = "蒋小淼";
        String nameTO = "王成";

        Folder folder = IMAPMailClient.getQQInbox(imap, imapAccount, pass);

        while ((tempString = reader.readLine()) != null) {
            Date billMonth = null;
            if (tempString.indexOf("#") > 0) {
                billMonth = DateUtils.strToDate(tempString.split("#")[1]);
                tempString = tempString.split("#")[0];
            }

            if (tempString.indexOf("/") < 0) {
                tempString = "1001228905/1/" + tempString;
            }
            System.out.println("begin to upload path: " + tempString);
            String path = tempString.substring(0, tempString.indexOf("/"));
            File file = new File("fileoutput/" + path);
            if (!file.exists()) {
                file.mkdir();
            }
            try {
                byte[] returnObject = service.getObjectContent(tempString);
                String fileName = "fileoutput/" + path + "/" + tempString.replaceAll("/", "-") + ".zip";
                String emlFileName = "fileoutput/" + path + "/" + tempString.replaceAll("/", "-") + ".eml";

                FileOutputStream fos = new FileOutputStream(fileName);
                IOUtils.write(returnObject, fos);
                IOUtils.closeQuietly(fos);
                ZipFile zip = new ZipFile(new File(fileName));
                ZipEntry zipEntry = zip.getEntry("pageContentFile.html");
                InputStream in = zip.getInputStream(zipEntry);
                String string = IOUtils.toString(in).replaceAll(accoutFrom, accoutTO).replaceAll(mailFrom, mailTo);
                if (billMonth != null) {
                    SimpleDateFormat mydate = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
                    mydate.format(billMonth);
                    System.out.println(mydate.format(billMonth));
                    string = string.replaceAll("\\w+, \\d+ \\w+ \\d{4}", mydate.format(billMonth));
                }


                List<Replace> list = new ArrayList<Replace>();
                list.add(new Replace(nameFrom, nameTO));

                Mail mimeMsg = MailParserImpl.INSTANCE.parseMessage(mailTo, IOUtils.toInputStream(string), list);
                mimeMsg.setTo(Address.parse(accoutTO + "@" + mailTo));


                if (mimeMsg.getSubject() != null) {
                    mimeMsg.setSubject(mimeMsg.getSubject().replace(nameFrom, nameTO));
                }
                // if (billMonth != null) {
                // mimeMsg.getDate().setYear(billMonth.getYear());
                // mimeMsg.getDate().setMonth(billMonth.getMonth());
                // mimeMsg.getDate().setDate(billMonth.getDate());
                // }
                File mailfile = new File(emlFileName);
                mimeMsg.writeTo(new FileOutputStream(mailfile));
                mimeMsg.dispose();

                Message message = new MimeMessage(null, new FileInputStream(mailfile));
                folder.appendMessages(new Message[] {message});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Test
    public void deleteKey() throws IOException, MessagingException {
        BufferedReader reader = new BufferedReader(
                new FileReader(new File("/Users/wangcheng/Documents/newworkspace/rawdata1/rawdatacentral/rawdatacentral-submitter/src/test/resource/keys")));
        String tempString = null;
        OssService service = OssServiceProvider.getDefaultService();

        while ((tempString = reader.readLine()) != null) {
            try {
                service.delete(tempString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
