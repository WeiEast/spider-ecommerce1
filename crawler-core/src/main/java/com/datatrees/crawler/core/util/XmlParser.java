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
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
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
        StringReader reader = new StringReader(xml);
        SAXBuilder saxBuilder = new SAXBuilder();
        document = saxBuilder.build(reader);
        root = document.getRootElement();
        reader.close();
    }

    public static String getElementValue(Object obj) {
        if (obj == null) {
            return StringUtils.EMPTY;
        }
        if (obj instanceof Attribute) {
            Attribute attribute = (Attribute) obj;
            return attribute.getValue();
        } else if (obj instanceof Text) {
            return ((Text) obj).getText();
        } else {
            return obj.toString();
        }
    }

    public Document getDocument() {
        return document;
    }

    public Element getRoot() {
        return root;
    }

    private XPathExpression<Object> complieXpath(String path) {
        return XPathFactory.instance().compile(path);
    }

    public Element getElementByXPath(String path) throws JDOMException {
        XPathExpression<Object> xpath = complieXpath(path);
        return (Element) xpath.evaluateFirst(root);
    }

    public Element getElementByXPath(Element context, String path) throws JDOMException {
        XPathExpression<Object> xpath = complieXpath(path);
        return (Element) xpath.evaluateFirst(context);
    }

    public List<Object> getElementsByXPath(String path) throws JDOMException {
        XPathExpression<Object> xpath = complieXpath(path);
        return xpath.evaluate(root);
    }

    public List<Object> getElementsByXPath(Element context, String path) throws JDOMException {
        XPathExpression<Object> xpath = complieXpath(path);
        return xpath.evaluate(context);
    }

    public String getStringValue(Element context, String path) {
        if (context == null) {
            return null;
        }
        XPathExpression<Object> xpath = complieXpath(path);
        Object obj = xpath.evaluateFirst(context);
        return getElementValue(obj);
    }

    public Integer getIntValue(Element context, String path) {
        String value = this.getStringValue(context, path);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Integer.valueOf(value);
    }

}
