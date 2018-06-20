package com.treefinance.crawler.framework.expression;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jerry
 * @since 17:30 2018/6/20
 */
public class StandardExpressionTest {

    @Test
    public void evalSpecial() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Jerry");

        String value = StandardExpression.evalSpecial("${name}", map);
        Assert.assertEquals("Jerry",value);

        value = StandardExpression.evalSpecial("${name1}", map);
        Assert.assertNull(value);
    }
}