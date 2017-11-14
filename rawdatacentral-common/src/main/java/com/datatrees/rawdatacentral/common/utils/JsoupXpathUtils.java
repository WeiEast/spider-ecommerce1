package com.datatrees.rawdatacentral.common.utils;

import java.util.*;

import cn.wanghaomiao.xpath.model.JXDocument;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsoupXpathUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsoupXpathUtils.class);

    public static String selectFirst(String document, String xpath) {
        CheckUtils.checkNotBlank(document, "empty doc");
        CheckUtils.checkNotBlank(xpath, "empty xpath");
        JXDocument jxDocument = new JXDocument(document);
        String result = "";
        try {
            List<Object> list = jxDocument.sel(xpath);
            if (list.isEmpty()) {
                logger.warn("not found content for xpath={}", xpath);
                return null;
            }
            logger.info("selectFirst success xpath={},content={}", xpath, result.length() > 200 ? result.substring(0, 200) : result);
            return list.get(0).toString();
        } catch (Exception e) {
            logger.error("selectFirst error, xpath={},content={}", xpath, result.length() > 200 ? result.substring(0, 200) + "....." : result, e);
            return null;
        }
    }

    public static List<Map<String, String>> selectAttributes(String document, String xpath) {
        CheckUtils.checkNotBlank(document, "empty doc");
        CheckUtils.checkNotBlank(xpath, "empty xpath");
        JXDocument jxDocument = new JXDocument(document);
        List<Map<String, String>> list = new ArrayList<>();
        try {
            List<Object> elements = jxDocument.sel(xpath);
            if (null != elements && !elements.isEmpty()) {
                for (Object object : elements) {
                    Element element = Element.class.cast(object);
                    Iterator<Attribute> iterator = element.attributes().iterator();
                    Map<String, String> map = null;
                    while (iterator.hasNext()) {
                        if (null == map) {
                            map = new HashMap<>();
                        }
                        Attribute attribute = iterator.next();
                        map.put(attribute.getKey(), attribute.getValue());
                    }
                    if (null != map && !map.isEmpty()) {
                        list.add(map);
                    }
                }
            }
            logger.info("selectAttributes success xpath={},size={}", xpath, list.size());
        } catch (Exception e) {
            logger.error("selectAttributes error, xpath={},document={}", xpath, document, e);
        }
        return list;
    }

    public static List<Element> selectElements(String document, String xpath) {
        CheckUtils.checkNotBlank(document, "empty doc");
        CheckUtils.checkNotBlank(xpath, "empty xpath");
        JXDocument jxDocument = new JXDocument(document);
        try {
            List<Element> list = new ArrayList<>();
            List<Object> sels = jxDocument.sel(xpath);
            if (CollectionUtils.isNotEmpty(sels)) {
                for (Object o : sels) {
                    list.add((Element) o);
                }
            }
            logger.info("selectElementList size={},xpath={}", list.size(), xpath);
            return list;
        } catch (Throwable e) {
            logger.error("selectElementList error document={},xpath={}", document, xpath, e);
            return null;
        }
    }

}
