/**
 * This document and its contents are protected by copyright 2005 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2015
 */

package com.datatrees.crawler.core.processor.extractor;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

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

/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月15日 上午12:46:27
 */
public class CMBExtractorTest extends BaseConfigTest {

    String[] billkey          = {"NameOnCard", "BillMonth", "BillStartDate", "CardNums", "BillDate", "PaymentDueDate", "CreditLimit", "FCCreditLimit", "NewBalance", "FCNewBalance", "MinPayment", "FCMinPayment", "LastBalance", "FCLastBalance", "LastPayment", "FCLastPayment", "NewCharges", "FCNewCharges", "Adjustment", "FCAdjustment", "Interest", "FCInterest", "Integral", "IntegralAdd", "IntegralUsed", "ShoppingSheet", "Installment"};
    String[] ShoppingSheetKey = {"CardNo", "TransDate", "PostDate", "Description", "Currencytype", "AmountMoney"};
    String[] InstallmentKey   = {"InstallmentInfo", "ImTotalTerm", "RecordedTerm", "ImTotalBalance", "ImTotalFees", "UnRecordedBalance", "ImNewBalance", "InstallmentDate", "ImNewFees", "InstallmentType"};

    private FileWapper fileInit(String path) {
        FileWapper file = new FileWapper();
        file.setCharSet("UTF-8");
        file.setMimeType("text/html");
        file.setFile(new File(path));
        return file;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    List<Map> cmbJianbanResourceInit() {
        List list = new ArrayList();
        Map<String, Object> mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单——美食代金券5折起！");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/jianban201507.html"));
        // list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/jianban201409.html"));
        // list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单――9积分兑“刀塔传奇”大礼包！");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/jianban201408.html"));
        // list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单-10元风 暴，千万件商品等您来抢！");// 年份拼接
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/jianban201401.html"));
        // list.add(mailMap);

        mailMap = new HashMap<String, Object>();
        mailMap.put("subject", "招商银行信用卡电子账单——美食代金券5折起！");
        mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/jianban2014.8.html"));

        list.add(mailMap);
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    List<Map> cmbXiangbanResourceInit() {
        List list = new ArrayList();
        Map<String, Object> mailMap = new HashMap<String, Object>();
        //        mailMap.put("subject", "招商银行信用卡电子账单");
        //        mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/xiangban201505.html"));
        //        list.add(mailMap);
        mailMap = new HashMap<String, Object>();
        mailMap.put("subject", "招商银行信用卡电子账单");
        mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/xiangban201402.html"));
        list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/xiangban201401.html"));
        // list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/xiangban201412.html"));
        // list.add(mailMap);
        // mailMap = new HashMap<String, Object>();
        // mailMap.put("subject", "招商银行信用卡电子账单");
        // mailMap.put("pageContent", this.getPageContent("src/test/resources/cmb/xiangban201501.html"));
        // list.add(mailMap);
        return list;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    List<Map> cmbBaijinResourceInit() {
        List list = new ArrayList();
        Map<String, Object> mailMap = new HashMap<String, Object>();
        mailMap.put("subject", "招商银行白金信用卡电子账单");
        mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/baijin201401.html"));
        list.add(mailMap);
        return list;
    }

    private void nullCheck(Map map, String[] billkey) {
        for (String key : billkey) {
            if (map.get(key) == null) {
                System.out.println(key + " null.");
            }
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testCMBExtractor() {
        String conf = "cmb/CMBExtratorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "CMB");
            context.setPluginManager(new SimplePluginManager());
            List<Map> resourceList = this.cmbBaijinResourceInit();
            for (Map mailMap : resourceList) {
                ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);
                request.setInput(mailMap);
                Response response = Extractor.extract(request);
                Map<String, List<Map>> map = ResponseUtil.getResponsePageExtractResultMap(response);
                for (Entry<String, List<Map>> set : map.entrySet()) {
                    for (Map obj : set.getValue()) {
                        System.out.println(GsonUtils.toJson(obj));
                        for (String key : billkey) {
                            if (((Map) obj).get(key) == null) {
                                System.out.println(key + " null.");
                            } else if (key.equals("ShoppingSheet")) {
                                Collection<Map> list = (Collection<Map>) ((Map) obj).get(key);
                                for (Map map1 : list) {
                                    this.nullCheck(map1, ShoppingSheetKey);
                                }
                            } else if (key.equals("Installment")) {
                                Collection<Map> list = (Collection<Map>) ((Map) obj).get(key);
                                for (Map map1 : list) {
                                    this.nullCheck(map1, InstallmentKey);
                                }
                            }
                        }
                    }

                }
            }
        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void testCITICExtractor() {
        String conf = "CITICExtractorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "CITIC");
            context.setPluginManager(new SimplePluginManager());
            Map<String, Object> mailMap = new HashMap<String, Object>();
            mailMap.put("pageContent", this.fileInit("src/test/resources/citic4.html"));

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
    public void testExtractor() {
        String conf = "CMBExtratorConfig.xml";
        try {
            ExtractorProcessorContext context = getExtractorProcessorContext(conf, "CMB");
            context.setPluginManager(new SimplePluginManager());

            Map<String, Object> mailMap = new HashMap<String, Object>();

            mailMap.put("subject", "招商银行白金信用卡电子账单");
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/白金201507.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/白金201401.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/白金201501.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/白金201502.html"));

            mailMap.put("subject", "招商银行运通百夫长黑金卡电子账单");
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/黑金201507.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/黑金20150702.html"));

            ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);

            request.setInput(mailMap);
            Response response = Extractor.extract(request);

            mailMap.put("subject", "招商银行美国运通卡电子账单");
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/美国运通卡201401.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/美国运通卡201406.html"));
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/美国运通卡201507.html"));

            mailMap.put("subject", "招商银行零售贷款电子对账单");
            mailMap.put("pageContent", this.fileInit("src/test/resources/cmb/零售贷款201505.html"));
            ExtractorRepuest request3 = ExtractorRepuest.build().setProcessorContext(context);
            request3.setInput(mailMap);
            Response response3 = Extractor.extract(request3);

            List<Object> objs = ResponseUtil.getResponseObjectList(response);
            List<Object> objs3 = ResponseUtil.getResponseObjectList(response3);

            for (Object obj : objs) {
                System.out.println("  obj " + obj);
            }
            for (Object obj : objs3) {
                System.out.println("  obj " + obj);
            }
        } catch (ParseException e) {
            Assert.fail("not well format config!");
        }
    }
}
