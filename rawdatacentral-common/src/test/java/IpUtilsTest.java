import java.io.IOException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/5/15.
 */
public class IpUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(IpUtilsTest.class);

    @Test
    public void testGetLocalHostName() throws IOException {
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        HtmlPage page = webClient.getPage("https://ah.ac.10086.cn/login");
        System.out.println(page.asXml());
        //page = webClient.getPage("https://ah.ac.10086.cn/login");
        //System.out.println(page.asXml());
        //HtmlElement head = page.getHead();
        //System.out.println(page.asXml());
    }
}
