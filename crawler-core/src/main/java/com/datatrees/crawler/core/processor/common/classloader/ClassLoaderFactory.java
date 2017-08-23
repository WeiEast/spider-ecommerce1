/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.common.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 5:47:48 PM
 */
public final class ClassLoaderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderFactory.class);

    private ClassLoaderFactory() {
    }

    /**
     * Create and return a new class loader, based on the configuration defaults
     * and the specified directory paths:
     * @param unpacked Array of pathnames to unpacked directories that should be
     *                 added to the repositories of the class loader, or
     *                 <code>null</code> for no unpacked directories to be considered
     * @param packed   Array of pathnames to directories containing JAR files that
     *                 should be added to the repositories of the class loader, or
     *                 <code>null</code> for no directories of JAR files to be
     *                 considered
     * @param parent   Parent class loader for the new class loader, or
     *                 <code>null</code> for the system class loader.
     * @exception Exception if an error occurs constructing the class loader
     */
    public static ClassLoader createClassLoader(File unpacked[], File packed[], ClassLoader parent) throws Exception {

        LOGGER.debug("Creating new class loader");

        // Construct the "class path" for this class loader
        ArrayList<URL> list = new ArrayList<URL>();

        // Add unpacked directories
        if (unpacked != null) {
            for (int i = 0; i < unpacked.length; i++) {
                File file = unpacked[i];
                if (!file.isDirectory() || !file.exists() || !file.canRead()) {
                    continue;
                }
                LOGGER.debug("  Including directory " + file.getAbsolutePath());
                URL url = new URL("file", null, file.getCanonicalPath() + File.separator);
                list.add(url);
            }
        }

        // Add packed directory JAR files
        if (packed != null) {
            for (int i = 0; i < packed.length; i++) {
                File directory = packed[i];
                if (directory.isFile()) {
                    LOGGER.debug("Including jar file " + directory.getAbsolutePath());
                    URL url = new URL("file", null, directory.getCanonicalPath());
                    list.add(url);
                } else {
                    String filenames[] = directory.list();
                    for (int j = 0; j < filenames.length; j++) {
                        String filename = filenames[j].toLowerCase();
                        if (!filename.endsWith(".jar")) continue;
                        File file = new File(directory, filenames[j]);
                        LOGGER.debug("Including jar file " + file.getAbsolutePath());
                        URL url = new URL("file", null, file.getCanonicalPath());
                        list.add(url);
                    }
                }

            }
        }

        // Construct the class loader itself
        URL array[] = list.toArray(new URL[list.size()]);
        URLClassLoader classLoader = null;
        if (parent == null) classLoader = new URLClassLoader(array);
        else classLoader = new URLClassLoader(array, parent);
        return (classLoader);
    }
}
