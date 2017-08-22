package com.datatrees.crawler.core.processor.format.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.crawler.core.processor.format.AbstractFormat;

public class CurrencyPaymentFormatImpl extends AbstractFormat {

    private static final Logger log = LoggerFactory.getLogger(CurrencyPaymentFormatImpl.class);

    CurrencyFormatImpl currencyFormat = new CurrencyFormatImpl();
    PaymentFormatImpl paymentFormat = new PaymentFormatImpl();

    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return null;
        }
        String paymentConfig = null;
        String currencyConfig = null;
        if (StringUtils.isNotBlank(pattern)) {
            String[] confifStrings = pattern.split("``");
            try {
                paymentConfig = confifStrings[0];
                currencyConfig = confifStrings.length > 1 ? confifStrings[1] : null;
            } catch (Exception e) {
                log.warn("error input " + pattern + ", " + e.getMessage());
            }
        }
        String currency = (String) currencyFormat.format(req, response, orginal, currencyConfig);
        Number number = (Number) paymentFormat.format(req, response, orginal, paymentConfig);
        String result = null;
        if (number != null && currency != null) {
            result = number + " " + currency;
        }
        return result;
    }

    @Override
    public boolean isResultType(Object result) {
        if (result != null && result instanceof String) {
            return true;
        } else {
            return false;
        }
    }

}
