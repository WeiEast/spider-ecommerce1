import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datatrees.rawdatacentral.domain.result.ProcessResult;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/5/15.
 */
public class IpUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(IpUtilsTest.class);

    @Test
    public void testGetLocalHostName() throws IOException, URISyntaxException {
        String str = "{\n" + "\t\"extra\": {},\n" + "\t\"processId\": 1,\n" + "\t\"processStatus\": \"SUCCESS\",\n" +
                "\t\"timestamp\": 1515137072053\n" + "}";
        JSON json = JSON.parseObject(str);
        ProcessResult<Object> result = JSON.parseObject(str, new TypeReference<ProcessResult<Object>>() {});
        System.out.println(json.toJSONString());
    }
}
