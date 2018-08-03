package com.datatrees.spider.share.service.plugin.operator.china_10000_app;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * 电信营业厅
 * 安卓App
 * 请求数据和响应页面的加解密工具
 * Created by guimeichao on 17/8/17.
 */
public class EncryptUtilsForChina10000App {

    private static final String paramKey       = "1234567`90koiuyhgtfrdewsaqaqsqde";

    private static final String cipherInstance = "DESede/CBC/PKCS5Padding";

    private static final String algorithmName  = "DESede";

    private static final String encryptCharset = "UTF-8";

    public static String encrypt(String paramString) throws Exception {
        byte[] paramBytes = paramString.getBytes(encryptCharset);
        byte[] keyBytes = subKey(paramKey);
        Cipher cipher = Cipher.getInstance(cipherInstance);
        byte[] iv = getIVParameter(cipher.getBlockSize());
        SecretKeySpec key = new SecretKeySpec(keyBytes, algorithmName);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(1, key, ivParameterSpec);
        return bytesToHexStr(cipher.doFinal(paramBytes));
    }

    private static final String bytesToHexStr(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2) sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] subKey(String paramString) throws UnsupportedEncodingException {
        int i = 24;
        byte[] arrayOfByte1 = new byte[i];
        byte[] arrayOfByte2 = paramString.getBytes(encryptCharset);
        int j = arrayOfByte1.length;
        int k = arrayOfByte2.length;
        if (j > k) {
            j = k;
        }
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
        return arrayOfByte1;
    }

    private static byte[] getIVParameter(int blockSize) {
        int i = blockSize;
        byte[] iv = new byte[i];
        int j = 0;
        while (j < i) {
            iv[j] = 0;
            j += 1;
        }
        return iv;
    }

    public static String decrypt(String paramString) throws Exception {
        byte[] data = hexStrToBytes(paramString.toUpperCase());
        byte[] keyBytes = subKey(paramKey);
        SecretKeySpec key = new SecretKeySpec(keyBytes, algorithmName);
        Cipher cipher = Cipher.getInstance(cipherInstance);
        byte[] iv = getIVParameter(cipher.getBlockSize());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(2, key, ivParameterSpec);
        byte[] bytes = cipher.doFinal(data);
        return new String(bytes);
    }

    private static byte[] hexStrToBytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static void main(String[] args) throws Exception {
        String data
                = "b5720f816f50db5eb94116fd795b9f770f4af1f252692aa8c138f0e8150856db0b52b7c8000a7be699aabc4ab106f380f9e488a10e8269792beb5b46a667cdf32e20cf7649e74841dcfc49d871e100bda5b005efdca1abf6d8f95b802b6db01dc0bc44d9f75be7b899fcac6bf3674bff51429cb76f9ea218fa2bad0b88a6c6c80d9edbb96f284e26c08a514ffe89869973b1d297873df3041a4446800efd93063b1d35423c78d4d090d96e5351d55d86891107c4e8ed7f25da3b29ef2c37d8f0b2718772ddd7794b8a07545ac46d094ab3eb5ec9b4c7c1cd22cbf377c5fcf3a45128168e49d6a7e8fef99088ad75bc260462f1dfb1cbd1e7aeca157582d8f59276b2dc0f72bddffb54c7428055be0b04176dc7a24d0bde3a30be210d781efee7fc1b4580ac12851e683c4894e3e7976e0e54972382d328e9dfb1b2c5c3aa4491d7f05be92c18db353a8e9ea45e60eb1d62097a81e6ce2019af0b77ff14ba6947f7fecc96f6a11900e89ae6c3a96e721e8bcd68d332409a924f885d3f4f332aaed2ad6faa7edc709876a9e07ff2c8b0bc90af0db6c19d11380d332a39414b001a48cdd127ef5590dfdbe37b3b7c225f94e138c1542fcbe4e2f7e8879b2829645a9e191f6b26ae3efe95a350bb6549e07a1acae8f264e03019b02f41a4f299b5d7603172a6a72728b264522d09fddc9cb4e82316a88eefc79e42d4bc88adcf09a234c1533c55e9874652aebd7a106e82d1181aaa77e3e9064aa2b6a366889e63e235bfd66ebb3c604ca4b51c92bc1fcbf3";
        String decrypt = EncryptUtilsForChina10000App.decrypt(data);
        System.out.println(decrypt);
    }
}
