/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc. The
 * copying and reproduction of this document and/or its content (whether wholly or partly) or any
 * incorporation of the same into any other material in any media or format of any kind is strictly
 * prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.domain.config.SearchConfig;
import com.datatrees.crawler.core.domain.config.parser.IndexMapping;
import com.datatrees.crawler.core.domain.config.parser.Parser;
import com.datatrees.crawler.core.domain.config.parser.ParserPattern;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.bean.CrawlRequest;
import com.datatrees.crawler.core.processor.common.RequestUtil;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.FieldExtractorWarpper;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import junit.framework.Assert;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 24, 2014 10:00:24 AM
 */
public class ParserTest extends BaseConfigTest {

    private static SearchConfig config = null;

    // @BeforeClass
    public static void init() {
        String fileName = "parserTest.xml";
        try {
            config = getSearchConfig(fileName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // @Ignore
    @Test
    public void testParserWithCombineTemplate() {
        Parser parser = getViewParser();

        String fileContent = getContent("id_XNjczMDc3NDk2_ev_1.html");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(false, parser);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String expected = "http://v.youku.com/QVideo/~ajax/getVideoPlayInfo?__rt=1&__ro=&id=168269374&sid=287058&type=vv&catid=97";
            System.out.println(resp.getOutPut());
            Assert.assertEquals(expected, resp.getOutPut());
        }

    }

    // @Ignore
    @Test
    public void testParserWithRequest() {

        Parser parser = getViewParser();
        String fileContent = getContent("id_XNjczMDc3NDk2_ev_1.html");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(true, parser);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            System.out.println(resp.getOutPut());
            Assert.assertEquals(expected, resp.getOutPut());
        }
    }

    @Test
    public void testParserWithRequestWithErrorContext() {

        Parser parser = getViewParserWithErrorTemplate();
        String fileContent = getContent("id_XNjczMDc3NDk2_ev_1.html");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(true, parser);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            System.out.println(resp.getOutPut());
            Assert.assertEquals(expected, resp.getOutPut());
        }

    }

    // @Ignore
    @Test
    public void testParserWithMultiUrls() {

        long start = System.currentTimeMillis();
        Parser parser = getVkParser();
        String fileContent = getContent("vk.html");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(false, parser, true);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            // System.out.println(resp.getOutPut());
            List<String> results = (List<String>) resp.getOutPut();
            System.out.println(results.size());
            for (String rs : results) {
                System.out.println(rs);
            }
            // Assert.assertEquals(expected, resp.getOutPut());
        }
        long end = System.currentTimeMillis();
        System.out.println("cost ...." + (end - start));

    }

    @Test
    public void testParserWithEmptyMapping() {

        long start = System.currentTimeMillis();
        Parser parser = getVkParserWithEmptyMappings();
        String fileContent = getContent("vk.html");
        if (fileContent != null) {
            CrawlRequest request = CrawlRequest.build();
            RequestUtil.setContent(request, fileContent);
            RequestUtil.getContext(request).put("cid", "xxxxxxxxxxxxxxxxxxx");
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(false, parser, true);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            // System.out.println(resp.getOutPut());
            List<String> results = (List<String>) resp.getOutPut();
            System.out.println(results.size());
            for (String rs : results) {
                System.out.println(rs);
            }
            // Assert.assertEquals(expected, resp.getOutPut());
        }
        long end = System.currentTimeMillis();
        System.out.println("cost ...." + (end - start));

    }

    // @Ignore
    @Test
    public void testParserWithMultiUrlPatterns() {
        long start = System.currentTimeMillis();
        Parser parser = getVkParser2();

        String fileContent = getContent("vk.html");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ParserImpl pi = new ParserImpl(false, parser, true);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            // System.out.println(resp.getOutPut());
            List<String> results = (List<String>) resp.getOutPut();
            System.out.println(results.size());
            for (String rs : results) {
                System.out.println(rs);
            }
            // Assert.assertEquals(expected, resp.getOutPut());
            long end = System.currentTimeMillis();
            System.out.println("cost ...." + (end - start));
        }

    }

    @Test
    public void testParserWithFieldContext() {
        Map<String, FieldExtractorWarpper> context = new HashMap<String, FieldExtractorWarpper>();
        context.put("ttt", new FieldExtractorWarpper(Arrays.asList("TEST")));
        Parser parser = getViewParser();
        String fileContent = getContent("id_XNjczMDc3NDk2_ev_1.html");
        parser.setUrlTemplate(parser.getUrlTemplate() + "ttt=${ttt}&ddd=${ddd}");
        if (fileContent != null) {
            Request request = new Request(fileContent);
            Response resp = new Response();
            ResponseUtil.setResponseFieldResult(resp, context);
            ParserImpl pi = new ParserImpl(false, parser, true);
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            // System.out.println(resp.getOutPut());
            List<String> results = (List<String>) resp.getOutPut();
            System.out.println(results.size());
            for (String rs : results) {
                System.out.println(rs);
            }

            context.put("ttt", new FieldExtractorWarpper(("TEST")));
            try {
                pi.invoke(request, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // String expected = "{\"vv\":341475,\"ss\":\"4QcpSTQzzjQ0A-0KAnsRMdA\"}";
            // System.out.println(resp.getOutPut());
            System.out.println(results.size());
            for (String rs : results) {
                System.out.println(rs);
            }
            // Assert.assertEquals(expected, resp.getOutPut());
        }

    }

    /**
     * <parser id="viewcount-parser"> <patterns> <pattern> <regex><![CDATA[videoId =
     * '(\d+)']]></regex> <mappings> <map group-index="1" placeholder="id" /> </mappings> </pattern>
     * <pattern> <regex><![CDATA[showid="(\d+)"]]></regex> <mappings> <map group-index="1"
     * placeholder="sid" /> </mappings> </pattern> </patterns>
     * <url-template><![CDATA[http://v.youku.
     * com/QVideo/~ajax/getVideoPlayInfo?__rt=1&__ro=&id=${id}&
     * sid=${sid}&type=vv&catid=97]]></url-template>
     * </parser>
     */
    private Parser getViewParser() {
        Parser parser = new Parser();

        ParserPattern pattern = new ParserPattern();
        pattern.setRegex("videoId = '(\\d+)'");
        IndexMapping indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("id");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        pattern = new ParserPattern();
        pattern.setRegex("showid=\"(\\d+)\"");
        indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("sid");
        // pattern.setIndexMapping(indexMapping);

        // parser.setPattern(pattern);

        parser.setUrlTemplate("http://v.youku.com/QVideo/~ajax/getVideoPlayInfo?__rt=1&__ro=&id=${id}&sid=${sid}&type=vv&catid=97");
        return parser;
    }

    private Parser getViewParserWithErrorTemplate() {
        Parser parser = new Parser();

        ParserPattern pattern = new ParserPattern();
        pattern.setRegex("videoId = '(\\d+)'");
        IndexMapping indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("id");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        pattern = new ParserPattern();
        pattern.setRegex("showid=\"(\\d+)\"");
        indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("sid");
        // pattern.setIndexMapping(indexMapping);

        // parser.setPattern(pattern);

        parser.setUrlTemplate("layInfo?__rt=1&__ro=&id=${id}&sid=${sid}&type=vv&catid=97");
        return parser;
    }

    private Parser getVkParserWithEmptyMappings() {
        Parser parser = new Parser();

        // ParserPattern pattern = new ParserPattern();
        // pattern.setRegex("(-\\d+), (\\d+), 'http:");
        // IndexMapping indexMapping = new IndexMapping();
        // indexMapping.setGroupIndex(1);
        // indexMapping.setPlaceholder("cid");
        // pattern.setIndexMapping(indexMapping);
        //
        // indexMapping = new IndexMapping();
        // indexMapping.setGroupIndex(2);
        // indexMapping.setPlaceholder("vid");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        parser.setUrlTemplate("http://vk.com/video${cid}_${vid}");
        return parser;
    }

    /**
     * <parser id="hostUrlParser" type="url"> <patterns> <pattern> <regex><![CDATA[(-\d+), (\d+),
     * 'http:]]></regex> <mappings> <map group-index="1" placeholder="1" /> <map group-index="2"
     * placeholder="2" /> </mappings> </pattern> </patterns>
     * <url-template><![CDATA[http://vk.com/video${1}_${2}]]></url-template> </parser>
     */
    private Parser getVkParser() {
        Parser parser = new Parser();

        ParserPattern pattern = new ParserPattern();
        pattern.setRegex("(-\\d+), (\\d+), 'http:");
        IndexMapping indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("cid");
        // pattern.setIndexMapping(indexMapping);

        indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(2);
        indexMapping.setPlaceholder("vid");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        parser.setUrlTemplate("http://vk.com/video${cid}_${vid}");
        return parser;
    }

    private Parser getVkParser2() {
        Parser parser = new Parser();

        ParserPattern pattern = new ParserPattern();
        pattern.setRegex("(-\\d+), (\\d+), 'http:");
        IndexMapping indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(1);
        indexMapping.setPlaceholder("cid");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        pattern = new ParserPattern();
        pattern.setRegex("(-\\d+), (\\d+), 'http:");
        indexMapping = new IndexMapping();
        indexMapping.setGroupIndex(2);
        indexMapping.setPlaceholder("vid");
        // pattern.setIndexMapping(indexMapping);
        // parser.setPattern(pattern);

        parser.setUrlTemplate("http://vk.com/video${cid}_${vid}");
        parser.setLinkUrlTemplate("http://vk.com/test/_${cid}");
        parser.setHeaders("{aid===${vid}}");
        return parser;
    }
}
