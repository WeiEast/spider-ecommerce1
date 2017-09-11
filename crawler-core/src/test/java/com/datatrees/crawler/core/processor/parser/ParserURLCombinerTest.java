package com.datatrees.crawler.core.processor.parser;

import org.junit.Test;

/**
 * @author Jerry
 * @since 00:07 22/05/2017
 */
public class ParserURLCombinerTest {

    @Test
    public void decodeParserUrl() throws Exception {
        String result = "layInfo?__rt=1&__ro=&id=168269374&sid=287058&type=vv&catid=97@PARSER@@PARSER@";
        String[] splits = ParserURLCombiner.decodeParserUrl(result);
        System.out.println(splits.length);
        for (String string : splits) {
            System.out.println(string);
        }

    }

}