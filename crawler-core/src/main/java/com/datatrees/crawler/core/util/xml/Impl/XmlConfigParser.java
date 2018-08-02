package com.datatrees.crawler.core.util.xml.Impl;

import java.lang.reflect.Method;
import java.util.*;

import com.datatrees.common.util.ReflectionUtils;
import com.datatrees.crawler.core.util.XmlParser;
import com.datatrees.crawler.core.util.xml.ConfigParser;
import com.datatrees.crawler.core.util.xml.ParentConfigHandler;
import com.datatrees.crawler.core.util.xml.annotation.Node;
import com.datatrees.crawler.core.util.xml.annotation.Path;
import com.datatrees.crawler.core.util.xml.exception.ParseException;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since Jan 9, 2014 5:48:40 PM
 */
public class XmlConfigParser implements ConfigParser {

    static final         List<Class<?>>        valueTypes       = Arrays
            .asList(new Class<?>[]{String.class, Boolean.class, Integer.class, Float.class, Double.class});

    private static final Logger                logger           = LoggerFactory.getLogger(XmlConfigParser.class);

    final                Map<String, Object>   contentMap       = new HashMap<String, Object>();

    final                Map<Class<?>, Object> typeSetMethodMap = new HashMap<Class<?>, Object>();

    private XmlConfigParser() {}

    public static XmlConfigParser getInstance() {
        return new XmlConfigParser();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.datatrees.crawler.core.util.xml.ConfigParser#parse(java.lang.String,
     * java.lang.Class, com.datatrees.crawler.core.util.xml.ConfigParser)
     */
    @Override
    public <T> T parse(String config, Class<T> type, ParentConfigHandler handler) throws Exception {
        T result = parse(config, type);
        if (handler != null) {
            result = handler.parse(result);
        }
        return result;
    }

    @Override
    public synchronized <T> T parse(String config, Class<T> type) throws ParseException {
        try {
            XmlParser parser = new XmlParser(config);
            return processNodes(parser, parser.getRoot(), type);
        } catch (Exception e) {
            throw new ParseException(e);
        } finally {
            contentMap.clear();
        }
    }

    private <V> V processNodes(XmlParser parser, Element e, Class<V> type) throws Exception {
        e = processPath(parser, e, type);
        if (e == null) {
            return null;
        }

        V parent = ReflectionUtils.newInstance(type);

        List<Method> methods = listOrderedSetMethod(type);
        for (Method method : methods) {
            processNodeMethod(parser, e, parent, method);
        }

        return parent;
    }

    @SuppressWarnings("unchecked")
    private <V> List<Method> listOrderedSetMethod(Class<V> type) {
        List<Method> registeredList = (List<Method>) typeSetMethodMap.get(type);
        if (registeredList == null) {
            List<Method> methods = ReflectionUtils.listSetMethodWithAnnotations(type, Node.class);
            registeredList = new LinkedList<Method>();
            List<Method> normalList = new LinkedList<Method>();
            for (Method method : methods) {
                Node node = method.getAnnotation(Node.class);
                if (node.registered()) {
                    registeredList.add(method);
                } else {
                    normalList.add(method);
                }
            }
            for (Method method : normalList) {
                registeredList.add(method);
            }
            typeSetMethodMap.put(type, registeredList);
        }
        return registeredList;
    }

    private Element processPath(XmlParser parser, Element e, Class<?> type) throws JDOMException {
        if (type.isAnnotationPresent(Path.class)) {
            Path path = type.getAnnotation(Path.class);
            if (StringUtils.isNotBlank(path.value())) {
                e = parser.getElementByXPath(e, path.value());
            }
        }
        return e;
    }

    private void processNodeMethod(XmlParser parser, Element e, Object parent, Method method) throws Exception {
        Node node = method.getAnnotation(Node.class);
        Class<?> setClassType = method.getParameterTypes()[0];
        List<Object> elements = Arrays.asList((Object) e);
        if (StringUtils.isNotBlank(node.value())) {
            elements = parser.getElementsByXPath(e, node.value());
        }

        for (Object element : elements) {
            if (node.types().length == 0) {// default use setClassType
                if (valueTypes.contains(setClassType)) {
                    this.defaultTypeProcess(element, setClassType, parent, method);
                } else {
                    this.customTypeProcess(parser, element, setClassType, parent, method);
                }
            } else {// get from node.types
                if (node.types().length > 0 && valueTypes.contains(node.types()[0])) {// base type
                    this.defaultTypeProcess(element, node.types()[0], parent, method);
                } else {// custom class
                    for (Class<?> type : node.types()) {
                        if (this.customTypeProcess(parser, element, type, parent, method) != null) break;
                    }
                }
            }
        }
    }

    private void defaultTypeProcess(Object element, Class<?> setClassType, Object parent, Method method) {
        Object value = processValue(element, setClassType);
        if (value != null) {
            logger.trace("invoke method : {} for target : {} with value : {}", method.getName(), parent, value);
            ReflectionUtils.invokeMethod(method, parent, value);
        }
    }

    private Object customTypeProcess(XmlParser parser, Object element, Class<?> setClassType, Object parent, Method method) throws Exception {
        Node node = method.getAnnotation(Node.class);
        Object value = null;
        if (node.referenced()) {
            String id = (String) processValue(element, String.class);// get id
            value = contentMap.get(id);
        } else {
            value = processNodes(parser, (Element) element, setClassType);
        }
        this.methodinvoke(value, parent, method, node);
        return value;
    }

    private void methodinvoke(Object value, Object parent, Method method, Node node) throws ParseException {
        if (value != null) {
            logger.trace("invoke method : {} for target : {} with value : {}", method.getName(), parent, value);
            ReflectionUtils.invokeMethod(method, parent, value);
            if (node.registered()) {
                String id = value.toString();
                Object oldBeanDefinition = contentMap.get(id);
                if (oldBeanDefinition != null) {
                    throw new ParseException("exist the same BeanDefinition named" + id);
                } else {
                    contentMap.put(id, value);
                }
            }
        }
    }

    private Object processValue(Object obj, Class<?> type) {
        String value = XmlParser.getElementValue(obj);
        if (value != null && String.class.equals(type) && obj instanceof Attribute) {// Attribute
            // allow " "
            return value;
        } else if (StringUtils.isBlank(value)) {
            return null;
        }

        value = value.trim();

        if (Boolean.class.equals(type)) {
            return Boolean.valueOf(value);
        } else if (Integer.class.equals(type)) {
            return Integer.valueOf(value);
        } else if (Float.class.equals(type)) {
            return Float.valueOf(value);
        } else if (Double.class.equals(type)) {
            return Double.valueOf(value);
        }

        return value;
    }

}
