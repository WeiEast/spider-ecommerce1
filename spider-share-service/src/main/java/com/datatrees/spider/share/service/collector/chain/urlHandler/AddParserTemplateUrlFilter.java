package com.datatrees.spider.share.service.collector.chain.urlHandler;

import java.util.List;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.common.util.PatternUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.datatrees.crawler.core.processor.search.SearchTemplateCombine;
import com.datatrees.spider.share.service.collector.chain.Context;
import com.datatrees.spider.share.service.collector.search.SearchProcessor;
import com.datatrees.spider.share.service.collector.search.URLHandlerImpl;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月29日 上午11:41:34
 */
public class AddParserTemplateUrlFilter extends RemovedFetchLinkNodeFilter {

    @Override
    protected void doProcess(LinkNode fetchLinkNode, SearchProcessor searchProcessor, Context context) {
        if (fetchLinkNode.isFromParser() && PatternUtils.match("\\#\\{page,", fetchLinkNode.getUrl())) {
            int pageNum = 1;
            String currentParserLinkUrl = SearchTemplateCombine.customTemplate(fetchLinkNode.getUrl(), pageNum);

            if (!currentParserLinkUrl.equals(fetchLinkNode.getUrl())) {
                logger.info("currentParserLinkUrl : {}", currentParserLinkUrl);
                URLHandlerImpl handler = context.getURLHandlerImpl();
                List<LinkNode> parserTemplatelinkNodeList = handler.getTempLinkNodes();

                LinkNode firsetLinkNode = GsonUtils.fromJson(GsonUtils.toJson(fetchLinkNode), LinkNode.class);
                firsetLinkNode.setUrl(currentParserLinkUrl);
                parserTemplatelinkNodeList.add(firsetLinkNode);

                String lastParserLinkUrl = "";
                while (!lastParserLinkUrl.equals(currentParserLinkUrl)) {
                    lastParserLinkUrl = currentParserLinkUrl;
                    currentParserLinkUrl = SearchTemplateCombine.customTemplate(fetchLinkNode.getUrl(), ++pageNum);
                    if (lastParserLinkUrl.equals(currentParserLinkUrl)) {
                        break;
                    }
                    logger.info("currentParserLinkUrl : {}", currentParserLinkUrl);
                    LinkNode parserLinkNode = GsonUtils.fromJson(GsonUtils.toJson(fetchLinkNode), LinkNode.class);
                    parserLinkNode.setUrl(currentParserLinkUrl);
                    parserLinkNode.setRemoved(false);
                    parserTemplatelinkNodeList.add(parserLinkNode);
                }
            }

            // remove parser template linkNode
            fetchLinkNode.setRemoved(true);
        }
    }

}
