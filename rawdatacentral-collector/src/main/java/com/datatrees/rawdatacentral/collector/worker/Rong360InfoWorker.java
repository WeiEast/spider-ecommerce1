/**
 * This document and its contents are protected by copyright 2015 and owned by datatrees.com Inc.
 * The copying and reproduction of this document and/or its content (whether wholly or partly) or
 * any incorporation of the same into any other material in any media or format of any kind is
 * strictly prohibited. All rights are reserved.
 * Copyright (c) datatrees.com Inc. 2016
 */

package com.datatrees.rawdatacentral.collector.worker;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datatrees.crawler.core.util.json.JsonPathUtil;
import com.datatrees.rawdatacentral.collector.actor.Collector;
import com.datatrees.rawdatacentral.core.common.UniqueKeyGenUtil;
import com.datatrees.rawdatacentral.core.model.message.impl.Rong360InfoMessage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author <A HREF="mailto:zhangjiachen@datatrees.com.cn">zhangjiachen</A>
 * @version 1.0
 * @since 2016年9月20日 下午7:53:42
 */
@Service
public class Rong360InfoWorker {

    private static final Logger log = LoggerFactory.getLogger(Rong360InfoWorker.class);
    @Resource
    private Collector collector;

    public void process(Rong360InfoMessage message) {
        message.setSerialNum(UniqueKeyGenUtil.uniqueKeyGen(null));// unique sign
        message.setFinish(false);
        Map<String, Object> propertys = new HashMap<String, Object>();
        propertys.put("content", message.getContent());
        if (message.getWebsiteName().equals("alipay.com_rong360")) {
            String jsonpath = "$.[0].alipay_info[*].alipay_cookie";
            String content = message.getContent();
            String cookie = null;
            List<String> list = JsonPathUtil.readAsList(content, jsonpath);
            if (CollectionUtils.isNotEmpty(list)) {
                cookie = list.get(0);
            }
            message.setCookie(cookie);
        }
        message.setProperty(propertys);
        collector.processMessage(message);
    }
}
