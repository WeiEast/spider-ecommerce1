/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 *
 * Copyright (c) datatrees.com Inc. 2015
 */
package com.datatrees.crawler.example.extractor;

import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.ExtractorProcessorContext;
import com.datatrees.crawler.core.processor.bean.ExtractorRepuest;
import com.datatrees.crawler.core.processor.bean.FileWapper;
import com.datatrees.crawler.core.processor.common.ResponseUtil;
import com.datatrees.crawler.core.processor.extractor.Extractor;
import com.datatrees.crawler.core.processor.plugin.SimplePluginManager;
import com.datatrees.crawler.example.BaseTest;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年8月18日 下午3:52:59
 */
public abstract class ExtractorTest extends BaseTest {

  private static final String[] BILL_FIELDS = {"NameOnCard", "BillMonth", "BillStartDate",
      "CardNums", "BillDate", "PaymentDueDate", "CreditLimit", "FCCreditLimit", "NewBalance",
      "FCNewBalance", "MinPayment", "FCMinPayment", "LastBalance", "FCLastBalance", "LastPayment",
      "FCLastPayment", "NewCharges", "FCNewCharges", "Adjustment", "FCAdjustment", "Interest",
      "FCInterest", "Integral", "IntegralAdd", "IntegralUsed", "ShoppingSheet", "Installment"};
  private static final String[] SHOPPING_SHEET_FIELDS = {"CardNo", "TransDate", "PostDate",
      "Description", "CurrencyType", "AmountMoney"};
  private static final String[] INSTALLMENT_FIELDS = {"InstallmentInfo", "ImTotalTerm",
      "RecordedTerm", "ImTotalBalance", "UnRecordedBalance", "ImNewBalance", "InstallmentDate",
      "ImNewFees"};
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  private ExtractorProcessorContext context;

  @Before
  public void setUp() throws Exception {
    ExtractorProcessorContext context = createExtractorProcessorContext(getWebsite(),
        getConfigFile());
    context.setPluginManager(new SimplePluginManager());

    this.context = context;
  }

  protected abstract String getWebsite();

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
    Assert.assertNotNull(map);

    map.forEach((key, maps) -> {
      for (Map obj : maps) {
        if (key.equals("subExtrat")) {
          logger.info("subExtract: {}", obj);
        } else {
          logger.info("result: {}", GsonUtils.toJson(obj));
        }

        showBillInfo(obj);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private void showBillInfo(Map obj) {
    for (String field : BILL_FIELDS) {
      Object value = obj.get(field);
      if (value == null) {
        logger.info("Field[{}] is null", field);
      } else if (field.equals("ShoppingSheet")) {
        showShoppingSheet((Collection<Map>) value);
      } else if (field.equals("Installment")) {
        showInstallment((Collection<Map>) value);
      }
    }
  }

  private void showInstallment(Collection<Map> value) {
    logger.info("Installment size: {}", value.size());

    for (Map map : value) {
      this.nullCheck(map, INSTALLMENT_FIELDS);
    }
  }

  private void showShoppingSheet(Collection<Map> value) {
    logger.info("ShoppingSheet size: {}", value.size());

    for (Map map : value) {
      this.nullCheck(map, SHOPPING_SHEET_FIELDS);
    }
  }

  private void nullCheck(Map map, String[] fields) {
    for (String field : fields) {
      if (map.get(field) == null) {
        logger.info("Field[{}] is null", field);
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

    addSimpleExtractParameters(map);

    return Collections.singletonList(map);
  }

  protected abstract void addSimpleExtractParameters(
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

}
