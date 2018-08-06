package com.datatrees.spider.share.service.collector.bdb.operator;

import java.util.LinkedList;

import com.datatrees.crawler.core.processor.bean.LinkNode;

/**
 * @author <A HREF="">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:43:18
 */
public interface Operator {

    LinkedList<LinkNode> fetchNewLinks(int size);

    int addLink(LinkNode link);
}
