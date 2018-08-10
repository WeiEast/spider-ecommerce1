package com.datatrees.spider.operator.plugin.china_10010_app;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncryptUtilForChina10010App {

    public static String encryString(String s) {
        return f.a(a.a(s.getBytes(), f.a("f6b0d3f905bf02939b4f6d29f257c2ab"), f.a("1a42eb4565be8628a807403d67dce78d")));
    }
}

class f {

    public static String a(byte abyte0[]) {
        return b.b(abyte0);
    }

    public static byte[] a(String s) {
        return b.a(s.toCharArray());
    }
}

class b {

    private static final char   a[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char   b[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String c = "";

    protected static int a(char c1, int i) {
        int j = Character.digit(c1, 16);
        if (j == -1) throw new RuntimeException();
        else return j;
    }

    public static byte[] a(char ac[]) {
        int j = 0;
        int k = ac.length;
        if ((k & 1) != 0) throw new RuntimeException();
        byte abyte0[] = new byte[k >> 1];
        int i = 0;
        do {
            if (j >= k) return abyte0;
            int l = a(ac[j], j);
            j++;
            int i1 = a(ac[j], j);
            j++;
            abyte0[i] = (byte) ((l << 4 | i1) & 255);
            i++;
        } while (true);
    }

    public static char[] a(byte abyte0[]) {
        return a(abyte0, true);
    }

    public static char[] a(byte abyte0[], boolean flag) {
        char ac[];
        if (flag) ac = a;
        else ac = b;
        return a(abyte0, ac);
    }

    protected static char[] a(byte abyte0[], char ac[]) {
        int j = 0;
        int k = abyte0.length;
        char ac1[] = new char[k << 1];
        int i = 0;
        do {
            if (i >= k) return ac1;
            int l = j + 1;
            ac1[j] = ac[(abyte0[i] & 240) >>> 4];
            j = l + 1;
            ac1[l] = ac[abyte0[i] & 15];
            i++;
        } while (true);
    }

    public static String b(byte abyte0[]) {
        return new String(a(abyte0));
    }

    public String toString() {
        return super.toString() + "[charsetName=" + c + "]";
    }

}

class a {

    private static SecureRandom a = new SecureRandom();

    public static byte[] a(byte abyte0[], byte abyte1[], byte abyte2[]) {
        return a(abyte0, abyte1, abyte2, 1);
    }

    private static byte[] a(byte abyte0[], byte[] abyte1, byte abyte2[], int i) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(i, new SecretKeySpec(abyte1, "AES"), new IvParameterSpec(abyte2));
            abyte0 = cipher.doFinal(abyte0);
        } catch (Exception e) {
            abyte0 = "".getBytes();
        }

        return abyte0;
    }

}


