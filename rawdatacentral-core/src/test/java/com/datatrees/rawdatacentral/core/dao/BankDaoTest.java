/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.rawdatacentral.core.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.datatrees.rawdatacentral.core.AbstractTest;

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月28日 下午12:00:05
 */
public class BankDaoTest extends AbstractTest {
    @Autowired
    private BankDao bankDao;
    
    @Test
    public void doTest() {
        System.out.println(bankDao.getAllBank());
        System.out.println(bankDao.getAllBank().size());

    }
}
