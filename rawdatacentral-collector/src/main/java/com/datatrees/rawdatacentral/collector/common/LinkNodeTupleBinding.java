package com.datatrees.rawdatacentral.collector.common;

import java.util.Map;

import com.datatrees.common.util.GsonUtils;
import com.datatrees.crawler.core.processor.bean.LinkNode;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;


/**
 *
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2015年7月20日 上午12:45:41
 */
public class LinkNodeTupleBinding extends TupleBinding<LinkNode> {

    @Override
    public LinkNode entryToObject(TupleInput input) {
        LinkNode linkNode = new LinkNode(input.readString());
        linkNode.setDepth(input.readInt());
        linkNode.setRedirectUrl(input.readString());
        linkNode.setRetryCount(input.readInt());
        linkNode.setPageTitle(input.readString());
        linkNode.setReferer(input.readString());
        linkNode.addHeaders((Map<String, String>) GsonUtils.fromJson(input.readString(), Map.class));
        linkNode.addPropertys((Map<String, Object>) GsonUtils.fromJson(input.readString(), Map.class));

        return linkNode;
    }

    @Override
    public void objectToEntry(LinkNode linkNode, TupleOutput output) {
        output.writeString(linkNode.getUrl());
        output.writeInt(linkNode.getDepth());
        output.writeString(linkNode.getRedirectUrl());
        output.writeInt(linkNode.getRetryCount());
        output.writeString(linkNode.getPageTitle());
        output.writeString(linkNode.getReferer());
        output.writeString(GsonUtils.toJson(linkNode.getHeaders()));
        output.writeString(GsonUtils.toJson(linkNode.getPropertys()));
    }
}
