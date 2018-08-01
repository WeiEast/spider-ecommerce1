import com.treefinance.mybatis.generator.CodeGenerator;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * Created by zhouxinghai on 16/7/15.
 */
public class CreateCode {

    @Test
    public void generator() {
        String generator_xml = this.getClass().getClassLoader().getResource("generator.xml").getPath();
        String driverFile = this.getClass().getClassLoader().getResource("mysql-connector-java-5.1.36.jar").getPath();

        String projectRoot = StringUtils.substringBefore(this.getClass().getClassLoader().getResource("").getPath(), "/spider-generator");
        new CodeGenerator(projectRoot, generator_xml, driverFile).createCode();
    }
}
