package com.datatrees.crawler.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.treefinance.crawler.framework.util.XmlDocumentHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Feb 7, 2014 10:16:52 AM
 */
@Deprecated
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
        document = XmlDocumentHelper.createDocument(xml);
        root = document.getRootElement();
    }

    public Document getDocument() {
        return document;
    }

    public Element getRoot() {
        return root;
    }

    public Element getElementByXPath(String path) {
        return (Element) XmlDocumentHelper.evaluateFirst(root, path);
    }

    public List<Object> getElementsByXPath(String path) {
        return XmlDocumentHelper.evaluate(root, path);
    }

    public static Element getElementByXPath(Element element, String path) {
        return XmlDocumentHelper.getElementByXPath(element, path);
    }

    public static List<Object> getElementsByXPath(Element element, String path) {
        return XmlDocumentHelper.getElementsByXPath(element, path);
    }

    public static String getElementValue(Object obj) {
        return XmlDocumentHelper.getElementValue(obj);
    }

    public static String getStringValue(Element element, String path) {
        return XmlDocumentHelper.getStringValue(element, path);
    }

    public static Integer getIntValue(Element element, String path) {
        return XmlDocumentHelper.getIntValue(element, path);
    }

}
