package com.datatrees.crawler.core.processor.common.html.urlspliter;

import java.util.ArrayList;
import java.util.List;

import com.datatrees.common.conf.PropertiesConfiguration;

/**
 * @author <A HREF="mailto:wangcheng@datatrees.com.cn">Cheng Wang</A>
 * @version 1.0
 * @since 2014-12-18 下午7:31:52
 */
public enum DefaultProtocol implements Protocol {

    INSTANCE;
    public static final String       protocolSeparator   = "://";
    private             String       protocols           = PropertiesConfiguration.getInstance().get("url.split.protocols", "http,https,rtmp,qvod,pa,mms,rtsp,thunder,bdhb");
    private             List<String> supportProtocolList = null;

    private DefaultProtocol() {
        createProtocol();
    }

    @Override
    public void createProtocol() {
        supportProtocolList = new ArrayList<String>();
        for (String protocol : protocols.split(",")) {
            supportProtocolList.add(protocol);
        }
    }

    public List<String> getSupportProtocolList() {
        return supportProtocolList;
    }

}
