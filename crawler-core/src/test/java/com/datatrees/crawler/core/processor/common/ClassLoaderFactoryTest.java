/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 6:00:36 PM
 */
/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 19, 2014 6:00:36 PM
 */

package com.datatrees.crawler.core.processor.common;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.datatrees.crawler.core.processor.common.classloader.ClassLoaderFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ClassLoaderFactoryTest {

    @Ignore
    @Test
    public void testGetClassLoader() {
        List<File> unpaked = new ArrayList<File>();
        List<File> paked = new ArrayList<File>();
        File f = new File("./src/test/java/cn/vobile/vt/core");
        System.out.println(f.getAbsolutePath());
        unpaked.add(f);
        try {
            ClassLoader loader = ClassLoaderFactory.createClassLoader(unpaked.toArray(new File[unpaked.size()]), paked.toArray(new File[paked.size()]), this.getClass().getClassLoader());
            Class clazz = loader.loadClass("TestClass");
            Assert.assertNotNull(clazz);
            // System.out.println(clazz.getSimpleName());
            // System.out.println(ClassLoaderFactoryTest.class.getClassLoader());
            // System.out.println(clazz.getClassLoader());
            Assert.assertTrue(clazz.getClassLoader() instanceof URLClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
