package com.datatrees.rawdatacentral.collector.chain.urlHandler;

import java.util.List;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.search.SearchTemplateCombine;
import com.datatrees.rawdatacentral.collector.chain.Context;
import com.datatrees.rawdatacentral.collector.chain.Filter;
import com.datatrees.rawdatacentral.collector.chain.FilterChain;
import com.datatrees.rawdatacentral.collector.chain.common.ContextUtil;
import com.datatrees.rawdatacentral.collector.search.SearchProcessor;
import com.datatrees.rawdatacentral.collector.search.URLHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午11:41:34
 */
public class AddParserTemplateUrlFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AddParserTemplateUrlFilter.class);

    @Override
    public void doFilter(Context context, FilterChain filterChain) {
        try {
            LinkNode fetchLinkNode = ContextUtil.getFetchLinkNode(context);
            SearchProcessor searchProcessor = ContextUtil.getSearchProcessor(context);
            if (fetchLinkNode.isFromParser() && PatternUtils.match("\\#\\{page,", fetchLinkNode.getUrl())) {
                int pageNum = 1;
                String currentParserLinkUrl = SearchTemplateCombine.customTemplate(fetchLinkNode.getUrl(), pageNum);

                if (!currentParserLinkUrl.equals(fetchLinkNode.getUrl())) {
                    log.info("AddParserTemplateUrlFilter : currentParserLinkUrl : " + currentParserLinkUrl);
                    URLHandlerImpl handler = ContextUtil.getURLHandlerImpl(context);
                    List<LinkNode> parserTemplatelinkNodeList = handler.getTempLinkNodes();

                    LinkNode firsetLinkNode = (LinkNode) GsonUtils.fromJson(GsonUtils.toJson(fetchLinkNode), LinkNode.class);
                    firsetLinkNode.setUrl(currentParserLinkUrl);
                    parserTemplatelinkNodeList.add(firsetLinkNode);

                    String lastParserLinkUrl = "";
                    while (!lastParserLinkUrl.equals(currentParserLinkUrl)) {
                        lastParserLinkUrl = currentParserLinkUrl;
                        currentParserLinkUrl = SearchTemplateCombine.customTemplate(fetchLinkNode.getUrl(), ++pageNum);
                        if (lastParserLinkUrl.equals(currentParserLinkUrl)) {
                            break;
                        }
                        log.info("AddParserTemplateUrlFilter : currentParserLinkUrl : " + currentParserLinkUrl);
                        LinkNode parserLinkNode = (LinkNode) GsonUtils.fromJson(GsonUtils.toJson(fetchLinkNode), LinkNode.class);
                        parserLinkNode.setUrl(currentParserLinkUrl);
                        parserLinkNode.setRemoved(false);
                        parserTemplatelinkNodeList.add(parserLinkNode);
                    }
                }

                // remove parser template linkNode
                fetchLinkNode.setRemoved(true);
            }
            if (!fetchLinkNode.isRemoved()) {
                filterChain.doFilter(context, filterChain);
            } else {
                searchProcessor.getTask().getFilteredCount().getAndIncrement();
            }
        } catch (Exception e) {
            log.error("do add parser templateUrlFilter error " + e.getMessage(), e);
        }
    }

}
