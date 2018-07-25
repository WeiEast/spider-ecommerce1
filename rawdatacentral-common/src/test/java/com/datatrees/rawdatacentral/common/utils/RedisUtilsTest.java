package com.datatrees.rawdatacentral.common.utils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.spider.share.common.utils.JsoupXpathUtils;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.junit.Test;

public class RedisUtilsTest {

    @Test
    public void test() throws IOException {
        WebClient webClient = new WebClient();
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        HtmlPage page = webClient.getPage("https://ah.ac.10086.cn/login");
        String content = page.asXml();
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        List<com.datatrees.spider.share.domain.http.Cookie> list = TaskUtils.getCookies(cookies);
        String spid = JsoupXpathUtils.selectFirst(content, "//form[@id='oldLogin']/input[@name='spid']/@value");
        String title = JsoupXpathUtils.selectFirst(content, "/titel");
        webClient.close();

    }

}