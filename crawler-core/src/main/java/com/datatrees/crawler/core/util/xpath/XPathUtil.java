package com.datatrees.crawler.core.util.xpath;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.datatrees.crawler.core.util.xpath.jsoup.JSoupQuerySyntax;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.JDomSerializer;
import org.htmlcleaner.TagNode;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.support.AbstractXMLOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.util.NamespaceStack;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月6日 上午12:06:37
 */
public class XPathUtil {

    private static final Logger log       = LoggerFactory.getLogger(XPathUtil.class);

    private static final String TEXT_SIGN = "/text(";

    private static final String SEPARATOR = "\"";

    public static List<String> getXpath(String select, String content) {
        List<String> result = new ArrayList<String>();
        if (StringUtils.isEmpty(select) || StringUtils.isEmpty(content)) {
            log.warn("empty xpath or content");
            return result;
        }
        for (String selectString : select.split(SEPARATOR)) {
            result.addAll(routeToSelet(selectString, content));
        }
        return result;
    }

    /**
     * check to use jsoup or xpath
     */
    private static List<String> routeToSelet(String select, String content) {
        content = contentPreClean(content);
        boolean extractText = select.contains(TEXT_SIGN);
        String split = "";
        String originalXpath = select;
        if (extractText) {
            select = originalXpath.substring(0, select.indexOf(TEXT_SIGN));
            split = StringUtils.substringBetween(originalXpath, TEXT_SIGN, ")");
        }

        boolean selectSynax = JSoupQuerySyntax.isValid(select);
        List<String> resList = new ArrayList<String>();
        if (selectSynax) {
            if (extractText) {
                Collection<String> resultStrings = (Collection<String>) useJsoupSelect(select, content);
                StringBuilder newContent = new StringBuilder();
                for (String string : resultStrings) {
                    newContent.append(string);
                }
                resList.addAll(useXpathSelect(".", newContent.toString(), extractText, split));
            } else {
                resList.addAll(useJsoupSelect(select, content));
            }
            log.debug("route to jsoup {}", select);

        } else {
            resList.addAll(useXpathSelect(select, content, extractText, split));
            log.debug("route to xpath {}", select);
        }
        if (CollectionUtils.isEmpty(resList)) {
            log.warn("empty select content! - {}", select);
        }
        return resList;
    }

    /**
     * @param select
     * @param content
     * @return
     */
    private static Collection<? extends String> useJsoupSelect(String select, String content) {
        List<String> splits = new LinkedList<String>();
        try {
            Elements eles = Jsoup.parse(content).select(select);
            if (eles != null) {
                int size = eles.size();
                for (int i = 0; i < size; i++) {
                    org.jsoup.nodes.Element element = eles.get(i);
                    splits.add(element.outerHtml());
                }
            }
        } catch (Exception e) {
            log.error("select use jsoup error! select " + select);
        }
        return splits;
    }

    /**
     * fix htmlcleaner's bug ,support clean dom wappered with
     * <tr>
     * or
     * <td>
     */
    private static String contentPreClean(String content) {
        content = content.trim();
        if (content.endsWith("</td>") || content.endsWith("</tr>")) {
            StringBuilder value = new StringBuilder();
            value.append("<table>").append(content).append("</table>");
            content = value.toString();
        }
        return content;
    }

    public static String getText(TagNode tagNode, String split) {
        StringBuilder text = new StringBuilder();
        for (Object item : tagNode.getAllChildren()) {
            if (item instanceof ContentNode) {
                if (StringUtils.isNotBlank(((ContentNode) item).getContent())) {
                    text.append(((ContentNode) item).getContent()).append(split);
                }
            } else if (item instanceof TagNode) {
                text.append(getText((TagNode) item, split));
            }
        }
        return text.toString();
    }

    /**
     * @param select
     * @param content
     * @return
     */
    private static Collection<? extends String> useXpathSelect(String xpath, String content, boolean extractText, String split) {
        List<String> splits = new LinkedList<String>();
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode node = cleaner.clean(content);
            XMLOutputter out = new XMLOutputter();
            out.setFormat(Format.getCompactFormat().setEncoding("utf-8"));
            out.setXMLOutputProcessor(new CustomProcessor());
            JDomSerializer jdomSerializer = new JDomSerializer(cleaner.getProperties(), false);
            Object[] elements = node.evaluateXPath(xpath);
            if (ArrayUtils.isNotEmpty(elements)) {
                for (Object obj : elements) {
                    if (obj instanceof TagNode) {
                        TagNode resultNode = (TagNode) obj;
                        if (extractText) {
                            if (StringUtils.isEmpty(split)) {
                                splits.add(com.datatrees.common.util.StringUtils.replaceBlank(resultNode.getText().toString()));
                            } else {
                                splits.add(com.datatrees.common.util.StringUtils.replaceBlank(getText(resultNode, split)));
                            }
                        } else {
                            Document doc = jdomSerializer.createJDom(resultNode);
                            splits.add(out.outputString(doc.getRootElement()));
                        }
                    } else if (obj instanceof CharSequence) {
                        splits.add(String.valueOf(obj));
                    }
                }
            }
        } catch (Exception e) {
            log.error("select use xpath error! xpath " + xpath);
        }
        return splits;

    }

    static final class CustomProcessor extends AbstractXMLOutputProcessor {

        @Override
        public void process(final Writer out, final Format format, final Element element) throws IOException {

            FormatStack fStack = new FormatStack(format);
            fStack.setEscapeOutput(false);
            // If this is the root element we could pre-initialize the
            // namespace stack with the namespaces
            printElement(out, fStack, new NamespaceStack(), element);
            out.flush();
        }

    }

}
