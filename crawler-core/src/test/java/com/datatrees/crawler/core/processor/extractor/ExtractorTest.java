package com.datatrees.crawler.core.processor.extractor;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ProcessorContextFactory;
import com.TestHelper;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jerry
 * @since 11:50 2018/7/12
 */
public class ExtractorTest {
    private ExtractorProcessorContext context;

    @Before
    public void setUp() throws Exception {
        this.context = ProcessorContextFactory.createExtractorProcessorContext("10086_app", "example/operator/10086_app/ExtractorConfig.xml");
    }

    @Test
    public void extract() {
        Map<String, Object> map = new HashMap<>();
        map.put("url","billInfo");
        map.put("pageContent",TestHelper.getFileContent("example/operator/10086_app/PageContent.json"));
        map.put("billPhone", "123");
        map.put("realname", "test");

        ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);
        request.setInput(map);
        Response response = Extractor.extract(request.contextInit());
        System.out.println(response);
    }

    @Test
    public void name() throws ParseException {

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        Date date = formatter.parseLocalDateTime("2018-03-01").toDate();

        System.out.println(date);
    }
}