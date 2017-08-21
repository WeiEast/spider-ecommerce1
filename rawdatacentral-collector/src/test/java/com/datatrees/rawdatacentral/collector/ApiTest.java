package com.datatrees.rawdatacentral.collector;

import com.datatrees.rawdatacentral.api.CrawlerService;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.utils.CookieUtils;
import com.datatrees.rawdatacentral.common.utils.TaskHttpClient;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.operator.OperatorCatalogue;
import com.datatrees.rawdatacentral.domain.result.HttpResult;
import com.datatrees.rawdatacentral.domain.vo.Response;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.nio.charset.Charset;
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
        Long taskId = 84316322183278592L;
        String cookieString = CookieUtils.getCookieString(taskId);
        CollectorMessage message = new CollectorMessage();
        message.setAccountNo(taskId.toString());
        message.setTaskId(taskId);
        message.setCookie(cookieString);
        message.setWebsiteName("china_10086_shop");
        message.setEndURL("");
        collector.processMessage(message);
    }

    @Test
    public void testHttp() throws Exception {
        Long taskId = 84316322183278592L;
        String artifact = "";
        String templateUrl = "http://shop.10086.cn/i/v1/auth/getArtifact?artifact={}&backUrl=http://shop.10086.cn/i/?f=home";

        Response response = TaskHttpClient.create(taskId, "china_10086_shop", RequestType.GET, "china_10086_shop_005").setFullUrl(templateUrl, artifact)
                .setRequestCharset(Charset.forName("UTF-8")).setRequestContentType(ContentType.APPLICATION_FORM_URLENCODED).invoke();
        System.out.println(response);
    }

}
