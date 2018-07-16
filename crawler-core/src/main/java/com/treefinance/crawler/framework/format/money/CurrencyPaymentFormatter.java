package com.treefinance.crawler.framework.format.money;

import javax.annotation.Nonnull;

import com.treefinance.crawler.framework.format.CommonFormatter;
import com.treefinance.crawler.framework.format.FormatConfig;
import org.apache.commons.lang.StringUtils;

/**
 * @author Jerry
 * @since 00:45 2018/6/2
 */
public class CurrencyPaymentFormatter extends CommonFormatter<String> {

    private final CurrencyFormatter currencyFormat = new CurrencyFormatter();
    private final PaymentFormatter  paymentFormat  = new PaymentFormatter();

    @Override
    protected String toFormat(@Nonnull String value, @Nonnull FormatConfig config) throws Exception {
        String pattern = config.getPattern();
        String paymentPattern = null;
        String currencyPattern = null;
        if (StringUtils.isNotEmpty(pattern)) {
            String[] patterns = pattern.split("``");
            paymentPattern = patterns[0];
            if (patterns.length > 1) {
                currencyPattern = patterns[1];
            }
        }
        String currency = currencyFormat.format(value, currencyPattern, config.getRequest(), config.getResponse());
        Number number = paymentFormat.format(value, paymentPattern, config.getRequest(), config.getResponse());
        String result = null;
        if (number != null && currency != null) {
            result = number + " " + currency;
        }
        return result;
    }

}
