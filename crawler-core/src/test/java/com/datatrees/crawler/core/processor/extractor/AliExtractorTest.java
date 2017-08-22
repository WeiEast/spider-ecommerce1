package com.datatrees.crawler.core.processor.extractor;

import com.datatrees.crawler.core.processor.BaseConfigTest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.util.xml.exception.ParseException;

public class AliExtractorTest extends BaseConfigTest {
    private FileWapper fileInit(String path) {
        FileWapper file = new FileWapper();
        file.setMimeType("text/html");
        file.setFile(new File(path));
        return file;
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testali() {
        String conf = "ali/alipayExtractTest.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "ali");
            context.setPluginManager(new SimplePluginManager());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("pageContent", this.fileInit("/Users/wangcheng/Documents/newworkspace/rawdata1/rawdata/rawdata-submitter/fileoutput/1000000146/1000000146-18-e9981bd9a63f45269942dc62daa6e650/pageContent.html"));
            mailMap.put("url", "https://consumeprod.alipay.com/record/download.htm");
            context.getContext().put("url", "https://consumeprod.alipay.com/record/download.htm");
            ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);
            request.setInput(mailMap);
            Response response = Extractor.extract(request);
            List<Object> objs = ResponseUtil.getResponseObjectList(response);
            if (objs != null) {
                for (Object obj : objs) {
                    System.out.println("  obj " + obj);
                }
            }
            Object obj = ResponseUtil.getResponsePageExtractResultMap(response);
            System.out.println(GsonUtils.toJson(obj));
        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

}
