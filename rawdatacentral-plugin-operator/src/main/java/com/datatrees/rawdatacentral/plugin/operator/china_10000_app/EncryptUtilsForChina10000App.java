package com.datatrees.rawdatacentral.plugin.operator.china_10000_app;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * 电信营业厅
 * 安卓App
 * 请求数据和响应页面的加解密工具
 *
 * Created by guimeichao on 17/8/17.
 */
public class EncryptUtilsForChina10000App {

    private static final String paramKey = "1234567`90koiuyhgtfrdewsaqaqsqde";
    private static final String cipherInstance = "DESede/CBC/PKCS5Padding";
    private static final String algorithmName = "DESede";
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
        byte[] data = hexStrToBytes(paramString);
        byte[] keyBytes = subKey(paramKey);
        SecretKeySpec key = new SecretKeySpec(keyBytes, algorithmName);
        Cipher cipher = Cipher.getInstance(cipherInstance);
        byte[] iv = getIVParameter(cipher.getBlockSize());
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(2, key, ivParameterSpec);
        return new String(cipher.doFinal(data));
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
}
