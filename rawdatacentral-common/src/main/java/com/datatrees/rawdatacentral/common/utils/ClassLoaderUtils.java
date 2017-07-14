package com.datatrees.rawdatacentral.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
public class ClassLoaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class);

    public static Class loadClass(File jarFile, String className) {
        try {
            URL url = new URL("file", null, jarFile.getAbsolutePath());
            URLClassLoader loader = new URLClassLoader(new URL[] { url },
                Thread.currentThread().getContextClassLoader());
            return loader.loadClass(className);
        } catch (Exception e) {
            logger.error("loadClass error jarFile={},className={}", jarFile.getAbsolutePath(), className);
            throw new RuntimeException(
                "loadClass error jarFile=" + jarFile.getAbsolutePath() + ",className=" + className);
        }

    }
}
