package com.datatrees.crawler.core.util.xpath.jsoup;

import org.junit.Test;

/**
 * @author Jerry
 * @since 22:44 21/05/2017
 */
public class JSoupQuerySyntaxTest {

    @Test
    public void isValid() throws Exception {
        // System.out.println(isValid("div.abc")); // valid
        System.out.println(JSoupQuerySyntax.isValid("//a")); // not valid
        System.out.println(JSoupQuerySyntax.isValid("li:eq(3)")); // not valid
        // System.out.println(isValid("div > a")); // valid
    }

}