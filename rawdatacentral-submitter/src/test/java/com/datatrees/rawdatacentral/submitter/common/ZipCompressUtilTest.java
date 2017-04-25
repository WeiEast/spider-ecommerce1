package com.datatrees.rawdatacentral.submitter.common;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;


public class ZipCompressUtilTest {
    @BeforeClass
    public static void init() {

    }

    @Test
    public void testZipCompress() throws UnsupportedEncodingException {
        String pageContent = "test";
        String zipPathName = "/tmp/abc.zip";
        Map<String, SubmitFile> map = new HashMap<String, SubmitFile>();
        map.put("pageContent", new SubmitFile(null, pageContent.getBytes("utf-8")));
        map.put("test1", new SubmitFile("test1.txt", pageContent.getBytes("utf-8")));
        ZipCompressUtils.compress(zipPathName, map);
        // ZipCompressUtils.compress(zipPathName, pageContent.getBytes(),"pageContent.txt");
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // ZipCompressUtils.compress(baos, map);
        // byte[] temp = baos.toByteArray();
        // try {
        // FileOutputStream fos = new FileOutputStream(zipPathName);
        // fos.write(temp);
        // fos.flush();
        // fos.close();
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }
}
