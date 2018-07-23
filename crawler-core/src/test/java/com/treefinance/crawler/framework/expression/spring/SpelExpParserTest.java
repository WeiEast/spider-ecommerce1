package com.treefinance.crawler.framework.expression.spring;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @since 16:10 2018/6/8
 */
public class SpelExpParserTest {

    @Test
    public void parse() {
        boolean result = SpelExpParser.parse("40<40&&\"4300.00万元\"!=\"-\"", Boolean.TYPE);

        Assert.assertFalse(result);
    }
}