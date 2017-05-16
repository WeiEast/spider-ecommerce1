import com.datatrees.rawdatacentral.common.utils.IpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/5/15.
 */
public class IpUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(IpUtilsTest.class);

    @Test
    public void testGetLocalHostName() {
        String localHostName = IpUtils.getLocalHostName();
        logger.info("localHostName={}", localHostName);
        Assert.assertNotNull(localHostName);
    }
}
