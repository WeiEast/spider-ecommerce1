/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * 
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import com.datatrees.crawler.core.processor.common.ReplaceUtils;

/**
 * 
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 21, 2014 4:20:12 PM
 */
public class TestReplaceUtils {

//    @Ignore
    @Test
    public void testGetReplaceList() {
        String template = "http://bai${TT}yy,com${DD}/have/${TT}/dd${page,1,10+,1}";
        Set<String> replaceList = ReplaceUtils.getReplaceList(template);
        int expected = 3 ; 
        Assert.assertEquals(expected, replaceList.size());
    }
    
    @Test
    public void testReplaceWithContext() {
        String template = "${a2.a1.TT}http://bai${TT}yy,com${DD}/have/${TT}/dd${page,1,10+,1}";
        Set<String> replaceList = ReplaceUtils.getReplaceList(template);
        int expected = 4;
        Assert.assertEquals(expected, replaceList.size());
        
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("TT", "^*^"); 
        
        Map<String, Object> context1 = new HashMap<String, Object>();
        context1.put("a1", context);
        Map<String, Object> context2 = new HashMap<String, Object>();
        context2.put("a2", context1);
        context2.put("TT", "2323");


        String result = ReplaceUtils.replaceMap(replaceList, context2, template);
        String expectedResult = "http://bai^*^yy,com${DD}/have/^*^/dd${page,1,10+,1}";
        System.out.println(result);
    }
    
    @Test
    public void testReplaceWithDefaultContext() {
        String template = "http://bai${TT}yy,com${DD}/have/${TT}/dd${page,1,10+,1}";
        Set<String> replaceList = ReplaceUtils.getReplaceList(template);
        int expected = 3 ; 
        Assert.assertEquals(expected, replaceList.size());
        
        Map<String, Object> context = new HashMap<String, Object>();
        
        Map<String, Object> context2 = new HashMap<String, Object>();
        context2.put("TT", "^*^"); 
        
        String result = ReplaceUtils.replaceMap(replaceList, context,context2, template);
        String expectedResult = "http://bai^*^yy,com${DD}/have/^*^/dd${page,1,10+,1}";
        System.out.println(result);
        Assert.assertEquals(expectedResult, result);
    }

}
