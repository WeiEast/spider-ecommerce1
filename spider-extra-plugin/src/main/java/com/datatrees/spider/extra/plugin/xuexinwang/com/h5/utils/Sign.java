package com.datatrees.spider.extra.plugin.xuexinwang.com.h5.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Random;

/**
 * Created by zhangyanjia on 2017/12/15.
 */
public class Sign {

    /**
     * 生成Authorization签名字段
     * @param appId
     * @param secretId
     * @param secretKey
     * @param bucketName
     * @param expired
     * @return
     * @exception Exception
     */
    public static String appSign(long appId, String secretId, String secretKey, String bucketName, long expired) throws Exception {
        long now = System.currentTimeMillis() / 1000;
        int rdm = Math.abs(new Random().nextInt());
        String plainText = String.format("a=%d&b=%s&k=%s&t=%d&e=%d&r=%d", appId, bucketName, secretId, now, now + expired, rdm);
        byte[] hmacDigest = HmacSha1(plainText, secretKey);
        byte[] signContent = new byte[hmacDigest.length + plainText.getBytes().length];
        System.arraycopy(hmacDigest, 0, signContent, 0, hmacDigest.length);
        System.arraycopy(plainText.getBytes(), 0, signContent, hmacDigest.length, plainText.getBytes().length);
        return Base64Encode(signContent);
    }

    /**
     * 生成base64编码
     * @param binaryData
     * @return
     */
    public static String Base64Encode(byte[] binaryData) {
        String encodedstr = Base64.getEncoder().encodeToString(binaryData);
        return encodedstr;
    }

    /**
     * 生成hmacsha1签名
     * @param binaryData
     * @param key
     * @return
     * @exception Exception
     */
    public static byte[] HmacSha1(byte[] binaryData, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(secretKey);
        byte[] HmacSha1Digest = mac.doFinal(binaryData);
        return HmacSha1Digest;
    }

    /**
     * 生成hmacsha1签名
     * @param plainText
     * @param key
     * @return
     * @exception Exception
     */
    public static byte[] HmacSha1(String plainText, String key) throws Exception {
        return HmacSha1(plainText.getBytes(), key);
    }

    //    public static void main(String[] args) {
    //        try {
    //            String str = appSign(1255662428, "AKIDhHk3A5hN2Zu3IPC2X7ZUDj3BBzb0jE5G", "7b6Wow8LckgQ1RfmmaCcrvscAVovugJy", "zyjtest1", 2592000L);
    //            System.out.println("------"+str);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

}
