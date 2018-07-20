/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.datatrees.rawdatacentral.plugin.operator.china_10010_app;

import java.nio.charset.StandardCharsets;

public class Base64
{

    protected static byte[] getAlphabet(int options)
    {
        if((options & 8) == 8)
            return _URL_SAFE_ALPHABET;
        else
            return _STANDARD_ALPHABET;
    }

    protected static byte[] getDecodabet(int options)
    {
        if((options & 8) == 8)
            return _URL_SAFE_DECODABET;
        else
            return _STANDARD_DECODABET;
    }

    private Base64()
    {
    }

    protected static int encode3to4(byte source[], int srcOffset, int numSigBytes, byte destination[], int destOffset, boolean noPadding, byte alphabet[])
    {
        int inBuff = (numSigBytes <= 0 ? 0 : (source[srcOffset] << 24) >>> 8) | (numSigBytes <= 1 ? 0 : (source[srcOffset + 1] << 24) >>> 16) | (numSigBytes <= 2 ? 0 : (source[srcOffset + 2] << 24) >>> 24);
        switch(numSigBytes)
        {
        case 3: // '\003'
            destination[destOffset] = alphabet[inBuff >>> 18];
            destination[destOffset + 1] = alphabet[inBuff >>> 12 & 63];
            destination[destOffset + 2] = alphabet[inBuff >>> 6 & 63];
            destination[destOffset + 3] = alphabet[inBuff & 63];
            return 4;

        case 2: // '\002'
            destination[destOffset] = alphabet[inBuff >>> 18];
            destination[destOffset + 1] = alphabet[inBuff >>> 12 & 63];
            destination[destOffset + 2] = alphabet[inBuff >>> 6 & 63];
            if(noPadding)
            {
                return 3;
            } else
            {
                destination[destOffset + 3] = 61;
                return 4;
            }

        case 1: // '\001'
            destination[destOffset] = alphabet[inBuff >>> 18];
            destination[destOffset + 1] = alphabet[inBuff >>> 12 & 63];
            if(noPadding)
            {
                return 2;
            } else
            {
                destination[destOffset + 2] = 61;
                destination[destOffset + 3] = 61;
                return 4;
            }
        }
        return 0;
    }

    public static String encodeToString(byte source[], int options)
    {
        return encodeToString(source, 0, source.length, options);
    }

    public static String encodeToString(byte source[], int off, int len, int options)
    {
        byte encoded[] = encode(source, off, len, options);
        return new String(encoded, StandardCharsets.UTF_8);
    }

    public static byte[] encode(byte source[], int off, int len, int options)
    {
        if(source == null)
            throw new NullPointerException("Cannot serialize a null array.");
        if(off < 0)
            throw new IllegalArgumentException((new StringBuilder()).append("Cannot have negative offset: ").append(off).toString());
        if(len < 0)
            throw new IllegalArgumentException((new StringBuilder()).append("Cannot have length offset: ").append(len).toString());
        if(off + len > source.length)
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", new Object[] {
                Integer.valueOf(off), Integer.valueOf(len), Integer.valueOf(source.length)
            }));
        if(len == 0)
            return new byte[0];
        boolean breakLines = (options & 2) == 0;
        boolean crlf = (options & 4) != 0;
        boolean noPadding = (options & 1) != 0;
        byte alphabet[] = getAlphabet(options);
        int bufLen = (len / 3) * 4 + (len % 3 <= 0 ? 0 : 4);
        if(breakLines)
        {
            int lines = bufLen / 76;
            bufLen += crlf ? lines * 2 : lines;
        }
        byte dest[] = new byte[bufLen];
        int srcPos = 0;
        int destPos = 0;
        int lineLength = 0;
        for(int len2 = len - 2; srcPos < len2; srcPos += 3)
        {
            if(breakLines && lineLength >= 76)
            {
                if(crlf)
                    dest[destPos++] = 13;
                dest[destPos++] = 10;
                lineLength = 0;
            }
            int l = encode3to4(source, srcPos + off, 3, dest, destPos, noPadding, alphabet);
            destPos += l;
            lineLength += l;
        }

        if(srcPos < len)
        {
            if(breakLines && lineLength >= 76)
            {
                if(crlf)
                    dest[destPos++] = 13;
                dest[destPos++] = 10;
            }
            destPos += encode3to4(source, srcPos + off, len - srcPos, dest, destPos, noPadding, alphabet);
        }
        if(destPos < dest.length)
        {
            byte finalOut[] = new byte[destPos];
            System.arraycopy(dest, 0, finalOut, 0, destPos);
            return finalOut;
        } else
        {
            return dest;
        }
    }

    public static byte[] encode(byte source[], int options)
    {
        return encode(source, 0, source.length, options);
    }

    protected static int decode4to3(byte src[], int len, byte dest[], int offset, byte decodabet[])
    {
        if(len == 2 || src[2] == 61 && src[3] == 61)
        {
            int outBuff = validate(decodabet, src[0]) << 18 | validate(decodabet, src[1]) << 12;
            dest[offset] = (byte)(outBuff >>> 16);
            return 1;
        }
        if(len == 3 || src[3] == 61)
        {
            int outBuff = validate(decodabet, src[0]) << 18 | validate(decodabet, src[1]) << 12 | validate(decodabet, src[2]) << 6;
            dest[offset] = (byte)(outBuff >>> 16);
            dest[offset + 1] = (byte)(outBuff >>> 8);
            return 2;
        } else
        {
            int outBuff = validate(decodabet, src[0]) << 18 | validate(decodabet, src[1]) << 12 | validate(decodabet, src[2]) << 6 | validate(decodabet, src[3]);
            dest[offset] = (byte)(outBuff >>> 16);
            dest[offset + 1] = (byte)(outBuff >>> 8);
            dest[offset + 2] = (byte)outBuff;
            return 3;
        }
    }

    private static int validate(byte decodabet[], byte from)
    {
        byte b = decodabet[from & 127];
        if(b < 0)
            throw new IllegalArgumentException(String.format("Bad Base64%s character '%s'", new Object[] {
                decodabet != _URL_SAFE_DECODABET ? "" : " url safe", escape(from)
            }));
        else
            return b;
    }

    public static byte[] decode(byte source[], int options)
    {
        if(source == null)
            throw new IllegalArgumentException("Cannot decode null source array.");
        else
            return decode(source, 0, source.length, options);
    }

    public static byte[] decode(byte source[], int offset, int len, int options)
    {
        if(source == null)
            throw new IllegalArgumentException("Cannot decode null source array.");
        if(offset < 0 || offset + len > source.length)
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", new Object[] {
                Integer.valueOf(source.length), Integer.valueOf(offset), Integer.valueOf(len)
            }));
        if(len == 0)
            return new byte[0];
        byte decodabet[] = getDecodabet(options);
        int len34 = (len * 3) / 4;
        byte outBuff[] = new byte[len34];
        int outBuffPosn = 0;
        byte b4[] = new byte[4];
        int b4Posn = 0;
        for(int i = offset; i < offset + len; i++)
        {
            byte sbiDecode = decodabet[source[i] & 127];
            if(sbiDecode >= -5)
            {
                if(sbiDecode < -1)
                    continue;
                b4[b4Posn++] = source[i];
                if(b4Posn > 3)
                {
                    outBuffPosn += decode4to3(b4, 4, outBuff, outBuffPosn, decodabet);
                    b4Posn = 0;
                }
            } else
            {
                throw new IllegalArgumentException(String.format("Bad%s Base64 character '%s' in array position %d", new Object[] {
                    decodabet != _URL_SAFE_DECODABET ? "" : " url safe", escape(source[i]), Integer.valueOf(i)
                }));
            }
        }

        if(b4Posn > 0)
            outBuffPosn += decode4to3(b4, b4Posn, outBuff, outBuffPosn, decodabet);
        if(outBuffPosn < outBuff.length)
        {
            byte out[] = new byte[outBuffPosn];
            System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
            return out;
        } else
        {
            return outBuff;
        }
    }

    public static byte[] decode(String s, int options)
    {
        if(s == null)
        {
            throw new IllegalArgumentException("Input string was null.");
        } else
        {
            byte bytes[] = s.getBytes(StandardCharsets.UTF_8);
            return decode(bytes, 0, bytes.length, options);
        }
    }

    private static String escape(byte b)
    {
        if(b == 34)
            return "\\\"";
        if(b == 39)
            return "\\'";
        if(b < 0)
            return String.format("\\u%04x", new Object[] {
                Integer.valueOf(b + 256)
            });
        if(b < 32 || b == 127)
            return String.format("\\%03o", new Object[] {
                Integer.valueOf(b)
            });
        else
            return String.valueOf((char)b);
    }

    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int CRLF = 4;
    public static final int URL_SAFE = 8;
    public static final int NO_CLOSE = 16;
    protected static final int MAX_LINE_LENGTH = 76;
    protected static final byte EQUALS_SIGN = 61;
    protected static final byte CR = 13;
    protected static final byte LF = 10;
    protected static final byte WHITE_SPACE_ENC = -5;
    private static final byte EQUALS_SIGN_ENC = -1;
    private static final byte _STANDARD_ALPHABET[] = {
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 43, 47
    };
    private static final byte _STANDARD_DECODABET[] = {
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -9, -9, -9, -9, -9
    };
    private static final byte _URL_SAFE_ALPHABET[] = {
        65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 
        75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 
        85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 
        101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 
        111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 
        121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 
        56, 57, 45, 95
    };
    private static final byte _URL_SAFE_DECODABET[] = {
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, 
        -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, 
        -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 
        54, 55, 56, 57, 58, 59, 60, 61, -9, -9, 
        -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 
        5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
        25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 
        29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
        39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
        49, 50, 51, -9, -9, -9, -9, -9
    };

}


/*
	DECOMPILATION REPORT

	Decompiled from: /Users/guimeichao/Downloads/android-util-2.2.1.jar
	Total time: 44 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/