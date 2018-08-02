/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * AES codec util
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年9月6日 下午3:10:07
 */
public class CodecUtils {

    public static final  String    CIPHER_ALGORITHM  = "AES/ECB/PKCS5Padding";

    private static final SecretKey DEFAULT_SecretKey = new SecretKeySpec("DATATREEDATATREE".getBytes(), "AES");

    public static byte[] decrypt(byte[] data) throws Exception {
        return decrypt(data, DEFAULT_SecretKey);
    }

    public static byte[] encrypt(byte[] data) throws Exception {
        return encrypt(data, DEFAULT_SecretKey);
    }

    public static byte[] decrypt(byte[] data, String keyString) throws Exception {
        return decrypt(data, new SecretKeySpec(keyString.getBytes(), "AES"));
    }

    public static byte[] encrypt(byte[] data, String keyString) throws Exception {
        return encrypt(data, new SecretKeySpec(keyString.getBytes(), "AES"));
    }

    private static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.encodeBase64(cipher.doFinal(data));
    }

    private static byte[] decrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(Base64.decodeBase64(data));
    }

}
