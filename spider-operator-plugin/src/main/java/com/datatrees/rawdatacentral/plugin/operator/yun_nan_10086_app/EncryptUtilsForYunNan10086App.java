package com.datatrees.rawdatacentral.plugin.operator.yun_nan_10086_app;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by guimeichao on 17/8/28.
 */
public class EncryptUtilsForYunNan10086App {
    private static final byte[] IV;
    private static final String KEY = "!@#j*&!k";

    static {
        byte[] arrayOfByte = new byte[8];
        arrayOfByte[0] = 1;
        arrayOfByte[1] = 2;
        arrayOfByte[2] = 3;
        arrayOfByte[3] = 4;
        arrayOfByte[4] = 5;
        arrayOfByte[5] = 6;
        arrayOfByte[6] = 7;
        arrayOfByte[7] = 8;
        IV = arrayOfByte;
    }

    private static byte[] encrypt(String key, byte[] data) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(1, keySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception localException) {
            localException.printStackTrace();
        }

        return null;
    }

    private static String sign(String text, String salt) throws UnsupportedEncodingException {
        String content = salt + text;

        return DigestUtils.md5Hex(content.getBytes("UTF-8"));
    }

    private static String getDeviceId(){
        return "D7F40D126FE979D7C24E5FB874DBB84D";
    }

    public static String getEncryptString(String text) {
        byte[] data = encrypt(KEY, text.getBytes());
        return new String(Base64.encodeBase64(data));
    }

    public static String md5sign(String text) {
        String sign;
        try {
            sign = sign(text, "11100android!@#" + getDeviceId());
            return sign.toUpperCase();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
