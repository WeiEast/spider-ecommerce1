package com.datatrees.rawdatacentral.submitter.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipCompressUtils {
    private static final Logger logger = LoggerFactory.getLogger(ZipCompressUtils.class);

    private static final int BUFFER = 8192;

    public static void compress(String zipPathName, String srcPathName) {
        logger.debug("start compress file: " + srcPathName);
        File zipFile = new File(zipPathName);
        File file = new File(srcPathName);
        if (!file.exists()) throw new RuntimeException(srcPathName + "not exist！");
        FileOutputStream fileOutputStream = null;
        CheckedOutputStream cos = null;
        ZipOutputStream out = null;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            out = new ZipOutputStream(cos);
            String basedir = "";
            compress(file, out, basedir);
        } catch (Exception e) {
            logger.error("compress unknown exception", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (cos != null) cos.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void compress(String zipPathName, Map<String, SubmitFile> compressFileMap) {
        File zipFile = new File(zipPathName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(zipFile);
            compress(fileOutputStream, compressFileMap);
        } catch (Exception e) {
            logger.error("compress unknown exception", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public static void compress(OutputStream output, Map<String, SubmitFile> compressFileMap) {
        logger.debug("start compress file: " + compressFileMap.keySet());
        CheckedOutputStream cos = null;
        ZipOutputStream out = null;
        try {
            cos = new CheckedOutputStream(output, new CRC32());
            out = new ZipOutputStream(cos);
            for (Entry<String, SubmitFile> entry : compressFileMap.entrySet()) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(entry.getValue().getValue());
                String basedir = "";
                try {
                    String fileName =
                            StringUtils.isNotBlank(entry.getValue().getFileName()) ? entry.getValue().getFileName() : entry.getKey() + ".html";
                    compressInputStream(inputStream, out, basedir, fileName);
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("compress unknown exception", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (out != null) out.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            try {
                if (cos != null) cos.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void compress(File file, ZipOutputStream out, String basedir) {
        if (file.isDirectory()) {
            logger.debug("compress dir: " + basedir + file.getName());
            compressDirectory(file, out, basedir);
        } else {
            logger.debug("compress file: " + basedir + file.getName());
            compressFile(file, out, basedir);
        }
    }

    private static void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()) return;

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            compress(files[i], out, basedir + dir.getName() + "/");
        }
    }

    private static void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            compressInputStream(fis, out, basedir, file.getName());
        } catch (Exception e) {
            logger.error("compress inputStream error!", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (fis != null) fis.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static void compressInputStream(InputStream input, ZipOutputStream out, String basedir, String name) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(input);
            ZipEntry entry = new ZipEntry(basedir + name);
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            logger.error("compress inputStream error!", e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (bis != null) bis.close();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
