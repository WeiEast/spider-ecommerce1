package com.datatrees.crawler.core.xml;

import java.io.*;

import com.datatrees.crawler.core.domain.config.ExtractorConfig;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigBuilder;
import com.datatrees.crawler.core.util.xml.Impl.XmlConfigParser;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 8, 2014 10:10:54 AM
 */
public class WebsiteConfigTest {

    @Test
    public void searchConfigTest() {
        try {
            String config = readFile("zhouxinghai01/search.xml");
            SearchConfig websiteConfig = XmlConfigParser.getInstance().parse(config, SearchConfig.class);
            System.out.println(XmlConfigBuilder.getInstance().buildConfig(websiteConfig));
            System.out.println(websiteConfig.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extratorConfigTest() {
        try {
            String config = readFile("extratorConfig.xml");
            ExtractorConfig websiteConfig = XmlConfigParser.getInstance().parse(config, ExtractorConfig.class);
            System.out.println(XmlConfigBuilder.getInstance().buildConfig(websiteConfig));
            System.out.println(websiteConfig.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readFile(String path) {
        String content = "";
        InputStream input = ClassLoader.getSystemResourceAsStream(path);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String data;
            while ((data = reader.readLine()) != null) content = content + data;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }

    @Test
    public void diff() {
        String input = readFile("input");
        String input2 = readFile("input2");
        System.out.println(input.length());
        System.out.println(input2.length());
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == input2.charAt(i)) {

            } else {
                System.out.println("i:" + i);
                System.out.println(input.substring(i));
                System.out.println(input2.substring(i));
                return;
            }

        }

    }

    public void blobInsert() throws Exception {
        // 2 126.com
        // 3 136.com
        // 5 cgbchina.com.cn 广发
        // 6 cebbank.com 光大
        // 7 hxb.com.cn 华夏
        // 8 ccb.com 建设
        // 9 cmbc.com.cn 民生
        // 10 abchina.com 农业
        // 11 spdb.com.cn 浦发
        // 12 cib.com.cn 兴业
        // 13 boc.cn 中国
        // 14 ecitic.com 中信
        // 15 icbc.com.cn 工商
        // 16 bankcomm.com 交通
        // 17 pingan.com 平安
        // 18 alipay.com 支付宝
        // 19 10010.com 联通
        // 20 zj.189.cn 电信
        // 21 gd.189.cn
        // 22 zj.10086.cn 移动
        // 23 gd.10086.cn

        String[] websiteName = {"126", "136", "cgb", "ceb", "hxb", "ccb", "cmbc", "abchina", "spdb", "cib", "boc", "ecitic", "icbc", "bankcomm", "pingan", "alipay", "chinaunicom", "zj189", "gd189", "zj10086", "gd10086"};
        // 1 only search 2 only extractor 3 both
        int[] haveSearchConfig = {1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3};
        int[] websiteId = {2, 3, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};
        String infile = "/Users/yaojun-2/Desktop/config/%s";
        try {
            for (int i = 0; i < websiteName.length; i++) {
                String searchFilePath = String.format(infile, websiteName[i] + "SearchConfig.xml");
                String extractorFilePath = String.format(infile, websiteName[i] + "ExtractorConfig.xml");
                String extractorConfig = null;
                String searchConfig = null;
                if (haveSearchConfig[i] == 3) {
                    extractorConfig = this.readConfigFile(extractorFilePath);
                    searchConfig = this.readConfigFile(searchFilePath);
                    System.out.println(String.format("insert into t_website_conf (WebsiteId,SearchConfig,ExtractorConfig,CreatedAt,UpdatedAt) values(%d, '%s', '%s', now(), now())", websiteId[i], searchConfig, extractorConfig));
                } else if (haveSearchConfig[i] == 2) {
                    extractorConfig = this.readConfigFile(extractorFilePath);
                    System.out.println(String.format("insert into t_website_conf (WebsiteId,ExtractorConfig,CreatedAt,UpdatedAt) values(%d,'%s', now(), now())", websiteId[i], extractorConfig));
                } else if (haveSearchConfig[i] == 1) {
                    searchConfig = this.readConfigFile(searchFilePath);
                    System.out.println(String.format("insert into t_website_conf (WebsiteId,SearchConfig,CreatedAt,UpdatedAt) values(%d,'%s', now(), now())", websiteId[i], searchConfig));
                } else {
                    System.out.println("Error!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readConfigFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fis = new FileInputStream(file);
        // StringBuilder sb = new StringBuilder();
        // String str = null;
        // int n = 512;
        // byte buffer[] = new byte[n];
        // int len = 0;
        // while ((len = fis.read(buffer)) != -1) {
        // sb.append(new String(buffer, 0, len));
        // }
        try {
            String fileString = IOUtils.toString(fis);
            return StringEscapeUtils.escapeJava(fileString).replaceAll("'", "\\\\'");
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    @Test
    public void genBlobSQL() {
        WebsiteConfigTest operator = new WebsiteConfigTest();
        try {
            operator.blobInsert();
            System.out.println("--------------success------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
