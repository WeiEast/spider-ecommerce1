package com.datatrees.rawdatacentral.common.utils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhouxinghai on 2017/7/14.
 */
public class ClassLoaderUtils {

    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class);

    public static Class loadClass(File jarFile, String className) {
        try {
            URL url = new URL("file", null, jarFile.getAbsolutePath());
            URLClassLoader loader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
            return loader.loadClass(className);
        } catch (Throwable e) {
            logger.error("loadClass error jarFile={},className={}", jarFile.getAbsolutePath(), className);
            throw new RuntimeException("loadClass error jarFile=" + jarFile.getAbsolutePath() + ",className=" + className);
        }
    }

    public static ClassLoader createClassLoader(File jarFile) {
        try {
            if(!jarFile.exists()){
                logger.error("jar file not found,fileName={}",jarFile.getAbsolutePath());
            }
            URL url = new URL("file", null, jarFile.getAbsolutePath());
            URLClassLoader loader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
            logger.info("createClassLoader success jarFile={}", jarFile.getAbsolutePath());
            return loader;
        } catch (Throwable e) {
            logger.error("createClassLoader error jarFile={}", jarFile.getAbsolutePath());
            throw new RuntimeException("createClassLoader error jarFile=" + jarFile.getAbsolutePath());
        }

    }
}
