import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
        // 凭据提供器
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        //URI serverURI = new URI("http://192.168.5.206/Frame.htm");
        URI serverURI = new URI("http://192.168.5.206/SmsRecvNew.htm");
        credsProvider.setCredentials(new AuthScope(serverURI.getHost(), serverURI.getPort()), new UsernamePasswordCredentials("admin", "admin"));
        RequestConfig config = RequestConfig.custom().setRedirectsEnabled(false).setConnectTimeout(3000).setSocketTimeout(3000)
                .setCookieSpec(CookieSpecs.DEFAULT).build();
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(config).setDefaultCredentialsProvider(credsProvider).build();
        while (true) {
            HttpGet httpGet = new HttpGet(serverURI);
            CloseableHttpResponse httpResponse = httpclient.execute(httpGet);
            System.out.println(httpResponse.getStatusLine());
            byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
            String page = new String(data, "utf-8");
            System.out.println(page);
        }
    }
}
