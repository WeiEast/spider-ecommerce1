package com.datatrees.crawler.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:52 AM
 */
public class XmlParser {

    private final Document document;

    private final Element  root;

    public XmlParser(InputStream input) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        document = saxBuilder.build(input);
        root = document.getRootElement();
    }

    public XmlParser(Reader reader) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder();
        document = saxBuilder.build(reader);
        root = document.getRootElement();
    }

    public XmlParser(String xml) throws JDOMException, IOException {
        document = createDocument(xml);
        root = document.getRootElement();
    }

    public Document getDocument() {
        return document;
    }

    public Element getRoot() {
        return root;
    }

    public static Document createDocument(String xml) throws JDOMException, IOException {
        try (StringReader reader = new StringReader(xml)) {
            SAXBuilder saxBuilder = new SAXBuilder();
            return saxBuilder.build(reader);
        }
    }

    public Element getElementByXPath(String path) {
        return (Element) evaluateFirst(root, path);
    }

    public List<Object> getElementsByXPath(String path) {
        return evaluate(root, path);
    }

    public static XPathExpression<Object> compileXpath(String path) {
        return XPathFactory.instance().compile(path);
    }

    public static Object evaluateFirst(Element element, String path) {
        XPathExpression<Object> xpath = compileXpath(path);
        return xpath.evaluateFirst(element);
    }

    public static List<Object> evaluate(Element element, String path) {
        XPathExpression<Object> xpath = compileXpath(path);
        return xpath.evaluate(element);
    }

    public static Element getElementByXPath(Element context, String path) {
        return (Element) evaluateFirst(context, path);
    }

    public static List<Object> getElementsByXPath(Element context, String path) {
        return evaluate(context, path);
    }

    public static String getElementValue(Object obj) {
        if (obj == null) {
            return StringUtils.EMPTY;
        }
        if (obj instanceof Attribute) {
            return ((Attribute) obj).getValue();
        } else if (obj instanceof Text) {
            return ((Text) obj).getText();
        } else {
            return obj.toString();
        }
    }

    public static String getStringValue(Element context, String path) {
        if (context == null) {
            return null;
        }
        Object obj = evaluateFirst(context, path);
        return getElementValue(obj);
    }

    public static Integer getIntValue(Element context, String path) {
        String value = getStringValue(context, path);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

}
