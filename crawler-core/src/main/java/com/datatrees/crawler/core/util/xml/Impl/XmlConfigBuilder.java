package com.datatrees.crawler.core.util.xml.Impl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.datatrees.common.util.ReflectionUtils;
import com.datatrees.crawler.core.util.xml.ConfigBuilder;
import com.datatrees.crawler.core.util.xml.annotation.Attr;
import com.datatrees.crawler.core.util.xml.annotation.ChildTag;
import com.datatrees.crawler.core.util.xml.annotation.Tag;
import com.datatrees.crawler.core.util.xml.definition.AbstractBeanDefinition;
import org.apache.commons.lang.StringUtils;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 5:49:13 PM
 */
public enum XmlConfigBuilder implements ConfigBuilder {
    INSTANCE;
    static final String splitRegex = "/";
    @SuppressWarnings("unchecked")
    List<Class> boxingTypes = Arrays.asList(new Class[]{String.class, Long.class, Integer.class, Short.class, Double.class, Float.class, Boolean.class});

    public static XmlConfigBuilder getInstance() {
        return INSTANCE;
    }
    
  /*  
    private static final class MyXMLProcessor extends AbstractXMLOutputProcessor {
        @Override
        public void process(final Writer out, final Format format, final Element element) throws IOException {
            FormatStack fStack = new FormatStack(format);
            fStack.setEscapeOutput(false);
            // If this is the root element we could pre-initialize the
            // namespace stack with the namespaces
            printElement(out, fStack, new NamespaceStack(), element);
            out.flush();
        }
    }*/

    @Override
    public String buildConfig(Object obj) {
        Document document = new Document();
        if (obj != null) {
            this.processObjectWithTag(document, obj);
        }
        return outputDocument(document);
    }

    private String outputDocument(Document document) {
        XMLOutputter fmt = new XMLOutputter();
        Format f = Format.getPrettyFormat();
        //  fmt.setXMLOutputProcessor(new MyXMLProcessor());
        f.setExpandEmptyElements(true);
        fmt.setFormat(f);
        return fmt.outputString(document);
    }

    private void processObjectWithTag(Parent parent, Object obj) {
        if (this.isValueObj(obj)) {
            parent.addContent(new CDATA(obj.toString()));
        } else {
            if (obj.getClass().isAnnotationPresent(Tag.class)) {
                Tag objTag = obj.getClass().getAnnotation(Tag.class);
                Element element = this.createElementWithTag(objTag);
                parent.addContent(element);
                parent = element;
            }

            if (parent instanceof Element) {
                this.processObjectWithAttr((Element) parent, obj);
            }
        }
        processObjectForTagMethods(parent, obj);
    }

    private void processObjectForTagMethods(Parent parent, Object obj) {
        @SuppressWarnings("unchecked") List<Method> tagMethods = ReflectionUtils.listGetMethodWithAnnotations(obj.getClass(), Tag.class, ChildTag.class);
        for (Method tagMethod : tagMethods) {
            if (tagMethod.isAnnotationPresent(Tag.class)) {
                this.processTagMethod(parent, obj, tagMethod);
            } else if (tagMethod.isAnnotationPresent(ChildTag.class)) {
                this.processChildTagMethod(parent, obj, tagMethod);
            }
        }
    }

    private void processTagMethod(Parent parent, Object obj, Method tagMethod) {
        Object child = ReflectionUtils.invokeMethod(tagMethod, obj);
        if (child != null) {
            Tag tag = tagMethod.getAnnotation(Tag.class);
            String[] splitList = tag.value().split(splitRegex);
            String lastPath = splitList.length > 0 ? splitList[splitList.length - 1] : "";
            Element lastParentElement = (Element) (splitList.length > 1 ? createElement(splitList[splitList.length - 2]) : parent);
            Element element = this.createElementWithTag(lastPath);
            if (element == null && parent instanceof Element) {
                element = (Element) parent;
            } else if (element != null) {
                lastParentElement.addContent(element);
            }

            if (ReflectionUtils.isColleciton(child)) {
                for (Iterator<?> iterator = ReflectionUtils.iteratorColleciton(child); iterator.hasNext(); ) {
                    Object item = iterator.next();
                    this.processObjectWithTag(element, item);
                }
            } else {
                this.processObjectWithTag(element, child);
            }
            this.emptyTagsCompletion(splitList, lastParentElement, parent);
        }
    }

    private void processChildTagMethod(Parent parent, Object obj, Method tagMethod) {
        Object child = ReflectionUtils.invokeMethod(tagMethod, obj);
        if (child != null && ReflectionUtils.isColleciton(child)) {
            ChildTag tag = tagMethod.getAnnotation(ChildTag.class);
            String[] splitList = tag.value().split(splitRegex);
            String lastPath = splitList.length > 0 ? splitList[splitList.length - 1] : "";
            Element lastParentElement = (Element) (splitList.length > 1 ? createElement(splitList[splitList.length - 2]) : parent);
            if (StringUtils.isNotBlank(lastPath)) {
                for (Iterator<?> iterator = ReflectionUtils.iteratorColleciton(child); iterator.hasNext(); ) {
                    Object item = iterator.next();
                    Element element = createElement(lastPath);
                    if (tag.referenced()) {// ref bean
                        AbstractBeanDefinition newBean = new AbstractBeanDefinition();
                        newBean.setId(item.toString());
                        item = newBean;
                    }
                    this.processObjectWithTag(element, item);
                    lastParentElement.addContent(element);
                }
            }
            this.emptyTagsCompletion(splitList, lastParentElement, parent);
        }
    }

    private void emptyTagsCompletion(String[] tagNmaes, Element lastParentElement, Parent parent) {
        for (int i = tagNmaes.length - 3; i >= 0; i--) {
            Element element = createElement(tagNmaes[i]);
            element.addContent(lastParentElement);
            lastParentElement = element;
        }
        if (!lastParentElement.equals(parent)) parent.addContent(lastParentElement);
    }

    private void processObjectWithAttr(Element parent, Object obj) {
        @SuppressWarnings("unchecked") List<Method> attrMethods = ReflectionUtils.listGetMethodWithAnnotations(obj.getClass(), Attr.class);
        for (Method method : attrMethods) {
            Object attrObj = ReflectionUtils.invokeMethod(method, obj);
            if (attrObj != null) {
                Attr attr = method.getAnnotation(Attr.class);
                parent.setAttribute(attr.value(), attrObj.toString());
            }
        }
    }

    private boolean isValueObj(Object obj) {
        if (obj.getClass().isPrimitive() || obj.getClass().isEnum() || boxingTypes.contains(obj.getClass())) {
            return true;
        } else {
            return false;
        }
    }

    private Element createElementWithTag(Tag tag) {
        if (tag.value() == null || tag.value().equals("")) {
            return null;
        }
        Element element = createElement(tag.value());
        return element;
    }

    private Element createElementWithTag(String name) {
        if (name == null || name.equals("")) {
            return null;
        }
        Element element = createElement(name);
        return element;
    }

    private Element createElement(String name) {
        return new Element(name);
    }
}
