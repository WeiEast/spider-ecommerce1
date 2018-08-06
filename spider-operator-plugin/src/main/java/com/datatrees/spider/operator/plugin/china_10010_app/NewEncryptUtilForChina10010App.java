package com.datatrees.spider.operator.plugin.china_10010_app;

import javax.crypto.Cipher;
import java.net.URLEncoder;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by guimeichao on 17/12/28.
 */
public class NewEncryptUtilForChina10010App {

    private static final byte[] a = "#PART#".getBytes();

    public static String encode(String str, String U) throws Exception {
        String str2 = (new StringBuilder()).append(str).append(U).toString();
        String encodeString = Base64.encodeToString(a(str2.getBytes(), Base64.decode(
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+CZK9bBA9IU+gZUOc6FUGu7yO9WpTNB0PzmgFBh96Mg1WrovD1oqZ+eIF4LjvxKXGOdI79JRdve9NPhQo07+uqGQgE4imwNnRx7PFtCRryiIEcUoavuNtuRVoBAm6qdB0SrctgaqGfLgKvZHOnwTjyNqjBUxzMeQlEC2czEMSwIDAQAB",
                2)), 2);
        return URLEncoder.encode(encodeString, "UTF-8");
    }

    private static byte[] a(byte[] bArr, byte[] bArr2) throws Exception {
        Key generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bArr2));
        Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        instance.init(1, generatePublic);
        return instance.doFinal(bArr);
    }
}
