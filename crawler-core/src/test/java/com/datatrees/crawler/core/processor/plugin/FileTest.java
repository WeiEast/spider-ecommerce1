/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.
 * @since Feb 19, 2014 5:30:29 PM
 */
public class FileTest {

    @Test
    public void testFileToURL() {
        File f = new File("/tmp/aa.jpg");
        URI uri = f.toURI();

        System.out.println(uri.getFragment());
        System.out.println(uri.toString());

        f = new File(uri);
        System.out.println(f.getAbsolutePath());
        try {
            URL url = f.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
