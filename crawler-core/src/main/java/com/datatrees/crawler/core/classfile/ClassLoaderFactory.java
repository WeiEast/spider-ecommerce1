package com.datatrees.crawler.core.classfile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 09:59 11/08/2017
 */
public final class ClassLoaderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderFactory.class);

    private ClassLoaderFactory() {
    }

    public static ClassLoader create(final File file, final ClassLoader parent) throws MalformedURLException {
        File[] files = file != null ? new File[]{file} : new File[0];
        return create(files, parent);
    }

    public static ClassLoader create(final File[] files, final ClassLoader parent) throws MalformedURLException {
        List<URL> urls = toURL(files);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Instantiate URLClassLoader. Url size: {}, parent: {}", files.length, parent);
        }
        if (CollectionUtils.isNotEmpty(urls)) {
            URL[] array = urls.toArray(new URL[urls.size()]);
            if (parent != null) {
                return URLClassLoader.newInstance(array, parent);
            }
            return URLClassLoader.newInstance(array);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Can not instantiate URLClassLoader because files is empty!");
        }

        return parent != null ? parent : ClassLoaderFactory.class.getClassLoader();
    }

    private static List<URL> toURL(File[] files) throws MalformedURLException {
        List<URL> list = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(files)) {
            for (File file : files) {
                if (!file.exists() || !file.canRead() || !file.getName().endsWith(".jar")) {
                    continue;
                }

                if (file.isFile()) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Loading jar file: {}", file.getAbsolutePath());
                    }
                    list.add(file.toURI().toURL());
                } else if (file.isDirectory()) {
                    File[] children = file.listFiles();
                    list.addAll(toURL(children));
                }
            }
        }

        return list;
    }

}
