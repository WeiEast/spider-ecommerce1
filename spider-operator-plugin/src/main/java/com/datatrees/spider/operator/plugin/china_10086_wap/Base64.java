package com.datatrees.spider.operator.plugin.china_10086_wap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by guimeichao on 2018/7/25.
 */
public class Base64 {
    private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    private static int decode(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 65;
        }
        if (c >= 'a' && c <= 'z') {
            return (c - 97) + 26;
        }
        if (c >= '0' && c <= '9') {
            return ((c - 48) + 26) + 26;
        }
        switch (c) {
            case 43:
                return 62;
            case '/':
                return 63;
            case '=':
                return 0;
            default:
                throw new RuntimeException("unexpected code: " + c);
        }
    }

    private static void decode(String s, OutputStream os) throws IOException {
        int i = 0;
        int length = s.length();
        while (true) {
            if (i < length && s.charAt(i) <= ' ') {
                i++;
            } else if (i != length) {
                int decode = (((decode(s.charAt(i)) << 18) + (decode(s.charAt(i + 1)) << 12)) + (decode(s.charAt(i + 2)) << 6)) + decode(s.charAt(i + 3));
                os.write((decode >> 16) & 255);
                if (s.charAt(i + 2) != '=') {
                    os.write((decode >> 8) & 255);
                    if (s.charAt(i + 3) != '=') {
                        os.write(decode & 255);
                        i += 4;
                    } else {
                        return;
                    }
                }
                return;
            } else {
                return;
            }
        }
    }

    public static byte[] decode(String s) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            decode(s, byteArrayOutputStream);
            byte[] toByteArray = byteArrayOutputStream.toByteArray();
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                System.err.println("Error while decoding BASE64: " + e.toString());
            }
            return toByteArray;
        } catch (IOException e2) {
            throw new RuntimeException();
        }
    }

    public static String encode(byte[] data) {
        int length = data.length;
        StringBuffer stringBuffer = new StringBuffer((data.length * 3) / 2);
        int i = length - 3;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        while (i2 <= i) {
            i4 = (((data[i2] & 255) << 16) | ((data[i2 + 1] & 255) << 8)) | (data[i2 + 2] & 255);
            stringBuffer.append(legalChars[(i4 >> 18) & 63]);
            stringBuffer.append(legalChars[(i4 >> 12) & 63]);
            stringBuffer.append(legalChars[(i4 >> 6) & 63]);
            stringBuffer.append(legalChars[i4 & 63]);
            i2 += 3;
            int i5 = i3 + 1;
            if (i3 >= 14) {
                i5 = 0;
                stringBuffer.append(" ");
            }
            i3 = i5;
        }
        if (i2 == (0 + length) - 2) {
            i4 = ((data[i2] & 255) << 16) | ((data[i2 + 1] & 255) << 8);
            stringBuffer.append(legalChars[(i4 >> 18) & 63]);
            stringBuffer.append(legalChars[(i4 >> 12) & 63]);
            stringBuffer.append(legalChars[(i4 >> 6) & 63]);
            stringBuffer.append("=");
        } else if (i2 == (0 + length) - 1) {
            i4 = (data[i2] & 255) << 16;
            stringBuffer.append(legalChars[(i4 >> 18) & 63]);
            stringBuffer.append(legalChars[(i4 >> 12) & 63]);
            stringBuffer.append("==");
        }
        return stringBuffer.toString();
    }
}
