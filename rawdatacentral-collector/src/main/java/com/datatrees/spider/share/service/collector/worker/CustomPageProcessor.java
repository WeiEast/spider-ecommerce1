package com.datatrees.spider.share.service.collector.worker;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;

import akka.dispatch.Future;
import com.datatrees.rawdatacentral.collector.actor.TaskMessage;
import com.datatrees.spider.share.service.collector.actor.TaskMessage;
import com.datatrees.spider.share.service.domain.data.EcommerceData;
import com.treefinance.crawler.framework.extension.spider.PageProcessor;
import com.treefinance.crawler.framework.extension.spider.page.AlipayRecordPage;
import com.treefinance.crawler.framework.extension.spider.page.Page;
import com.treefinance.crawler.framework.extension.spider.page.SimplePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 20:02 27/11/2017
 */
public class CustomPageProcessor implements PageProcessor {

    private static final Logger                                LOGGER  = LoggerFactory.getLogger(CustomPageProcessor.class);

    private final        ConcurrentLinkedQueue<Future<Object>> futures = new ConcurrentLinkedQueue<>();

    private final        ResultDataHandler                     resultDataHandler;

    private final        TaskMessage                           taskMessage;

    public CustomPageProcessor(ResultDataHandler resultDataHandler, TaskMessage taskMessage) {
        this.resultDataHandler = resultDataHandler;
        this.taskMessage = taskMessage;
    }

    @Override
    public void process(Page page) {
        if (page instanceof SimplePage && "EcommerceData".equals(((SimplePage) page).getResultType())) {
            if (page instanceof AlipayRecordPage) {
                doProcessAlipayRecords((AlipayRecordPage) page);
            } else {
                doSimpleExtract(page);
            }
        }
    }

    private void doSimpleExtract(Page page) {
        LOGGER.info("Find ecommerce page: {}", page.getUrl());
        EcommerceData data = new EcommerceData();
        data.setUrl(page.getUrl());
        data.setPageContent(page.getContent());
        futures.addAll(resultDataHandler.resultListHandler(Collections.singletonList(data), taskMessage));
    }

    /**
     * 专门处理支付宝交易记录的爬取结果
     */
    private void doProcessAlipayRecords(AlipayRecordPage page) {
        if (!page.isEnd()) {
            doSimpleExtract(page);
        }

        // TODO: 22/01/2018 save alipay records's crawling info
    }

    public Collection<Future<Object>> getFutures() {
        return futures;
    }

}
