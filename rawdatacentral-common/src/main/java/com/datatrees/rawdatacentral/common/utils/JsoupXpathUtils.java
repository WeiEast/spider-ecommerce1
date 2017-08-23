package com.datatrees.rawdatacentral.common.utils;

import java.util.List;

import cn.wanghaomiao.xpath.model.JXDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupXpathUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsoupXpathUtils.class);

    public static String selectFirstString(String document, String xpath) {
        CheckUtils.checkNotBlank(document, "empty doc");
        CheckUtils.checkNotBlank(xpath, "empty xpath");
        JXDocument jxDocument = new JXDocument(document);
        String result = "";
        try {
            List<Object> list = jxDocument.sel(xpath);
            if (!list.isEmpty()) {
                logger.warn("not found content for xpath={}", xpath);
                result = list.get(0).toString();
            }
            logger.info("selectFirstString success xpath={},content={}", xpath, result.length() > 200 ? result.substring(0, 200) : result);
        } catch (Exception e) {
            logger.error("selectFirstString error, xpath={},content={}", xpath, result.length() > 200 ? result.substring(0, 200) + "....." : result, e);
        }
        return result;
    }

    public static void main(String[] args) {
    }
}
