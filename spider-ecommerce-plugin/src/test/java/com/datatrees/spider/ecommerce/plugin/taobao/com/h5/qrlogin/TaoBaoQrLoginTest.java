/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.datatrees.spider.ecommerce.plugin.taobao.com.h5.qrlogin;

import com.treefinance.crawler.support.selenium.SeleniumHelper;
import com.treefinance.toolkit.util.Base64Codec;
import com.treefinance.toolkit.util.Preconditions;
import com.treefinance.toolkit.util.crypto.RSA;
import com.treefinance.toolkit.util.crypto.core.Decryptor;
import com.treefinance.toolkit.util.crypto.exception.CryptoException;
import com.treefinance.toolkit.util.io.Streams;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Jerry
 * @date 2019-02-23 13:29
 */
public class TaoBaoQrLoginTest {
    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "/Users/Jerry/Downloads/chromedriver");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    @Test
    public void testMsgLoginUmToken() throws IOException {
        driver.get("https://login.m.taobao.com/msg_login.htm");

        String umTn = (String)new WebDriverWait(driver, 10).until(ExpectedConditions.jsReturnsValue("return localStorage.getItem('_um_cn_umsvtn');"));

        System.out.println(umTn);

        if (StringUtils.isNotEmpty(umTn)) {
            int i = umTn.indexOf("@@");
            if (i > -1) {
                System.out.println(umTn.substring(0, i));
            }
        }

        driver.quit();
    }

    @Test
    public void testUA() throws IOException {
        driver.get("https://login.taobao.com/member/login.jhtml?style=mini&newMini=true&goto=https%3A%2F%2Fmy.alipay.com%2Fportal%2Fi.htm%3Fsign_from%3D3000");

        Object result = SeleniumHelper.evalScript(driver, readJs());

        System.out.println(result);
    }

    private String readJs() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("115_custom.js")) {
            Preconditions.notNull("115_custom.js", inputStream);
            return Streams.readToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @Test
    public void test1() {
        byte[] data = Base64Codec.decode("eyJuZWVkY29kZSI6dHJ1ZX0=");
        System.out.println(new String(data));
    }

    @Test
    public void test2() throws CryptoException {
        String key =
            "9a39c3fefeadf3d194850ef3a1d707dfa7bec0609a60bfcc7fe4ce2c615908b9599c8911e800aff684f804413324dc6d9f982f437e95ad60327d221a00a2575324263477e4f6a15e3b56a315e0434266e092b2dd5a496d109cb15875256c73a2f0237c5332de28388693c643c8764f137e28e8220437f05b7659f58c4df94685";
        String text =
            "126ux11O1TNeZ2TPTCCe1Cso311GCpA11g2u11DwRfCL0a61qOrN15tuhWrlyzeuR7cPe1L8ykwumskJhUU4AWNca8pAurPQOSfPFt1C+9Ng/bWRhaFG96NDaL9X+6zIvIAyeH38ykL80EPehaz8OkNDah2fuzFQASAPe1L8ykCQOQk5sA9vVwPZAHejsgCs9C+EdQQNr6+plfPhO6iRTkVODhb/Jt4pPh18wQw4XDmMRCThU9NWfc8B43mSsIATOt000jW8sZsjtmQVsDPiD4AKR+YbPlDbKebKkZO7c63vOzzeKkxjV+xjj7NJmVZjxMbxl6+2WgNv4v2fbHBkJr7Q5jq7m3T84B4yOy1841UDwKFWl43Us+G27dbnNXkaLprmWGmzz3IZ/+1YkzRPrFM/fcJA/rRPBprrankFG9/kCyDcLv+XcQaGftdAoc6+9NYYeqv+0KtD7AnRybFmv3Qa7NJtksyuCNThFFRyxF1lCn0nMPC9yeTNVv9vj0Mvzm3HdRLEWLpNnJZo5JpVypwfZdkPSs/OSw4tPzPZhACqD7IUC8aqkMqhSs4LSROe4lmm5/bafZe4JIe60LcP9fit55iK1cjhXvMKmrP6gsd1Y2pKO2Mji1AAMAHh4WgJHiboR4OThs9n+vHvFJo8paMzu+glTdLeV3o4ZdNo/A2kjPyXwOKynvUoWWNSAvZLLbsh4pFuz/2qdmXIgIYkEcKEyo3+GMp2ACn6pJRP+p8bcB2qjyKRAM+Wwgv15/Tye+kE40y9JgE1sAi/VTo7JMDApBgTZY895RxSIoxa6pivixJbbZH5IgrxKBKy+slnvcOSxac5XEb3+Ri6hSbJGym9fBxArDis9x+UYP90KF+PtnK/a34zbTt0Amj44PUxLQE/nNNEbieT1472Krq3J3JMfBeftQrieRv7Lyz2LXrJRYlEWWEU5mTfMNWV2I73NdOidzXMp80fRFgm";

        Decryptor decryptor = RSA.createDecryptor(key);
        String result = decryptor.decryptWithBase64AsString(text);
        System.out.println(result);
    }
}