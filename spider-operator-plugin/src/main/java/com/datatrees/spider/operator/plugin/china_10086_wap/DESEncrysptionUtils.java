package com.datatrees.spider.operator.plugin.china_10086_wap;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DESEncrysptionUtils {

    private static byte[] iv = new byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7, (byte) 8};

    public static String decryptDES(String decryptString) {
        try {
            byte[] decode = Base64.decode(decryptString);
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Key secretKeySpec = new SecretKeySpec("cmcczyzx".getBytes(), "DES");
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return new String(instance.doFinal(decode));
        } catch (Exception e) {
            e.printStackTrace();
            return decryptString;
        }
    }

    public static String encryptDES(String encryptString) {
        try {
            AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            Key secretKeySpec = new SecretKeySpec("cmcczyzx".getBytes(), "DES");
            Cipher instance = Cipher.getInstance("DES/CBC/PKCS5Padding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            encryptString = Base64.encode(instance.doFinal(encryptString.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptString;
    }

    private static final String ALGORITHM     = "DES";

    public static final  String APP_10086     = "app10086";

    public static final  String APP_SHARE_KEY = "asiainfo";

    private DESEncrysptionUtils() {
    }

    public static String byte2hex(byte[] b) {
        String str = "";
        for (byte b2 : b) {
            String toHexString = Integer.toHexString(b2 & 255);
            str = toHexString.length() == 1 ? str + "0" + toHexString : str + toHexString;
        }
        return str.toUpperCase();
    }

    public static byte[] decode(byte[] input,
            byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Key secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher instance = Cipher.getInstance(ALGORITHM);
        instance.init(2, secretKeySpec);
        return instance.doFinal(input);
    }

    public static String decrypt(String str, String key) {
        try {
            return new String(decode(hex2byte(str), key.getBytes()));
        } catch (Exception e) {
            return str;
        }
    }

    public static byte[] encode(byte[] input,
            byte[] key) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        Key secretKeySpec = new SecretKeySpec(key, ALGORITHM);
        Cipher instance = Cipher.getInstance(ALGORITHM);
        instance.init(1, secretKeySpec);
        return instance.doFinal(input);
    }

    public static String encrypt(String content, String key) {
        String str = content;
        try {
            str = byte2hex(encode(content.getBytes(), key.getBytes()));
        } catch (Exception e) {
        }
        return str;
    }

    public static byte[] getKey() throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(ALGORITHM).generateKey().getEncoded();
    }

    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] toCharArray = hex.toCharArray();
        byte[] bArr = new byte[(hex.length() / 2)];
        int i = 0;
        int length = hex.length();
        int i2 = 0;
        while (i2 < length) {
            int i3 = i2 + 1;
            String valueOf = String.valueOf(toCharArray[i2]);
            String valueOf2 = String.valueOf(toCharArray[i3]);
            StringBuilder stringBuilder = new StringBuilder(valueOf);
            stringBuilder.append(valueOf2);
            bArr[i] = Integer.valueOf(Integer.parseInt(stringBuilder.toString(), 16) & 255).byteValue();
            i++;
            i2 = i3 + 1;
        }
        return bArr;
    }

    public static byte[] md5(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("MD5");
        instance.update(input);
        return instance.digest();
    }

    public static String getMD5(byte[] source) {
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(source);
            byte[] digest = instance.digest();
            char[] cArr2 = new char[32];
            int i = 0;
            for (int i2 = 0; i2 < 16; i2++) {
                byte b = digest[i2];
                int i3 = i + 1;
                cArr2[i] = cArr[(b >>> 4) & 15];
                i = i3 + 1;
                cArr2[i3] = cArr[b & 15];
            }
            return new String(cArr2);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static void addCommonPara(TreeMap requestParams) {
        requestParams.put("deviceType", "101");
        requestParams.put("cityCode", "111");
        requestParams.put("versionCode", 43);
        requestParams.put("userAgent", "android");
        requestParams.put("mobileSdkVersion", 26);
        requestParams.put("mobileBrand", "Xiaomi");
        requestParams.put("mobileType", "Mi Note 2");
        requestParams.put("mobileSystemVersion", "8.0.0");
        requestParams.put("ext3", "4G");
        requestParams.put("mobileTime", System.currentTimeMillis());
    }

    public static String encryptParams(TreeMap requestParams) {
        if (requestParams == null) {
            requestParams = new TreeMap();
        }
        addCommonPara(requestParams);
        StringBuilder map2Str = map2Str(requestParams);
        String ss = map2Str.toString();
        map2Str.append("&").append("sign=").append(DESEncrysptionUtils.getMD5((ss + "cmcczyzx2016").getBytes()));
        return DESEncrysptionUtils.encrypt(map2Str.toString(), DESEncrysptionUtils.APP_10086);
    }

    public static Map getRequestParam(TreeMap requestParams) {
        Map hashMap = new HashMap();
        hashMap.put("enDesParams", map2Str(requestParams).toString());
        return hashMap;
    }

    public static StringBuilder map2Str(TreeMap requestParams) {
        StringBuilder stringBuilder = new StringBuilder();
        if (requestParams != null) {
            for (Object str : requestParams.keySet()) {
                stringBuilder.append(str).append("=").append(requestParams.get(str)).append("&");
            }
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf("&"));
        }
        return stringBuilder;
    }

    public static String signParams(String operation, String moduleId, TreeMap requestParams, String service) {
        if (requestParams == null) {
            requestParams = new TreeMap();
        }
        requestParams.put("service", service);
        requestParams.put("operation", operation);
        requestParams.put("moduleId", moduleId);
        return signParams(requestParams);
    }

    public static String signParams(TreeMap requestParams) {
        Map hashMap = new HashMap();
        return "enDesParams=" + encryptParams(requestParams);
    }

    public static String signParams(TreeMap requestParams, String operation, String service) {
        if (requestParams == null) {
            requestParams = new TreeMap();
        }
        requestParams.put("service", service);
        requestParams.put("operation", operation);
        return signParams(requestParams);
    }
}