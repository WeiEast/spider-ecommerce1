/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.core.processor.extractor;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.BaseConfigTest;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月18日 下午3:52:59
 */
public abstract class BaseExtractorTest extends BaseConfigTest {

  private final String[] BILL_FIELDS = {"NameOnCard", "BillMonth", "BillStartDate", "CardNums",
      "BillDate", "PaymentDueDate", "CreditLimit", "FCCreditLimit", "NewBalance", "FCNewBalance",
      "MinPayment", "FCMinPayment", "LastBalance", "FCLastBalance", "LastPayment", "FCLastPayment",
      "NewCharges", "FCNewCharges", "Adjustment", "FCAdjustment", "Interest", "FCInterest",
      "Integral", "IntegralAdd", "IntegralUsed", "ShoppingSheet", "Installment"};

  private final String[] SHOPPING_SHEET_FIELDS = {"CardNo", "TransDate", "PostDate", "Description",
      "CurrencyType", "AmountMoney"};

  private final String[] INSTALLMENT_FIELDS = {"InstallmentInfo", "ImTotalTerm", "RecordedTerm",
      "ImTotalBalance", "UnRecordedBalance", "ImNewBalance", "InstallmentDate", "ImNewFees"};

  private ExtractorProcessorContext context;

  @Before
  public void setUp() throws Exception {
    ExtractorProcessorContext context = getExtractorProcessorContext(getConfigFile(), "ccb");
    context.setPluginManager(new SimplePluginManager());

    this.context = context;
  }

  protected abstract String getConfigFile();

  @Test
  public void test() throws Exception {
    List<Map<String, Object>> resourceList = this.getExtractResources();

    Objects.requireNonNull(resourceList);

    for (Map<String, Object> mailMap : resourceList) {

      Map<String, List<Map>> map = extract(mailMap);

      handleResult(map);
    }
  }

  private void handleResult(Map<String, List<Map>> map) {
    for (Entry<String, List<Map>> set : map.entrySet()) {
      for (Map obj : set.getValue()) {
        if (set.getKey().equals("subExtrat")) {
          System.out.println(obj);
        } else {
          System.out.println(GsonUtils.toJson(obj));
        }
        for (String key : BILL_FIELDS) {
          if ((obj).get(key) == null) {
            System.out.println(key + " null.");
          } else if (key.equals("ShoppingSheet")) {
            Collection<Map> list = (Collection<Map>) obj.get(key);
            for (Map map1 : list) {
              this.nullCheck(map1, SHOPPING_SHEET_FIELDS);
            }
            System.out.println("ShoppingSheet size:" + list.size());
          } else if (key.equals("Installment")) {
            Collection<Map> list = (Collection<Map>) obj.get(key);
            for (Map map1 : list) {
              this.nullCheck(map1, INSTALLMENT_FIELDS);
            }
            System.out.println("Installment size:" + list.size());
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Map<String, List<Map>> extract(Map mailMap) {
    ExtractorRepuest request = ExtractorRepuest.build().setProcessorContext(context);
    request.setInput(mailMap);
    Response response = Extractor.extract(request);
    return ResponseUtil.getResponsePageExtractResultMap(response);
  }

  protected List<Map<String, Object>> getExtractResources() throws Exception {
    Map<String, Object> map = new HashMap<>();

    addSimpleExtractSource(map);

    return Collections.singletonList(map);
  }

  protected abstract void addSimpleExtractSource(
      Map<String, Object> map) throws Exception;

  protected FileWapper getPageContent(String path) {
    return getPageContent(new File(path));
  }

  protected FileWapper getPageContent(File page) {
    FileWapper file = new FileWapper();
    file.setMimeType("text/html");
    file.setFile(page);
    return file;
  }

  private void nullCheck(Map map, String[] billkey) {
    for (String key : billkey) {
      if (map.get(key) == null) {
        System.out.println(key + " null.");
      }
    }
  }

}
