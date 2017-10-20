/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods for working on GZIPed data.
 */
public class GZIPUtils {

    private static final Logger LOG                        = LoggerFactory.getLogger(GZIPUtils.class);
    private static final int    EXPECTED_COMPRESSION_RATIO = 5;
    private static final int    BUF_SIZE                   = 4096;

    /**
     * Returns an gunzipped copy of the input array. If the gzipped input has been truncated or
     * corrupted, a best-effort attempt is made to unzip as much as possible. If no data can be
     * extracted <code>null</code> is returned.
     */
    public static final byte[] unzipBestEffort(byte[] in) {
        return unzipBestEffort(in, Integer.MAX_VALUE);
    }

    /**
     * Returns an gunzipped copy of the input array, truncated to <code>sizeLimit</code> bytes, if
     * necessary. If the gzipped input has been truncated or corrupted, a best-effort attempt is
     * made to unzip as much as possible. If no data can be extracted <code>null</code> is returned.
     */
    public static final byte[] unzipBestEffort(byte[] in, int sizeLimit) {
        try {
            // decompress using GZIPInputStream
            ByteArrayOutputStream outStream = new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

            CustomGzipStream inStream = new CustomGzipStream(new ByteArrayInputStream(in));

            byte[] buf = new byte[BUF_SIZE];
            int written = 0;
            while (true) {
                try {
                    int size = inStream.read(buf);
                    if (size <= 0) break;
                    if ((written + size) > sizeLimit) {
                        outStream.write(buf, 0, sizeLimit - written);
                        break;
                    }
                    outStream.write(buf, 0, size);
                    written += size;
                } catch (Exception e) {
                    break;
                }
            }
            try {
                outStream.close();
            } catch (IOException e) {}

            return outStream.toByteArray();

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Returns an gunzipped copy of the input array.
     * @exception IOException if the input cannot be properly decompressed
     */
    public static final byte[] unzip(byte[] in) throws IOException {
        // decompress using GZIPInputStream
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(EXPECTED_COMPRESSION_RATIO * in.length);

        CustomGzipStream inStream = new CustomGzipStream(new ByteArrayInputStream(in));

        byte[] buf = new byte[BUF_SIZE];
        while (true) {
            int size = inStream.read(buf);
            if (size <= 0) break;
            outStream.write(buf, 0, size);
        }
        outStream.close();

        return outStream.toByteArray();
    }

    /**
     * Returns an gzipped copy of the input array.
     */
    public static final byte[] zip(byte[] in) {
        try {
            // compress using GZIPOutputStream
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream(in.length / EXPECTED_COMPRESSION_RATIO);

            GZIPOutputStream outStream = new GZIPOutputStream(byteOut);

            try {
                outStream.write(in);
            } catch (Exception e) {
                LOG.error("Failed to get outStream.write input", e);
            }

            try {
                outStream.close();
            } catch (IOException e) {
                LOG.error("Failed to implement outStream.close", e);
            }

            return byteOut.toByteArray();

        } catch (IOException e) {
            LOG.error("Failed with IOException", e);
            return null;
        }
    }

}