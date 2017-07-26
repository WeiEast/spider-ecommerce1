package com.datatrees.rawdatacentral.collector;

import com.alibaba.fastjson.JSON;
import com.datatrees.crawler.plugin.util.PluginHttpUtils;
import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by zhouxinghai on 2017/6/23
 */
public class ApiTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ApiTest.class);

    @Resource
    private CrawlerService      crawlerService;

    @Resource
    private Collector           collector;

    @Test
    public void testQueryAllOperatorConfig() throws Exception {
        HttpResult<List<OperatorCatalogue>> result = crawlerService.queryAllOperatorConfig();
        List<OperatorCatalogue> list = result.getData();
        Assert.assertTrue(!list.isEmpty());
    }

    @Test
    public void startTask() throws Exception {
        Long taskId = 123456L;
        String cookieString = PluginHttpUtils.getCookieString(taskId);
        CollectorMessage message = new CollectorMessage();
        message.setAccountNo(taskId.toString());
        message.setTaskId(taskId);
        message.setCookie(cookieString);
        message.setWebsiteName("china_10086_shop");
        message.setEndURL("");
        collector.processMessage(message);
        TimeUnit.SECONDS.sleep(60);

    }

}
