package com.datatrees.crawler.core.processor.extractor;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

/**
 * @author Jerry
 * @datetime 2015-07-18 19:29
 */
public class JsonPathFieldExtractorTest extends BaseConfigTest {


    private FileWapper fileInit(String path) {
        FileWapper file = new FileWapper();
        file.setCharSet("UTF-8");
        file.setMimeType("text/html");
        file.setFile(new File(path));
        return file;
    }

    @Test
    public void testExtractor() {
        String conf = "jsonpath-extractorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "CMB");
            context.setPluginManager(new SimplePluginManager());

            Map<String, Object> mailMap = new HashMap<String, Object>();

            mailMap.put(
                    "detailBillPage",
                    this.fileInit("src/test/resources/jsonpath-test.json"));

            ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);

            request.setInput(mailMap);
            Response response = Extractor.extract(request);



            List<Object> objs = ResponseUtil.getResponseObjectList(response);
            for (Object obj : objs) {
                System.out.println("  obj " + obj);
            }
        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }
}
