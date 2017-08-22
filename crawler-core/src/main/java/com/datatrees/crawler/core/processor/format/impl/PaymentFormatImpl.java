package com.datatrees.crawler.core.processor.format.impl;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import com.datatrees.crawler.core.processor.format.container.PaymentMapContainer;
import com.datatrees.crawler.core.processor.format.unit.PaymentUnit;

public class PaymentFormatImpl extends AbstractFormat {

    private static final Logger log = LoggerFactory.getLogger(PaymentFormatImpl.class);
    private final String paymentNumRegex = "([\\.,\\d]+)";
    private static final String NegateString = "~";


    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return null;
        }
        Number result;
        String numPart = PatternUtils.group(orginal, paymentNumRegex, 1);
        // replace ,to
        boolean needNegate = false;
        if (StringUtils.isNotBlank(pattern) && pattern.startsWith(NegateString)) {
            pattern = pattern.replaceFirst(NegateString, "");
            needNegate = true;
        }

        Map<String, PaymentUnit> paymentMapper = parseNewFormate(pattern);
        PaymentUnit unit = findPaymentUnit(paymentMapper, orginal);

        if (unit == null) {
            PaymentMapContainer container = PaymentMapContainer.get(getConf());
            paymentMapper = container.getPaymentMapper();
            unit = findPaymentUnit(paymentMapper, orginal);
        }
        if (unit == null) {
            if (log.isDebugEnabled()) {
                log.debug("can't find corrent payment conf! " + orginal + " & " + numPart + " & " + pattern);
            }
            unit = PaymentUnit.OUT;
        }
        // save to .00
        String temp = new StringBuilder(unit.getValue()).append(numPart).toString();

        try {
            result = DecimalFormat.getInstance().parse(temp);
        } catch (ParseException e) {
            log.error("parse error " + temp + ",Exception:" + e.getMessage());
            result = 0;
        }
        if (needNegate) {
            result = 0 - result.doubleValue();
        }

        return result;
    }

    private PaymentUnit findPaymentUnit(Map<String, PaymentUnit> paymentMapper, String orginal) {
        PaymentUnit result = null;
        if (MapUtils.isNotEmpty(paymentMapper) && StringUtils.isNotEmpty(orginal)) {
            Iterator<String> paymentPatterns = paymentMapper.keySet().iterator();
            while (paymentPatterns.hasNext()) {
                String paymentPattern = paymentPatterns.next();
                if (PatternUtils.match(paymentPattern.toLowerCase(), orginal.toLowerCase())) {
                    result = paymentMapper.get(paymentPattern);
                    log.debug("find payment " + orginal + " unit :" + result.getValue());
                    break;
                }

            }
        }
        return result;
    }

    private Map<String, PaymentUnit> parseNewFormate(String pattern) {
        Map<String, PaymentUnit> map = null;
        if (StringUtils.isNotEmpty(pattern)) {
            String[] spilts = pattern.split("###");
            if (spilts.length != 2) {
                log.warn("payment pattern parse error! " + pattern);
                return null;
            }
            map = new HashMap<String, PaymentUnit>();
            for (int i = 0; i < 2; i++) {
                switch (i) {
                    case 0:
                        map.put(spilts[0], PaymentUnit.IN);
                        break;
                    case 1:
                        map.put(spilts[1], PaymentUnit.OUT);
                        break;
                    default:
                        break;
                }
            }
        }
        return map;
    }

    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof Number) {
            return true;
        } else {
            return false;
        }
    }

}
