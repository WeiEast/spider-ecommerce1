package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10000_wap;

import javax.script.Invocable;
import java.io.InputStream;

import com.datatrees.rawdatacentral.common.utils.ScriptEngineUtil;
import org.junit.Assert;
import org.junit.Test;

public class YunNan10000ForWapTest {

    @Test
    public void testEncrypt() throws Exception {
        /**
         *  var accNbr = $("#accNbr").val();
         var password = $("#password").val();
         var enAccNbr = strEnc(accNbr,'wap_accnbr_2016','','');
         var enPassword = strEnc(password,'wap_password_2016','','');
         $("#enAccNbr").val(enAccNbr);
         $("#enPassword").val(enPassword);
         *
         */
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("yun_nan_10000_wap/des.js");
        Invocable invocable = ScriptEngineUtil.createInvocableFromBase64(inputStream, "UTF-8");
        String mobile = "15368726413";
        String password = "869380";
        String encodeMobile = invocable.invokeFunction("strEnc", mobile, "wap_accnbr_2016", "", "").toString();
        Assert.assertEquals(encodeMobile, "7BB8AB1C627DA1326101BCCEB7C8DA56B819B66058CCA6BD");

        Object encodePassword = invocable.invokeFunction("strEnc", password, "wap_password_2016", "", "");
        Assert.assertEquals(encodePassword, "83A5AA2D78E94FBCF3862E863BDD78E5");

    }

}
