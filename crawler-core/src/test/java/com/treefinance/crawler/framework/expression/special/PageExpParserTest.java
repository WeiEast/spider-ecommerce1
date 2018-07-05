package com.treefinance.crawler.framework.expression.special;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @since 14:48 2018/5/31
 */
public class PageExpParserTest {

    @Test
    public void eval() {
        String text = PageExpParser.eval("curCuror=#{page, 101 , 500 / 100 , 100 +}", 3);
        Assert.assertEquals("curCuror=301", text);
    }
}