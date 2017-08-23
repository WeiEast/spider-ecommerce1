package com.datatrees.crawler.core.processor.format.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.datatrees.common.pipeline.Request;
import com.datatrees.common.pipeline.Response;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.format.AbstractFormat;
import com.datatrees.crawler.core.processor.format.container.RMBMapContainer;
import com.datatrees.crawler.core.processor.format.unit.RMBUnit;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMBFormatImpl extends AbstractFormat {

    private static final Logger log             = LoggerFactory.getLogger(RMBFormatImpl.class);
    private final        String paymentNumRegex = "([\\.,\\d]+)";

    @Override
    public Object format(Request req, Response response, String orginal, String pattern) {
        if (StringUtils.isEmpty(orginal)) {
            log.warn("orginal empty!");
            return null;
        }
        Number result;
        String numPart = PatternUtils.group(orginal, paymentNumRegex, 1);
        Map<String, RMBUnit> rmbMapper = parseNewFormate(pattern);
        RMBUnit unit = findRMBUnit(rmbMapper, orginal);

        if (unit == null) {
            RMBMapContainer container = RMBMapContainer.get(getConf());
            rmbMapper = container.getRMBMapper();
            unit = findRMBUnit(rmbMapper, orginal);
        }
        if (unit == null) {
            if (log.isDebugEnabled()) {
                log.debug("can't find corrent rmb conf! " + orginal + " & " + numPart + " & " + pattern);
            }
            unit = RMBUnit.YUAN;
        }
        // save to .00
        double temp = 0.00;

        try {
            temp = Double.valueOf(numPart);
            result = temp * unit.getConversion();
        } catch (Exception e) {
            log.error("parse error " + temp, e);
            result = null;
        }
        return result;
    }

    private RMBUnit findRMBUnit(Map<String, RMBUnit> paymentMapper, String orginal) {
        RMBUnit result = null;
        if (MapUtils.isNotEmpty(paymentMapper) && StringUtils.isNotEmpty(orginal)) {
            Iterator<String> paymentPatterns = paymentMapper.keySet().iterator();
            while (paymentPatterns.hasNext()) {
                String paymentPattern = paymentPatterns.next();
                if (PatternUtils.match(paymentPattern.toLowerCase(), orginal.toLowerCase())) {
                    result = paymentMapper.get(paymentPattern);
                    log.debug("find RMB " + orginal + " unit :" + result);
                    break;
                }
            }
        }
        return result;
    }

    private Map<String, RMBUnit> parseNewFormate(String pattern) {
        Map<String, RMBUnit> map = null;
        if (StringUtils.isNotEmpty(pattern)) {
            String[] spilts = pattern.split("###");
            if (spilts.length != 3) {
                log.warn("payment pattern parse error! " + pattern);
                return null;
            }
            map = new HashMap<String, RMBUnit>();
            for (int i = 0; i < 3; i++) {
                switch (i) {
                    case 0:
                        map.put(spilts[0], RMBUnit.YUAN);
                        break;
                    case 1:
                        map.put(spilts[1], RMBUnit.JIAO);
                        break;
                    case 2:
                        map.put(spilts[2], RMBUnit.FEN);
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
