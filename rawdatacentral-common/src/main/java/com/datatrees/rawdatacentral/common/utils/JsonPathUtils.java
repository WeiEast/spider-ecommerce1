package com.datatrees.rawdatacentral.common.utils;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.ReadContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonPathUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonPathUtils.class);

    public static <T> T select(ReadContext ctx, String select) {
        if (StringUtils.isBlank(select)) {
            return null;
        }
        try {
            return ctx.read(select);
        } catch (PathNotFoundException e) {
            logger.error("result not found for select={}", select);
            return null;
        }
    }

}
