package com.datatrees.rawdatacentral.common.utils;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;

public class HtmlunitUtils {

    public static String getPage(Long taskId, String url) {
        String pageContent = null;
        try {
            WebClient webClient = new WebClient();
            webClient.getCookieManager().setCookiesEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setDownloadImages(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            HtmlPage page = webClient.getPage("https://ah.ac.10086.cn/login");
            pageContent = page.asXml();
            Set<Cookie> cookies = webClient.getCookieManager().getCookies();
            List<com.datatrees.spider.share.domain.http.Cookie> list = TaskUtils.getCookies(cookies);
            System.out.println(JSON.toJSONString(list));

        } catch (Throwable e) {

        }
        return pageContent;
    }

}
