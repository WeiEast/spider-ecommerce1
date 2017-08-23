package com.datatrees.crawler.core.processor.extractor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.junit.Assert;
import org.junit.Test;

public class OperatorExtractorTest extends BaseConfigTest {

    private FileWapper fileInit(String path) {
        FileWapper file = new FileWapper();
        file.setCharSet("utf-8");
        file.setMimeType("text/html");
        file.setFile(new File(path));
        return file;
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testali() {
        String conf = "10010/extractotConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "ali");
            context.setPluginManager(new SimplePluginManager());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("pageContent", this.fileInit("/Users/wangcheng/Downloads/pageContent 15.html"));
            mailMap.put("url", "http://iservice.10010.com/e3/static/query/accountBalance/search\"type=onlyAccount");
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
            System.out.println(obj);
            System.out.println(GsonUtils.toJson(obj));

        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test189Extractor() {
        String conf = "js189/js189ExtractorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "189");
            context.setPluginManager(new SimplePluginManager());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("pageContent", this.fileInit("src/test/resources/page.html"));
            mailMap.put("url", "chargeQuery/chargeQuery_queryCustBill.action");
            mailMap.put("phonenum", "15351656811");
            context.getContext().put("phonenum", "15351656811");
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

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void test10010Extractor() {
        String conf = "zj189/zj189ExtractorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "189");
            context.setPluginManager(new SimplePluginManager());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("pageContent", this.fileInit("src/test/resources/page.html"));
            mailMap.put("url", "bill/getBillDetail.htm");

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
