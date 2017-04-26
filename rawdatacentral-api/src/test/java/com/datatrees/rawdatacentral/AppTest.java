package com.datatrees.rawdatacentral;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.domain.model.WebsiteConf;
import com.google.common.base.Preconditions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 以下方法中， refreshStatus 和 verifyQRCode 是轮询方法，即H5页面setInterval处理的逻辑
 */
public class AppTest {
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    @Test
    public void getOneWebsite() throws Exception {
        String websiteName = "alipay.com_server";
        // 直连dubbo 测试
        CrawlerService service = com.datatrees.rawdatacentral.DubboService.INSTANCE.getService(CrawlerService.class, "dubbo://10.139.50.234:20192");
        // 获取登录初始化配置,用于sdk或者H5页面的展示，和endulr的拦截，一些默认提示配置
        WebsiteConf conf = service.getWebsiteConf(websiteName);
        Preconditions.checkNotNull(conf, String.format("系统暂不支持网站%s", websiteName));
        logger.info(String.format("website[%s] get %s  success.", websiteName, conf));
    }


    @Test
    public void getWebsites() throws Exception {
        List<String> websites = new ArrayList<>();
        websites.add("alipay.com_server");
        websites.add("taobao.com_server");
        websites.add("qq.com");
        // 直连dubbo 测试
        CrawlerService service = com.datatrees.rawdatacentral.DubboService.INSTANCE.getService(CrawlerService.class, "dubbo://10.139.50.234:20192");
        // 获取登录初始化配置,用于sdk或者H5页面的展示，和endulr的拦截，一些默认提示配置
        List<WebsiteConf> list = service.getWebsiteConf(websites);
        logger.info(String.format("website[%s] get %s  success.", websites, list));
    }
}
