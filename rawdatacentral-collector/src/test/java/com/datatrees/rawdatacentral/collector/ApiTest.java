package com.datatrees.rawdatacentral.collector;

import javax.annotation.Resource;
import java.nio.charset.Charset;

import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.common.http.TaskHttpClient;
import com.datatrees.rawdatacentral.common.http.TaskUtils;
import com.datatrees.rawdatacentral.core.model.message.impl.CollectorMessage;
import com.datatrees.rawdatacentral.domain.enums.RequestType;
import com.datatrees.rawdatacentral.domain.vo.Response;
import org.apache.http.entity.ContentType;
import org.junit.Test;

/**
 * Created by zhouxinghai on 2017/6/23
 */
public class ApiTest extends BaseTest {

    @Resource
    private Collector      collector;


    @Test
    public void startTask() throws Exception {
        Long taskId = 84316322183278592L;
        String cookieString = TaskUtils.getCookieString(taskId);
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

        Response response = TaskHttpClient.create(taskId, "china_10086_shop", RequestType.GET, "china_10086_shop_005").setFullUrl(templateUrl, artifact).setRequestCharset(Charset.forName("UTF-8")).setRequestContentType(ContentType.APPLICATION_FORM_URLENCODED).invoke();
        System.out.println(response);
    }

}
