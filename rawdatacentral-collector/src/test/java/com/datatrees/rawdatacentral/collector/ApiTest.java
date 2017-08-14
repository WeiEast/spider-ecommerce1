package com.datatrees.rawdatacentral.collector;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

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
        Long taskId = 123789L;
        String cookieString = CookieUtils.getCookieString(taskId);
        CollectorMessage message = new CollectorMessage();
        message.setAccountNo(taskId.toString());
        message.setTaskId(taskId);
        message.setCookie(cookieString);
        message.setWebsiteName("china_10086_shop");
        message.setEndURL("");
        collector.processMessage(message);
    }

}
